@file:PeekComposable

package io.github.jakex7.peek.core

import androidx.compose.runtime.Composable
import io.github.jakex7.peek.core.progress.EmittableCircularProgressIndicator
import io.github.jakex7.peek.core.progress.EmittableLinearProgressIndicator

@Composable
fun LinearProgressIndicator(
  progress: Float? = null,
  modifier: PeekModifier = PeekModifier,
  color: ColorProvider = PeekTheme.colors.primary,
  trackColor: ColorProvider = PeekTheme.colors.surfaceContainerHighest,
) {
  PeekNode(
    factory = ::EmittableLinearProgressIndicator,
    update = {
      setModifier(modifier)
      set(progress) { this.progress = it }
      set(color) { this.color = it }
      set(trackColor) { this.trackColor = it }
    },
  )
}

@Composable
fun CircularProgressIndicator(
  progress: Float? = null,
  modifier: PeekModifier = PeekModifier,
  color: ColorProvider = PeekTheme.colors.primary,
  trackColor: ColorProvider = PeekTheme.colors.surfaceContainerHighest,
) {
  PeekNode(
    factory = ::EmittableCircularProgressIndicator,
    update = {
      setModifier(modifier)
      set(progress) { this.progress = it }
      set(color) { this.color = it }
      set(trackColor) { this.trackColor = it }
    },
  )
}
