package com.example.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

data class AppUpdateInfo(
    val versionName: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val apkDownloadUrl: String?,
    val apkFileName: String,
    val apkSizeBytes: Long = -1L,
    val releasePageUrl: String,
    val publishedAt: String? = null
)

sealed class UpdateCheckResult {
    data class UpdateAvailable(val updateInfo: AppUpdateInfo) : UpdateCheckResult()
    object UpToDate : UpdateCheckResult()
    data class Error(val message: String) : UpdateCheckResult()
}

sealed class ApkDownloadState {
    data class Downloading(
        val bytesDownloaded: Long,
        val totalBytes: Long,
        val progress: Float
    ) : ApkDownloadState()

    data class Completed(val apkFile: File) : ApkDownloadState()
    data class Error(val message: String) : ApkDownloadState()
}

enum class ApkInstallLaunchResult {
    LAUNCHED,
    NEEDS_INSTALL_PERMISSION,
    FILE_MISSING,
    FAILED
}

object AppUpdateChecker {
    const val REPO_URL = "https://github.com/charfeddine23/Video"
    private const val RELEASES_API_URL = "https://api.github.com/repos/charfeddine23/Video/releases/latest"
    private const val RELEASES_LIST_API_URL = "https://api.github.com/repos/charfeddine23/Video/releases?per_page=5"

