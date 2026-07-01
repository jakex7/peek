@file:PeekComposable

package io.github.jakex7.peek.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.Updater

@Composable
internal fun <T : Emittable> PeekNode(
  factory: () -> T,
  update: @DisallowComposableCalls Updater<T>.() -> Unit,
) {
  ComposeNode<T, PeekApplier>(factory, update)
}

@Composable
internal fun <T : Emittable> PeekNode(
  factory: () -> T,
  update: @DisallowComposableCalls Updater<T>.() -> Unit,
  content: @Composable @PeekComposable () -> Unit,
) {
  ComposeNode<T, PeekApplier>(factory, update, content)
}
