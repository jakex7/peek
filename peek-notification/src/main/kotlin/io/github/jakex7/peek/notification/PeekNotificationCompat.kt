package io.github.jakex7.peek.notification

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.core.app.NotificationCompat
import io.github.jakex7.peek.core.PeekComposable
import io.github.jakex7.peek.remoteviews.PeekNotificationSize
import io.github.jakex7.peek.remoteviews.PeekNotificationViews
import io.github.jakex7.peek.remoteviews.PeekRemoteViews

/**
 * Composes the collapsed/expanded/heads-up notification surfaces.
 *
 * Main-safe: composition runs off the calling thread. Call from a coroutine and pass the result to
 * [setPeekContent], or use the [setPeekContent] overload that takes the slot composables directly.
 * Surfaces are composed sequentially.
 */
suspend fun peekNotificationViews(
  context: Context,
  collapsed: @Composable @PeekComposable () -> Unit,
  expanded: (@Composable @PeekComposable () -> Unit)? = null,
  headsUp: (@Composable @PeekComposable () -> Unit)? = null,
): PeekNotificationViews =
  PeekNotificationViews(
    collapsed = PeekRemoteViews.render(context, PeekNotificationSize.Collapsed, collapsed),
    expanded = expanded?.let {
      PeekRemoteViews.render(context, PeekNotificationSize.Expanded, it)
    },
    headsUp = headsUp?.let {
      PeekRemoteViews.render(context, PeekNotificationSize.HeadsUp, it)
    },
  )

/**
 * Composes Peek [content] into the notification and attaches it.
 *
 * Main-safe: composition runs off the calling thread, so this may be called from a coroutine on the
 * main thread without blocking it.
 */
suspend fun NotificationCompat.Builder.setPeekContent(
  context: Context,
  collapsed: @Composable @PeekComposable () -> Unit,
  expanded: (@Composable @PeekComposable () -> Unit)? = null,
  headsUp: (@Composable @PeekComposable () -> Unit)? = null,
  decorated: Boolean = true,
  ongoing: Boolean = true,
  actions: List<PeekNotificationAction> = emptyList(),
): NotificationCompat.Builder =
  setPeekContent(
    views = peekNotificationViews(context, collapsed, expanded, headsUp),
    decorated = decorated,
    ongoing = ongoing,
    actions = actions,
  )

/**
 * Attaches already-composed [views] to the notification.
 *
 * Synchronous; performs no composition. Build [views] with [peekNotificationViews] from a coroutine.
 */
fun NotificationCompat.Builder.setPeekContent(
  views: PeekNotificationViews,
  decorated: Boolean = true,
  ongoing: Boolean = true,
  actions: List<PeekNotificationAction> = emptyList(),
): NotificationCompat.Builder {
  setCustomContentView(views.collapsed)
  views.expanded?.let(::setCustomBigContentView)
  views.headsUp?.let(::setCustomHeadsUpContentView)
  if (decorated) {
    setStyle(NotificationCompat.DecoratedCustomViewStyle())
  }
  setOngoing(ongoing)
  addPeekActions(actions)
  return this
}
