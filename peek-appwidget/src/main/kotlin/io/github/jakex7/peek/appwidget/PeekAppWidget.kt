@file:Suppress("RestrictedApi")

package io.github.jakex7.peek.appwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.DpSize
import io.github.jakex7.peek.core.PeekComposable
import io.github.jakex7.peek.remoteviews.PeekRemoteViews
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

abstract class PeekAppWidget(
  @param:LayoutRes
  private val errorUiLayout: Int = R.layout.peek_appwidget_error,
) {
  open val sizeMode: PeekAppWidgetSizeMode = PeekAppWidgetSizeMode.Single

  open suspend fun provideContent(
    context: Context,
    id: PeekAppWidgetId,
  ): @Composable @PeekComposable () -> Unit =
    error("${this::class.java.name} must override provideContent or renderRemoteViewsForSizes.")

  open suspend fun onDelete(
    context: Context,
    id: PeekAppWidgetId,
  ) = Unit

  suspend fun update(
    context: Context,
    id: PeekAppWidgetId,
    options: Bundle? = null,
  ) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    try {
      appWidgetManager.updateAppWidget(id.appWidgetId, composeRemoteViews(context, id, options))
    } catch (throwable: Throwable) {
      // Rethrow cancellation of the calling scope instead of rendering the error UI for it.
      currentCoroutineContext().ensureActive()
      onCompositionError(context, id, throwable)
    }
  }

  suspend fun updateAll(
    context: Context,
    receiver: Class<out PeekAppWidgetReceiver>,
  ) {
    PeekAppWidgetManager(context)
      .getPeekAppWidgetIds(receiver)
      .forEach { update(context, it) }
  }

  suspend fun composeRemoteViews(
    context: Context,
    id: PeekAppWidgetId,
    options: Bundle? = null,
  ): RemoteViews {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val resolvedOptions =
      options ?: appWidgetManager.getAppWidgetOptions(id.appWidgetId) ?: Bundle.EMPTY
    val minSize = appWidgetManager.getWidgetMinSize(context, id, resolvedOptions)
    val sizes = resolvePeekAppWidgetSizes(sizeMode, resolvedOptions, minSize)
    val views = renderRemoteViewsForSizes(context, id, resolvedOptions, sizes)
    return combineRemoteViewsBySize(views, sizeMode)
  }

  open fun onCompositionError(
    context: Context,
    id: PeekAppWidgetId,
    throwable: Throwable,
  ) {
    if (errorUiLayout == 0) throw throwable
    AppWidgetManager.getInstance(context)
      .updateAppWidget(id.appWidgetId, RemoteViews(context.packageName, errorUiLayout))
  }

  protected open suspend fun renderRemoteViewsForSizes(
    context: Context,
    id: PeekAppWidgetId,
    options: Bundle,
    sizes: List<DpSize>,
  ): List<Pair<DpSize, RemoteViews>> {
    val content = provideContent(context, id)
    return sizes.map { size ->
      size to renderContentForSize(context, id, options, size, content)
    }
  }

  private suspend fun renderContentForSize(
    context: Context,
    id: PeekAppWidgetId,
    options: Bundle,
    size: DpSize,
    content: @Composable @PeekComposable () -> Unit,
  ): RemoteViews =
    PeekRemoteViews.renderAppWidget(context) {
      CompositionLocalProvider(
        LocalPeekAppWidgetId provides id,
        LocalPeekAppWidgetOptions provides options,
        LocalPeekWidgetSize provides size,
      ) {
        content()
      }
    }

}
