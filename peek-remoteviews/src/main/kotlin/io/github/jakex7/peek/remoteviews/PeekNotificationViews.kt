package io.github.jakex7.peek.remoteviews

import android.widget.RemoteViews

data class PeekNotificationViews(
  val collapsed: RemoteViews,
  val expanded: RemoteViews? = null,
  val headsUp: RemoteViews? = null,
)
