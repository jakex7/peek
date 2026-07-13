package io.github.jakex7.peek.core

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class PeekThemeTest {
  @Test
  fun defaultColorsUseAndroidSystemPaletteInDayMode() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    assertEquals(androidColor(context, android.R.color.system_accent1_600), defaultPrimary(context))
    assertEquals(androidColor(context, android.R.color.system_accent1_0), defaultOnPrimary(context))
    assertEquals(androidColor(context, android.R.color.system_accent1_100), defaultPrimaryContainer(context))
    assertEquals(androidColor(context, android.R.color.system_accent1_900), defaultOnPrimaryContainer(context))
    assertEquals(androidColor(context, android.R.color.system_accent2_600), defaultSecondary(context))
    assertEquals(androidColor(context, android.R.color.system_accent2_0), defaultOnSecondary(context))
    assertEquals(androidColor(context, android.R.color.system_accent2_100), defaultSecondaryContainer(context))
    assertEquals(androidColor(context, android.R.color.system_accent2_900), defaultOnSecondaryContainer(context))
    assertEquals(androidColor(context, android.R.color.system_accent3_600), defaultTertiary(context))
    assertEquals(androidColor(context, android.R.color.system_accent3_0), defaultOnTertiary(context))
    assertEquals(androidColor(context, android.R.color.system_accent3_100), defaultTertiaryContainer(context))
    assertEquals(androidColor(context, android.R.color.system_accent3_900), defaultOnTertiaryContainer(context))
    assertEquals(androidColor(context, android.R.color.system_neutral1_10), defaultSurface(context))
    assertEquals(androidColor(context, android.R.color.system_neutral1_900), defaultOnSurface(context))
    assertEquals(androidColor(context, android.R.color.system_neutral2_100), defaultSurfaceVariant(context))
    assertEquals(androidColor(context, android.R.color.system_neutral2_700), defaultOnSurfaceVariant(context))
    assertEquals(androidColor(context, android.R.color.system_neutral2_500), defaultOutline(context))
    assertEquals(androidColor(context, android.R.color.system_neutral2_200), defaultOutlineVariant(context))
    assertEquals(androidColor(context, android.R.color.system_neutral1_800), defaultInverseSurface(context))
    assertEquals(androidColor(context, android.R.color.system_neutral1_50), defaultInverseOnSurface(context))
    assertEquals(androidColor(context, android.R.color.system_accent1_200), defaultInversePrimary(context))
    assertEquals(androidColor(context, android.R.color.system_accent2_50), defaultWidgetBackground(context))
  }

  @Test
  fun defaultColorsUseAndroidSystemPaletteInNightMode() {
    val context = ApplicationProvider.getApplicationContext<Context>().withNightMode()

    assertEquals(androidColor(context, android.R.color.system_accent1_200), defaultPrimary(context))
    assertEquals(androidColor(context, android.R.color.system_accent1_800), defaultOnPrimary(context))
    assertEquals(androidColor(context, android.R.color.system_accent1_700), defaultPrimaryContainer(context))
    assertEquals(androidColor(context, android.R.color.system_accent1_100), defaultOnPrimaryContainer(context))
    assertEquals(androidColor(context, android.R.color.system_accent2_200), defaultSecondary(context))
    assertEquals(androidColor(context, android.R.color.system_accent2_800), defaultOnSecondary(context))
    assertEquals(androidColor(context, android.R.color.system_accent2_700), defaultSecondaryContainer(context))
    assertEquals(androidColor(context, android.R.color.system_accent2_100), defaultOnSecondaryContainer(context))
    assertEquals(androidColor(context, android.R.color.system_accent3_200), defaultTertiary(context))
    assertEquals(androidColor(context, android.R.color.system_accent3_800), defaultOnTertiary(context))
    assertEquals(androidColor(context, android.R.color.system_accent3_700), defaultTertiaryContainer(context))
    assertEquals(androidColor(context, android.R.color.system_accent3_100), defaultOnTertiaryContainer(context))
    assertEquals(androidColor(context, android.R.color.system_neutral1_900), defaultSurface(context))
    assertEquals(androidColor(context, android.R.color.system_neutral1_100), defaultOnSurface(context))
    assertEquals(androidColor(context, android.R.color.system_neutral2_700), defaultSurfaceVariant(context))
    assertEquals(androidColor(context, android.R.color.system_neutral2_200), defaultOnSurfaceVariant(context))
    assertEquals(androidColor(context, android.R.color.system_neutral2_400), defaultOutline(context))
    assertEquals(androidColor(context, android.R.color.system_neutral2_700), defaultOutlineVariant(context))
    assertEquals(androidColor(context, android.R.color.system_neutral1_100), defaultInverseSurface(context))
    assertEquals(androidColor(context, android.R.color.system_neutral1_800), defaultInverseOnSurface(context))
    assertEquals(androidColor(context, android.R.color.system_accent1_600), defaultInversePrimary(context))
    assertEquals(androidColor(context, android.R.color.system_accent2_800), defaultWidgetBackground(context))
  }

  @Test
  fun defaultColorsExposeEveryMaterial3Role() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val colors = PeekThemeDefaults.colors

    listOf(
      colors.primary,
      colors.onPrimary,
      colors.primaryContainer,
      colors.onPrimaryContainer,
      colors.secondary,
      colors.onSecondary,
      colors.secondaryContainer,
      colors.onSecondaryContainer,
      colors.tertiary,
      colors.onTertiary,
      colors.tertiaryContainer,
      colors.onTertiaryContainer,
      colors.error,
      colors.onError,
      colors.errorContainer,
      colors.onErrorContainer,
      colors.background,
      colors.onBackground,
      colors.surface,
      colors.onSurface,
      colors.surfaceVariant,
      colors.onSurfaceVariant,
      colors.outline,
      colors.outlineVariant,
      colors.scrim,
      colors.inverseSurface,
      colors.inverseOnSurface,
      colors.inversePrimary,
      colors.surfaceDim,
      colors.surfaceBright,
      colors.surfaceContainerLowest,
      colors.surfaceContainerLow,
      colors.surfaceContainer,
      colors.surfaceContainerHigh,
      colors.surfaceContainerHighest,
      colors.widgetBackground,
    ).forEach { it.getColor(context) }
  }

  private fun defaultPrimary(context: Context): Int =
    PeekThemeDefaults.colors.primary.getColor(context).toArgb()

  private fun defaultOnPrimary(context: Context): Int =
    PeekThemeDefaults.colors.onPrimary.getColor(context).toArgb()

  private fun defaultPrimaryContainer(context: Context): Int =
    PeekThemeDefaults.colors.primaryContainer.getColor(context).toArgb()

  private fun defaultOnPrimaryContainer(context: Context): Int =
    PeekThemeDefaults.colors.onPrimaryContainer.getColor(context).toArgb()

  private fun defaultSecondary(context: Context): Int =
    PeekThemeDefaults.colors.secondary.getColor(context).toArgb()

  private fun defaultOnSecondary(context: Context): Int =
    PeekThemeDefaults.colors.onSecondary.getColor(context).toArgb()

  private fun defaultSecondaryContainer(context: Context): Int =
    PeekThemeDefaults.colors.secondaryContainer.getColor(context).toArgb()

  private fun defaultOnSecondaryContainer(context: Context): Int =
    PeekThemeDefaults.colors.onSecondaryContainer.getColor(context).toArgb()

  private fun defaultTertiary(context: Context): Int =
    PeekThemeDefaults.colors.tertiary.getColor(context).toArgb()

  private fun defaultOnTertiary(context: Context): Int =
    PeekThemeDefaults.colors.onTertiary.getColor(context).toArgb()

  private fun defaultTertiaryContainer(context: Context): Int =
    PeekThemeDefaults.colors.tertiaryContainer.getColor(context).toArgb()

  private fun defaultOnTertiaryContainer(context: Context): Int =
    PeekThemeDefaults.colors.onTertiaryContainer.getColor(context).toArgb()

  private fun defaultSurface(context: Context): Int =
    PeekThemeDefaults.colors.surface.getColor(context).toArgb()

  private fun defaultOnSurface(context: Context): Int =
    PeekThemeDefaults.colors.onSurface.getColor(context).toArgb()

  private fun defaultSurfaceVariant(context: Context): Int =
    PeekThemeDefaults.colors.surfaceVariant.getColor(context).toArgb()

  private fun defaultOnSurfaceVariant(context: Context): Int =
    PeekThemeDefaults.colors.onSurfaceVariant.getColor(context).toArgb()

  private fun defaultOutline(context: Context): Int =
    PeekThemeDefaults.colors.outline.getColor(context).toArgb()

  private fun defaultOutlineVariant(context: Context): Int =
    PeekThemeDefaults.colors.outlineVariant.getColor(context).toArgb()

  private fun defaultInverseSurface(context: Context): Int =
    PeekThemeDefaults.colors.inverseSurface.getColor(context).toArgb()

  private fun defaultInverseOnSurface(context: Context): Int =
    PeekThemeDefaults.colors.inverseOnSurface.getColor(context).toArgb()

  private fun defaultInversePrimary(context: Context): Int =
    PeekThemeDefaults.colors.inversePrimary.getColor(context).toArgb()

  private fun defaultWidgetBackground(context: Context): Int =
    PeekThemeDefaults.colors.widgetBackground.getColor(context).toArgb()

  private fun androidColor(context: Context, resId: Int): Int =
    Color(context.getColor(resId)).toArgb()

  private fun Context.withNightMode(): Context {
    val configuration =
      Configuration(resources.configuration).apply {
        uiMode =
          (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
             Configuration.UI_MODE_NIGHT_YES
      }
    return createConfigurationContext(configuration)
  }
}
