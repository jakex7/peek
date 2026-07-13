@file:PeekComposable

package io.github.jakex7.peek.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

data class PeekColors(
  val primary: ColorProvider,
  val onPrimary: ColorProvider,
  val surface: ColorProvider,
  val onSurface: ColorProvider,
  val primaryContainer: ColorProvider = ResourceColorProvider(R.color.peek_colorPrimaryContainer),
  val onPrimaryContainer: ColorProvider = ResourceColorProvider(R.color.peek_colorOnPrimaryContainer),
  val secondary: ColorProvider = ResourceColorProvider(R.color.peek_colorSecondary),
  val onSecondary: ColorProvider = ResourceColorProvider(R.color.peek_colorOnSecondary),
  val secondaryContainer: ColorProvider = ResourceColorProvider(R.color.peek_colorSecondaryContainer),
  val onSecondaryContainer: ColorProvider = ResourceColorProvider(R.color.peek_colorOnSecondaryContainer),
  val tertiary: ColorProvider = ResourceColorProvider(R.color.peek_colorTertiary),
  val onTertiary: ColorProvider = ResourceColorProvider(R.color.peek_colorOnTertiary),
  val tertiaryContainer: ColorProvider = ResourceColorProvider(R.color.peek_colorTertiaryContainer),
  val onTertiaryContainer: ColorProvider = ResourceColorProvider(R.color.peek_colorOnTertiaryContainer),
  val error: ColorProvider = ResourceColorProvider(R.color.peek_colorError),
  val onError: ColorProvider = ResourceColorProvider(R.color.peek_colorOnError),
  val errorContainer: ColorProvider = ResourceColorProvider(R.color.peek_colorErrorContainer),
  val onErrorContainer: ColorProvider = ResourceColorProvider(R.color.peek_colorOnErrorContainer),
  val background: ColorProvider = ResourceColorProvider(R.color.peek_colorBackground),
  val onBackground: ColorProvider = ResourceColorProvider(R.color.peek_colorOnBackground),
  val surfaceVariant: ColorProvider = ResourceColorProvider(R.color.peek_colorSurfaceVariant),
  val onSurfaceVariant: ColorProvider = ResourceColorProvider(R.color.peek_colorOnSurfaceVariant),
  val outline: ColorProvider = ResourceColorProvider(R.color.peek_colorOutline),
  val outlineVariant: ColorProvider = ResourceColorProvider(R.color.peek_colorOutlineVariant),
  val scrim: ColorProvider = ResourceColorProvider(R.color.peek_colorScrim),
  val inverseSurface: ColorProvider = ResourceColorProvider(R.color.peek_colorInverseSurface),
  val inverseOnSurface: ColorProvider = ResourceColorProvider(R.color.peek_colorInverseOnSurface),
  val inversePrimary: ColorProvider = ResourceColorProvider(R.color.peek_colorInversePrimary),
  val surfaceDim: ColorProvider = ResourceColorProvider(R.color.peek_colorSurfaceDim),
  val surfaceBright: ColorProvider = ResourceColorProvider(R.color.peek_colorSurfaceBright),
  val surfaceContainerLowest: ColorProvider = ResourceColorProvider(R.color.peek_colorSurfaceContainerLowest),
  val surfaceContainerLow: ColorProvider = ResourceColorProvider(R.color.peek_colorSurfaceContainerLow),
  val surfaceContainer: ColorProvider = ResourceColorProvider(R.color.peek_colorSurfaceContainer),
  val surfaceContainerHigh: ColorProvider = ResourceColorProvider(R.color.peek_colorSurfaceContainerHigh),
  val surfaceContainerHighest: ColorProvider = ResourceColorProvider(R.color.peek_colorSurfaceContainerHighest),
  val widgetBackground: ColorProvider = ResourceColorProvider(R.color.peek_colorWidgetBackground),
)

