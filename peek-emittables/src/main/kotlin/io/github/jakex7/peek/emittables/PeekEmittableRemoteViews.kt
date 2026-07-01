@file:Suppress("RestrictedApi")

package io.github.jakex7.peek.emittables

import android.content.Context
import android.widget.RemoteViews
import io.github.jakex7.peek.remoteviews.PeekRemoteViews

object PeekEmittableRemoteViews {
  fun render(
    context: Context,
    root: PeekRoot,
  ): RemoteViews =
    PeekRemoteViews.render(context, root)

  fun renderAppWidget(
    context: Context,
    root: PeekRoot,
  ): RemoteViews =
    PeekRemoteViews.renderAppWidget(context, root)
}
