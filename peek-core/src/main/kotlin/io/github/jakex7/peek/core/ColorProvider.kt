package io.github.jakex7.peek.core

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.graphics.Color

interface ColorProvider {
  fun getColor(context: Context): Color
}

fun ColorProvider(color: Color): ColorProvider =
  FixedColorProvider(color)

fun ColorProvider(day: Color, night: Color): ColorProvider =
  DayNightColorProvider(day = day, night = night)

fun ColorProvider.withAlpha(alpha: Float): ColorProvider =
  AlphaColorProvider(source = this, alpha = alpha)

data class FixedColorProvider(val color: Color) : ColorProvider {
  override fun getColor(context: Context): Color = color
}

internal data class ResourceColorProvider(val resId: Int) : ColorProvider {
  override fun getColor(context: Context): Color =
    Color(context.getColor(resId))
}

data class DayNightColorProvider(
  val day: Color,
  val night: Color,
) : ColorProvider {
  override fun getColor(context: Context): Color =
    if (context.isNightMode) night else day
}

private data class AlphaColorProvider(
  val source: ColorProvider,
  val alpha: Float,
) : ColorProvider {
  override fun getColor(context: Context): Color =
    source.getColor(context).copy(alpha = alpha)
}

internal val Context.isNightMode: Boolean
  get() =
    (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
       Configuration.UI_MODE_NIGHT_YES