object PeekThemeDefaults {
  val colors: PeekColors =
    PeekColors(
      primary = ResourceColorProvider(R.color.peek_colorPrimary),
      onPrimary = ResourceColorProvider(R.color.peek_colorOnPrimary),
      primaryContainer = ResourceColorProvider(R.color.peek_colorPrimaryContainer),
      onPrimaryContainer = ResourceColorProvider(R.color.peek_colorOnPrimaryContainer),
      secondary = ResourceColorProvider(R.color.peek_colorSecondary),
      onSecondary = ResourceColorProvider(R.color.peek_colorOnSecondary),
      secondaryContainer = ResourceColorProvider(R.color.peek_colorSecondaryContainer),
      onSecondaryContainer = ResourceColorProvider(R.color.peek_colorOnSecondaryContainer),
      tertiary = ResourceColorProvider(R.color.peek_colorTertiary),
      onTertiary = ResourceColorProvider(R.color.peek_colorOnTertiary),
      tertiaryContainer = ResourceColorProvider(R.color.peek_colorTertiaryContainer),
      onTertiaryContainer = ResourceColorProvider(R.color.peek_colorOnTertiaryContainer),
      error = ResourceColorProvider(R.color.peek_colorError),
      onError = ResourceColorProvider(R.color.peek_colorOnError),
      errorContainer = ResourceColorProvider(R.color.peek_colorErrorContainer),
      onErrorContainer = ResourceColorProvider(R.color.peek_colorOnErrorContainer),
      background = ResourceColorProvider(R.color.peek_colorBackground),
      onBackground = ResourceColorProvider(R.color.peek_colorOnBackground),
      surface = ResourceColorProvider(R.color.peek_colorSurface),
      onSurface = ResourceColorProvider(R.color.peek_colorOnSurface),
      surfaceVariant = ResourceColorProvider(R.color.peek_colorSurfaceVariant),
      onSurfaceVariant = ResourceColorProvider(R.color.peek_colorOnSurfaceVariant),
      outline = ResourceColorProvider(R.color.peek_colorOutline),
      outlineVariant = ResourceColorProvider(R.color.peek_colorOutlineVariant),
      scrim = ResourceColorProvider(R.color.peek_colorScrim),
      inverseSurface = ResourceColorProvider(R.color.peek_colorInverseSurface),
      inverseOnSurface = ResourceColorProvider(R.color.peek_colorInverseOnSurface),
      inversePrimary = ResourceColorProvider(R.color.peek_colorInversePrimary),
      surfaceDim = ResourceColorProvider(R.color.peek_colorSurfaceDim),
      surfaceBright = ResourceColorProvider(R.color.peek_colorSurfaceBright),
      surfaceContainerLowest = ResourceColorProvider(R.color.peek_colorSurfaceContainerLowest),
      surfaceContainerLow = ResourceColorProvider(R.color.peek_colorSurfaceContainerLow),
      surfaceContainer = ResourceColorProvider(R.color.peek_colorSurfaceContainer),
      surfaceContainerHigh = ResourceColorProvider(R.color.peek_colorSurfaceContainerHigh),
      surfaceContainerHighest = ResourceColorProvider(R.color.peek_colorSurfaceContainerHighest),
      widgetBackground = ResourceColorProvider(R.color.peek_colorWidgetBackground),
    )
}

