@file:Suppress(
  "INVISIBLE_MEMBER",
  "INVISIBLE_REFERENCE",
  "RestrictedApiAndroidX",
)

package io.github.jakex7.peek.glance

import android.graphics.Paint
import androidx.compose.remote.creation.ComponentHeight
import androidx.compose.remote.creation.ComponentWidth
import androidx.compose.remote.creation.RemoteComposeWriterAndroid
import androidx.compose.remote.creation.modifiers.RecordingModifier
import androidx.compose.ui.graphics.toArgb
import androidx.glance.appwidget.remotecompose.TranslationContext
import androidx.glance.appwidget.remotecompose.components.RcElement
import androidx.glance.appwidget.remotecompose.convertGlanceModifierToRemoteComposeModifier

internal class RcDeterminateCircularProgressIndicator(
  emittable: EmittableDeterminateCircularProgressIndicator,
  translationContext: TranslationContext,
) : RcElement(translationContext) {
  override val outputModifier: RecordingModifier

  init {
    val density = translationContext.context.resources.displayMetrics.density
    val strokeWidth = DefaultStrokeWidthDp * density
    val indicatorArgb = emittable.color.getColor(translationContext.context).toArgb()
    val trackArgb = emittable.trackColor.getColor(translationContext.context).toArgb()
    val sweep = normalizeProgress(emittable.progress) * FullSweepDegrees

    outputModifier =
      convertGlanceModifierToRemoteComposeModifier(
        modifiers = emittable.modifier,
        translationContext = translationContext,
      ).drawWithContent { writer ->
        val diameter = writer.ComponentWidth().min(writer.ComponentHeight())
        val inset = strokeWidth / 2f
        val left = (writer.ComponentWidth() - diameter) / 2f + inset
        val top = (writer.ComponentHeight() - diameter) / 2f + inset
        val right = left + diameter - strokeWidth
        val bottom = top + diameter - strokeWidth

        val painter = (writer as RemoteComposeWriterAndroid).painter
        painter
          .setStyle(Paint.Style.STROKE)
          .setStrokeCap(Paint.Cap.ROUND)
          .setStrokeWidth(strokeWidth)
          .setColor(trackArgb)
          .commit()
        writer.drawArc(
          left.toFloat(),
          top.toFloat(),
          right.toFloat(),
          bottom.toFloat(),
          StartAngleDegrees,
          FullSweepDegrees,
        )
        painter.setColor(indicatorArgb).commit()
        writer.drawArc(
          left.toFloat(),
          top.toFloat(),
          right.toFloat(),
          bottom.toFloat(),
          StartAngleDegrees,
          sweep,
        )
      }
  }

  override fun writeComponent(translationContext: TranslationContext) {
    translationContext.remoteComposeContext.box(outputModifier)
  }

  private companion object {
    const val DefaultStrokeWidthDp = 4f
    const val StartAngleDegrees = -90f
    const val FullSweepDegrees = 360f
  }
}
