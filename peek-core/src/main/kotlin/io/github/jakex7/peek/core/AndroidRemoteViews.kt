@file:PeekComposable

package io.github.jakex7.peek.core

import android.view.View
import android.widget.RemoteViews
import androidx.annotation.IdRes
import androidx.compose.runtime.Composable
import io.github.jakex7.peek.core.remoteviews.EmittableAndroidRemoteViews

@Composable
fun AndroidRemoteViews(
  remoteViews: RemoteViews,
  modifier: PeekModifier = PeekModifier,
) {
  AndroidRemoteViews(remoteViews, View.NO_ID, modifier) {}
}

@Composable
fun AndroidRemoteViews(
  remoteViews: RemoteViews,
  @IdRes containerViewId: Int,
  modifier: PeekModifier = PeekModifier,
  content: @Composable @PeekComposable () -> Unit,
) {
  PeekNode(
    factory = ::EmittableAndroidRemoteViews,
    update = {
      setModifier(modifier)
      set(remoteViews) { this.remoteViews = it }
      set(containerViewId) { this.containerViewId = it }
    },
    content = content,
  )
}