fun peekColors(
  primary: ColorProvider = PeekThemeDefaults.colors.primary,
  onPrimary: ColorProvider = PeekThemeDefaults.colors.onPrimary,
  primaryContainer: ColorProvider = PeekThemeDefaults.colors.primaryContainer,
  onPrimaryContainer: ColorProvider = PeekThemeDefaults.colors.onPrimaryContainer,
  secondary: ColorProvider = PeekThemeDefaults.colors.secondary,
  onSecondary: ColorProvider = PeekThemeDefaults.colors.onSecondary,
  secondaryContainer: ColorProvider = PeekThemeDefaults.colors.secondaryContainer,
  onSecondaryContainer: ColorProvider = PeekThemeDefaults.colors.onSecondaryContainer,
  tertiary: ColorProvider = PeekThemeDefaults.colors.tertiary,
  onTertiary: ColorProvider = PeekThemeDefaults.colors.onTertiary,
  tertiaryContainer: ColorProvider = PeekThemeDefaults.colors.tertiaryContainer,
  onTertiaryContainer: ColorProvider = PeekThemeDefaults.colors.onTertiaryContainer,
  error: ColorProvider = PeekThemeDefaults.colors.error,
  onError: ColorProvider = PeekThemeDefaults.colors.onError,
  errorContainer: ColorProvider = PeekThemeDefaults.colors.errorContainer,
  onErrorContainer: ColorProvider = PeekThemeDefaults.colors.onErrorContainer,
  background: ColorProvider = PeekThemeDefaults.colors.background,
  onBackground: ColorProvider = PeekThemeDefaults.colors.onBackground,
  surface: ColorProvider = PeekThemeDefaults.colors.surface,
  onSurface: ColorProvider = PeekThemeDefaults.colors.onSurface,
  surfaceVariant: ColorProvider = PeekThemeDefaults.colors.surfaceVariant,
  onSurfaceVariant: ColorProvider = PeekThemeDefaults.colors.onSurfaceVariant,
  outline: ColorProvider = PeekThemeDefaults.colors.outline,
  outlineVariant: ColorProvider = PeekThemeDefaults.colors.outlineVariant,
  scrim: ColorProvider = PeekThemeDefaults.colors.scrim,
  inverseSurface: ColorProvider = PeekThemeDefaults.colors.inverseSurface,
  inverseOnSurface: ColorProvider = PeekThemeDefaults.colors.inverseOnSurface,
  inversePrimary: ColorProvider = PeekThemeDefaults.colors.inversePrimary,
  surfaceDim: ColorProvider = PeekThemeDefaults.colors.surfaceDim,
  surfaceBright: ColorProvider = PeekThemeDefaults.colors.surfaceBright,
  surfaceContainerLowest: ColorProvider = PeekThemeDefaults.colors.surfaceContainerLowest,
  surfaceContainerLow: ColorProvider = PeekThemeDefaults.colors.surfaceContainerLow,
  surfaceContainer: ColorProvider = PeekThemeDefaults.colors.surfaceContainer,
  surfaceContainerHigh: ColorProvider = PeekThemeDefaults.colors.surfaceContainerHigh,
  surfaceContainerHighest: ColorProvider = PeekThemeDefaults.colors.surfaceContainerHighest,
  widgetBackground: ColorProvider = PeekThemeDefaults.colors.widgetBackground,
): PeekColors =
  PeekColors(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    error = error,
    onError = onError,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    outline = outline,
    outlineVariant = outlineVariant,
    scrim = scrim,
    inverseSurface = inverseSurface,
    inverseOnSurface = inverseOnSurface,
    inversePrimary = inversePrimary,
    surfaceDim = surfaceDim,
    surfaceBright = surfaceBright,
    surfaceContainerLowest = surfaceContainerLowest,
    surfaceContainerLow = surfaceContainerLow,
    surfaceContainer = surfaceContainer,
    surfaceContainerHigh = surfaceContainerHigh,
    surfaceContainerHighest = surfaceContainerHighest,
    widgetBackground = widgetBackground,
  )

object PeekTheme {
  val colors: PeekColors
    @Composable
    @ReadOnlyComposable
    get() = LocalPeekColors.current
}

@Composable
fun PeekTheme(
  colors: PeekColors = PeekTheme.colors,
  content: @Composable @PeekComposable () -> Unit,
) {
  CompositionLocalProvider(LocalPeekColors provides colors, content = content)
}

private val LocalPeekColors =
  staticCompositionLocalOf { PeekThemeDefaults.colors }
