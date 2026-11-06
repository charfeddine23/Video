package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Video Pulse", appName)
  }

  @Test
  fun `verify hero media item is present with El Sitt Oumour`() {
    val hero = com.example.data.model.MediaRepository.heroItem
    assertEquals("hero_1", hero.id)
    assertEquals("الست أمور", hero.titleArabic)
    assertEquals("Top 01 in TV Shows", hero.subtitleBadge)
  }

  @Test
  fun `verify streak preferences repository persists state across app restart`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.preferences.StreakPreferencesRepository.getInstance(context)

    // Save custom state
    val customState = com.example.ui.viewmodel.StreakUiState(
        streakDays = 21,
        shieldsCount = 25,
        maxShields = 30,
        dailyWatchSeconds = 240,
        dailyGoalSeconds = 300,
        isGoalCompletedToday = false
    )
    repo.saveStreakState(customState)

    // Reload and assert equality
    val loaded = repo.loadInitialState()
    assertEquals(21, loaded.streakDays)
    assertEquals(25, loaded.shieldsCount)
    assertEquals(240, loaded.dailyWatchSeconds)
  }

  @Test
  fun `verify fresh StreakUiState default values are 0 streak, 3 shields, 0s watch time`() {
    val defaultState = com.example.ui.viewmodel.StreakUiState()
    assertEquals(0, defaultState.streakDays)
    assertEquals(3, defaultState.shieldsCount)
    assertEquals(0, defaultState.dailyWatchSeconds)
    assertEquals(300, defaultState.dailyGoalSeconds)
  }
}
