@file:PeekComposable

package io.github.jakex7.peek.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Updater
import io.github.jakex7.peek.core.layout.EmittableBox
import io.github.jakex7.peek.core.layout.EmittableColumn
import io.github.jakex7.peek.core.layout.EmittableRow
import io.github.jakex7.peek.core.layout.EmittableSpacer

@Composable
fun Box(
  modifier: PeekModifier = PeekModifier,
  contentAlignment: Alignment = Alignment.TopStart,
  content: @Composable @PeekComposable () -> Unit,
) {
  PeekNode(
    factory = ::EmittableBox,
    update = {
      setModifier(modifier)
      set(contentAlignment) { this.contentAlignment = it }
    },
    content = content,
  )
}

@Composable
fun Row(
  modifier: PeekModifier = PeekModifier,
  horizontalAlignment: Alignment.Horizontal = Alignment.Start,
  verticalAlignment: Alignment.Vertical = Alignment.Top,
  content: @Composable @PeekComposable () -> Unit,
) {
  PeekNode(
    factory = ::EmittableRow,
    update = {
      setModifier(modifier)
      set(horizontalAlignment) { this.horizontalAlignment = it }
      set(verticalAlignment) { this.verticalAlignment = it }
    },
    content = content,
  )
}

@Composable
fun Column(
  modifier: PeekModifier = PeekModifier,
  verticalAlignment: Alignment.Vertical = Alignment.Top,
  horizontalAlignment: Alignment.Horizontal = Alignment.Start,
  content: @Composable @PeekComposable () -> Unit,
) {
  PeekNode(
    factory = ::EmittableColumn,
    update = {
      setModifier(modifier)
      set(verticalAlignment) { this.verticalAlignment = it }
      set(horizontalAlignment) { this.horizontalAlignment = it }
    },
    content = content,
  )
}

@Composable
fun Spacer(modifier: PeekModifier = PeekModifier) {
  PeekNode(
    factory = ::EmittableSpacer,
    update = { setModifier(modifier) },
  )
}

internal fun <T : Emittable> Updater<T>.setModifier(modifier: PeekModifier) {
  set(modifier) { this.modifier = it }
}
