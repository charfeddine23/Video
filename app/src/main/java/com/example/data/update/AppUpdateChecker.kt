package com.example.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AppUpdateInfo(
    val versionName: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val publishedAt: String? = null
)

sealed class UpdateCheckResult {
    data class UpdateAvailable(val updateInfo: AppUpdateInfo) : UpdateCheckResult()
    object UpToDate : UpdateCheckResult()
    data class Error(val message: String) : UpdateCheckResult()
}

object AppUpdateChecker {
    const val REPO_URL = "https://github.com/charfeddine23/Video"
    private const val RELEASES_API_URL = "https://api.github.com/repos/charfeddine23/Video/releases/latest"
    private const val TAGS_API_URL = "https://api.github.com/repos/charfeddine23/Video/tags"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .build()
    }

    suspend fun checkForUpdates(
        currentVersion: String = BuildConfig.VERSION_NAME
    ): UpdateCheckResult = withContext(Dispatchers.IO) {
        try {
            val releaseRequest = Request.Builder()
                .url(RELEASES_API_URL)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "AuraPulse-Android/${BuildConfig.VERSION_NAME}")
                .build()

            client.newCall(releaseRequest).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string().orEmpty()
                    if (body.isNotBlank()) {
                        val json = JSONObject(body)
                        val tagName = json.optString("tag_name", "").trim()
                        val cleanRemote = normalizeVersion(tagName)
                        if (cleanRemote.isNotEmpty()) {
                            return@withContext if (isNewerVersion(cleanRemote, currentVersion)) {
                                val releaseTitle = json.optString("name", "").ifBlank { "v$cleanRemote" }
                                val notes = json.optString("body", "").trim()
                                val htmlUrl = json.optString("html_url", "").ifBlank { "$REPO_URL/releases/latest" }
                                val publishedAt = json.optString("published_at", "").takeIf { it.isNotBlank() }
                                UpdateCheckResult.UpdateAvailable(
                                    AppUpdateInfo(
                                        versionName = cleanRemote,
                                        releaseTitle = releaseTitle,
                                        releaseNotes = notes,
                                        downloadUrl = htmlUrl,
                                        publishedAt = publishedAt
                                    )
                                )
                            } else {
                                UpdateCheckResult.UpToDate
                            }
                        }
                    }
                }
            }

            // Fallback: check repository tags if no formal GitHub Release has been published yet
            val tagsRequest = Request.Builder()
                .url(TAGS_API_URL)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "AuraPulse-Android/${BuildConfig.VERSION_NAME}")
                .build()

            client.newCall(tagsRequest).execute().use { tagsResponse ->
                if (tagsResponse.isSuccessful) {
                    val body = tagsResponse.body?.string().orEmpty()
                    val tagsArray = JSONArray(body)
                    if (tagsArray.length() > 0) {
                        val latestTagObj = tagsArray.getJSONObject(0)
                        val tagName = latestTagObj.optString("name", "").trim()
                        val cleanRemote = normalizeVersion(tagName)
                        if (cleanRemote.isNotEmpty()) {
                            return@withContext if (isNewerVersion(cleanRemote, currentVersion)) {
                                UpdateCheckResult.UpdateAvailable(
                                    AppUpdateInfo(
                                        versionName = cleanRemote,
                                        releaseTitle = "v$cleanRemote",
                                        releaseNotes = "",
                                        downloadUrl = "$REPO_URL/releases/tag/$tagName"
                                    )
                                )
                            } else {
                                UpdateCheckResult.UpToDate
                            }
                        }
                    }
                    return@withContext UpdateCheckResult.UpToDate
                } else {
                    return@withContext UpdateCheckResult.Error("HTTP ${tagsResponse.code}")
                }
            }
        } catch (e: Exception) {
            UpdateCheckResult.Error(e.localizedMessage ?: "Network unavailable")
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

    fun openUpdateUrl(context: Context, url: String = REPO_URL) {
        val target = url.ifBlank { REPO_URL }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(target)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }
}