    private val apiClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val downloadClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    suspend fun checkForUpdates(
        currentVersion: String = BuildConfig.VERSION_NAME
    ): UpdateCheckResult = withContext(Dispatchers.IO) {
        try {
            val latestRequest = Request.Builder()
                .url(RELEASES_API_URL)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "AuraPulse-Android/${BuildConfig.VERSION_NAME}")
                .build()

            apiClient.newCall(latestRequest).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string().orEmpty()
                    if (body.isNotBlank()) {
                        val json = JSONObject(body)
                        parseReleaseObject(json, currentVersion)?.let { return@withContext it }
                    }
                }
            }

            // Fallback: inspect recent releases list in case latest release is tagged differently
            val listRequest = Request.Builder()
                .url(RELEASES_LIST_API_URL)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "AuraPulse-Android/${BuildConfig.VERSION_NAME}")
                .build()

            apiClient.newCall(listRequest).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string().orEmpty()
                    val array = JSONArray(body)
                    for (i in 0 until array.length()) {
                        val releaseObj = array.getJSONObject(i)
                        if (releaseObj.optBoolean("draft", false)) continue
                        parseReleaseObject(releaseObj, currentVersion)?.let { result ->
                            return@withContext result
                        }
                        break
                    }
                    return@withContext UpdateCheckResult.UpToDate
                } else {
                    return@withContext UpdateCheckResult.Error("HTTP ${response.code}")
                }
            }
        } catch (e: Exception) {
            UpdateCheckResult.Error(e.localizedMessage ?: "Network unavailable")
        }
    }

    private fun parseReleaseObject(
        json: JSONObject,
        currentVersion: String
    ): UpdateCheckResult? {
        val tagName = json.optString("tag_name", "").trim()
        val cleanRemote = normalizeVersion(tagName)
        if (cleanRemote.isEmpty()) return null

        if (!isNewerVersion(cleanRemote, currentVersion)) {
            return UpdateCheckResult.UpToDate
        }

        val releaseTitle = json.optString("name", "").ifBlank { "v$cleanRemote" }
        val notes = json.optString("body", "").trim()
        val htmlUrl = json.optString("html_url", "").ifBlank { "$REPO_URL/releases/latest" }
        val publishedAt = json.optString("published_at", "").takeIf { it.isNotBlank() }

        var apkUrl: String? = null
        var apkName = "Video.Pulse.v$cleanRemote.apk"
        var apkSize = -1L

        val assets = json.optJSONArray("assets")
        if (assets != null) {
            for (i in 0 until assets.length()) {
                val asset = assets.optJSONObject(i) ?: continue
                val name = asset.optString("name", "")
                val download = asset.optString("browser_download_url", "")
                if (name.endsWith(".apk", ignoreCase = true) && download.isNotBlank()) {
                    apkUrl = download
                    apkName = name
                    apkSize = asset.optLong("size", -1L)
                    break
                }
            }
        }

        return UpdateCheckResult.UpdateAvailable(
            AppUpdateInfo(
                versionName = cleanRemote,
                releaseTitle = releaseTitle,
                releaseNotes = notes,
                apkDownloadUrl = apkUrl,
                apkFileName = apkName,
                apkSizeBytes = apkSize,
                releasePageUrl = htmlUrl,
                publishedAt = publishedAt
            )
        )
    }

    /**
     * Downloads the release APK completely inside the app into cacheDir/updates/
     * without launching any external browser or external download manager.
     */
    fun downloadUpdateApk(
        context: Context,
        updateInfo: AppUpdateInfo
    ): Flow<ApkDownloadState> = flow {
        val url = updateInfo.apkDownloadUrl?.trim().orEmpty()
        if (url.isBlank()) {
            emit(ApkDownloadState.Error("APK download asset is not attached to this release yet."))
            return@flow
        }

        val updatesDir = File(context.cacheDir, "updates").apply {
            if (!exists()) mkdirs()
        }

        // Remove stale APK files from previous update checks
        updatesDir.listFiles()?.forEach { oldFile ->
            runCatching { oldFile.delete() }
        }

        val safeName = updateInfo.apkFileName
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
            .ifBlank { "Video.Pulse.v${updateInfo.versionName}.apk" }
        val tempFile = File(updatesDir, "$safeName.tmp")
        val finalFile = File(updatesDir, safeName)

        try {
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/octet-stream")
                .header("User-Agent", "AuraPulse-Android/${BuildConfig.VERSION_NAME}")
                .build()

            downloadClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    emit(ApkDownloadState.Error("HTTP ${response.code}"))
                    return@flow
                }

                val body = response.body ?: run {
                    emit(ApkDownloadState.Error("Empty response body"))
                    return@flow
                }

                val contentLength = body.contentLength()
                val totalBytes = if (contentLength > 0L) contentLength else updateInfo.apkSizeBytes

                var downloaded = 0L
                var lastEmitTime = 0L
                var lastPercent = -1

                emit(ApkDownloadState.Downloading(0L, totalBytes, 0f))

                body.byteStream().use { input ->
                    FileOutputStream(tempFile).use { output ->
                        val buffer = ByteArray(16 * 1024)
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            coroutineContext.ensureActive()
                            output.write(buffer, 0, read)
                            downloaded += read

                            val now = System.currentTimeMillis()
                            val progress = if (totalBytes > 0L) {
                                (downloaded.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                            } else {
                                -1f
                            }
                            val pct = if (progress >= 0f) (progress * 100).toInt() else -1

                            if (now - lastEmitTime >= 80L || pct != lastPercent) {
                                lastEmitTime = now
                                lastPercent = pct
                                emit(
                                    ApkDownloadState.Downloading(
                                        bytesDownloaded = downloaded,
                                        totalBytes = totalBytes,
                                        progress = progress
                                    )
                                )
                            }
                        }
                        output.flush()
                    }
                }

                if (!tempFile.exists() || tempFile.length() < 1024L) {
                    tempFile.delete()
                    emit(ApkDownloadState.Error("Downloaded APK file is incomplete"))
                    return@flow
                }

                if (finalFile.exists()) finalFile.delete()
                if (!tempFile.renameTo(finalFile)) {
                    tempFile.copyTo(finalFile, overwrite = true)
                    tempFile.delete()
                }

                emit(ApkDownloadState.Downloading(downloaded, if (totalBytes > 0L) totalBytes else downloaded, 1f))
                emit(ApkDownloadState.Completed(finalFile))
            }
        } catch (e: Exception) {
            runCatching { tempFile.delete() }
            emit(ApkDownloadState.Error(e.localizedMessage ?: "Download failed"))
        }
    }.flowOn(Dispatchers.IO)

    fun canInstallPackages(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun openInstallPermissionSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            runCatching { context.startActivity(intent) }
        }
    }

    fun launchApkInstaller(context: Context, apkFile: File): ApkInstallLaunchResult {
        if (!apkFile.exists() || apkFile.length() <= 0L) {
            return ApkInstallLaunchResult.FILE_MISSING
        }
        if (!canInstallPackages(context)) {
            return ApkInstallLaunchResult.NEEDS_INSTALL_PERMISSION
        }

        return try {
            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            }
            context.startActivity(installIntent)
            ApkInstallLaunchResult.LAUNCHED
        } catch (_: Exception) {
            ApkInstallLaunchResult.FAILED
        }
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0L) return ""
        val mb = bytes.toDouble() / (1024.0 * 1024.0)
        return if (mb >= 1.0) {
            String.format(Locale.US, "%.1f MB", mb)
        } else {
            val kb = bytes.toDouble() / 1024.0
            String.format(Locale.US, "%.0f KB", kb)
        }
    }

    fun normalizeVersion(raw: String): String {
        return raw.trim()
            .removePrefix("v")
            .removePrefix("V")
            .substringBefore("-")
            .trim()
    }

    fun isNewerVersion(remoteVersion: String, localVersion: String): Boolean {
        val remoteParts = normalizeVersion(remoteVersion)
            .split(".")
            .map { it.filter(Char::isDigit).toIntOrNull() ?: 0 }
        val localParts = normalizeVersion(localVersion)
            .split(".")
            .map { it.filter(Char::isDigit).toIntOrNull() ?: 0 }

        val maxLen = maxOf(remoteParts.size, localParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val l = localParts.getOrElse(i) { 0 }
            if (r > l) return true
            if (r < l) return false
        }
        return false
    }
}
