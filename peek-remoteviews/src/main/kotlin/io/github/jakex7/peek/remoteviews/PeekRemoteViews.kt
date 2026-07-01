package io.github.jakex7.peek.remoteviews

import android.content.Context
import android.widget.RemoteViews
import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import io.github.jakex7.peek.core.PeekComposable
import io.github.jakex7.peek.core.PeekRoot
import io.github.jakex7.peek.runtime.PeekComposition
import io.github.jakex7.peek.runtime.PeekTreeNormalizer

object PeekRemoteViews {
  /**
   * Composes [content] and translates it into host-neutral [RemoteViews].
   *
   * Use this for non-notification hosts that should wrap their content. Main-safe: composition
   * runs off the calling thread (see [PeekComposition]), so this may be called from the main thread
   * without blocking it. Call it from a coroutine.
   */
  suspend fun render(
    context: Context,
    content: @Composable @PeekComposable () -> Unit,
  ): RemoteViews {
    val root = PeekComposition.render(content)
    return render(context, root)
  }

  /**
   * Composes [content] and translates it into app-widget RemoteViews.
   *
   * App-widget hosts provide a bounded slot, so the root wrapper must fill both axes. Main-safe:
   * composition runs off the calling thread (see [PeekComposition]).
   */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  suspend fun renderAppWidget(
    context: Context,
    content: @Composable @PeekComposable () -> Unit,
  ): RemoteViews {
    val root = PeekComposition.render(content)
    return renderAppWidget(context, root)
  }

  /**
   * Translates an already-built [root] into host-neutral [RemoteViews].
   *
   * Synchronous and cheap: it performs no composition, only normalization and translation.
   */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  fun render(
    context: Context,
    root: PeekRoot,
  ): RemoteViews =
    RemoteViewsTranslator(context, surfaceDescription = "RemoteViews")
      .translate(PeekTreeNormalizer.normalize(root))

  /**
   * Translates an already-built [root] into app-widget RemoteViews.
   *
   * Synchronous and cheap: it performs no composition, only normalization and translation.
   */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  fun renderAppWidget(
    context: Context,
    root: PeekRoot,
  ): RemoteViews =
    RemoteViewsTranslator(
      context = context,
      surfaceDescription = "app widget RemoteViews",
      rootLayoutId = R.layout.peek_rv_root_fill_size,
    ).translate(PeekTreeNormalizer.normalize(root))

  /**
   * Composes [content] and translates it into notification [RemoteViews].
   *
   * Main-safe: composition runs off the calling thread (see [PeekComposition]), so this may be
   * called from the main thread without blocking it. Call it from a coroutine.
   */
  suspend fun render(
    context: Context,
    size: PeekNotificationSize,
    content: @Composable @PeekComposable () -> Unit,
  ): RemoteViews {
    val root = PeekComposition.render(content)
    return RemoteViewsTranslator(context, size.surfaceDescription).translate(root)
  }

  /**
   * Translates an already-composed [root] into notification [RemoteViews].
   *
   * Synchronous and cheap: it performs no composition, only normalization and translation.
   */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  fun render(
    context: Context,
    size: PeekNotificationSize,
    root: PeekRoot,
  ): RemoteViews =
    RemoteViewsTranslator(context, size.surfaceDescription)
      .translate(PeekTreeNormalizer.normalize(root))
}

private val PeekNotificationSize.surfaceDescription: String
  get() =
    when (this) {
      PeekNotificationSize.Collapsed -> "collapsed notification RemoteViews"
      PeekNotificationSize.Expanded -> "expanded notification RemoteViews"
      PeekNotificationSize.HeadsUp -> "heads-up notification RemoteViews"
    }
