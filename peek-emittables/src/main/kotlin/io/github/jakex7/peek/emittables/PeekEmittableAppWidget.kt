package io.github.jakex7.peek.emittables

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.compose.ui.unit.DpSize
import io.github.jakex7.peek.appwidget.PeekAppWidget
import io.github.jakex7.peek.appwidget.PeekAppWidgetId

abstract class PeekEmittableAppWidget(
  @param:LayoutRes private val errorUiLayout: Int = R.layout.peek_emittable_appwidget_error,
) : PeekAppWidget(errorUiLayout) {

  abstract suspend fun provideRoot(
    context: Context,
    id: PeekAppWidgetId,
    options: Bundle,
    size: DpSize,
  ): PeekRoot

  override fun onCompositionError(
    context: Context,
    id: PeekAppWidgetId,
    throwable: Throwable,
  ) {
    onRenderError(context, id, throwable)
  }

  override suspend fun renderRemoteViewsForSizes(
    context: Context,
    id: PeekAppWidgetId,
    options: Bundle,
    sizes: List<DpSize>,
  ): List<Pair<DpSize, RemoteViews>> =
    sizes.map { size ->
      size to PeekEmittableRemoteViews.renderAppWidget(
        context,
        provideRoot(context, id, options, size),
      )
    }

  open fun onRenderError(
    context: Context,
    id: PeekAppWidgetId,
    throwable: Throwable,
  ) {
    Log.e("PeekEmittableAppWidget", "Error while rendering app widget.", throwable)
    if (errorUiLayout == 0) throw throwable
    AppWidgetManager.getInstance(context)
      .updateAppWidget(id.appWidgetId, RemoteViews(context.packageName, errorUiLayout))
  }
}
