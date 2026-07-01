package io.github.jakex7.peek.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.ui.unit.DpSize

class PeekAppWidgetManager(
  private val context: Context,
) {
  private val appWidgetManager = AppWidgetManager.getInstance(context)

  fun getPeekAppWidgetIds(receiver: Class<out PeekAppWidgetReceiver>): List<PeekAppWidgetId> {
    val componentName = ComponentName(context, receiver)
    return appWidgetManager.getAppWidgetIds(componentName).map(::PeekAppWidgetId)
  }

  fun getAppWidgetId(id: PeekAppWidgetId): Int =
    id.appWidgetId

  fun getPeekAppWidgetIdBy(appWidgetId: Int): PeekAppWidgetId {
    requireNotNull(appWidgetManager.getAppWidgetInfo(appWidgetId)) {
      "Invalid app widget ID: $appWidgetId"
    }
    return PeekAppWidgetId(appWidgetId)
  }

  fun getPeekAppWidgetIdBy(configurationIntent: Intent): PeekAppWidgetId? {
    val appWidgetId =
      configurationIntent.extras?.getInt(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID,
      ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    return if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) null else PeekAppWidgetId(appWidgetId)
  }

  fun getAppWidgetSizes(id: PeekAppWidgetId): List<DpSize> =
    appWidgetManager
      .getAppWidgetOptions(id.appWidgetId)
      .extractAllSizes { DpSize.Zero }

  fun requestPinPeekAppWidget(
    receiver: Class<out PeekAppWidgetReceiver>,
    preview: RemoteViews? = null,
    successCallback: PendingIntent? = null,
  ): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
    if (!AppWidgetManagerApi26Impl.isRequestPinAppWidgetSupported(appWidgetManager)) return false
    val extras =
      preview?.let {
        Bundle().apply { putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW, it) }
      }
    return AppWidgetManagerApi26Impl.requestPinAppWidget(
      appWidgetManager,
      ComponentName(context, receiver),
      extras,
      successCallback,
    )
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private object AppWidgetManagerApi26Impl {
    fun isRequestPinAppWidgetSupported(manager: AppWidgetManager): Boolean =
      manager.isRequestPinAppWidgetSupported

    fun requestPinAppWidget(
      manager: AppWidgetManager,
      target: ComponentName,
      extras: Bundle?,
      successCallback: PendingIntent?,
    ): Boolean =
      manager.requestPinAppWidget(target, extras, successCallback)
  }
}
