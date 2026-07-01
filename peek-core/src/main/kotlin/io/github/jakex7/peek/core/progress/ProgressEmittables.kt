package io.github.jakex7.peek.core.progress

import io.github.jakex7.peek.core.ColorProvider
import io.github.jakex7.peek.core.Emittable
import io.github.jakex7.peek.core.PeekModifier
import io.github.jakex7.peek.core.PeekThemeDefaults

class EmittableLinearProgressIndicator : Emittable {
  override var modifier: PeekModifier = PeekModifier
  var progress: Float? = null
  var color: ColorProvider = PeekThemeDefaults.colors.primary
  var trackColor: ColorProvider = PeekThemeDefaults.colors.surfaceContainerHighest

  override fun copy(): Emittable =
    EmittableLinearProgressIndicator().also {
      it.modifier = modifier
      it.progress = progress
      it.color = color
      it.trackColor = trackColor
    }
}

class EmittableCircularProgressIndicator : Emittable {
  override var modifier: PeekModifier = PeekModifier
  var progress: Float? = null
  var color: ColorProvider = PeekThemeDefaults.colors.primary
  var trackColor: ColorProvider = PeekThemeDefaults.colors.surfaceContainerHighest

  override fun copy(): Emittable =
    EmittableCircularProgressIndicator().also {
      it.modifier = modifier
      it.progress = progress
      it.color = color
      it.trackColor = trackColor
    }
}
