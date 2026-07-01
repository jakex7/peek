package io.github.jakex7.peek.appwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class PeekAppWidgetReceiver : AppWidgetProvider() {
  companion object {
    const val ACTION_DEBUG_UPDATE: String = "io.github.jakex7.peek.appwidget.action.DEBUG_UPDATE"
    private const val TAG = "PeekAppWidgetReceiver"
  }

  abstract val peekAppWidget: PeekAppWidget

  open val coroutineContext: CoroutineContext = Dispatchers.Default

  @CallSuper
  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray,
  ) {
    goAsync {
      appWidgetIds
        .map { async { peekAppWidget.update(context, PeekAppWidgetId(it)) } }
        .awaitAll()
    }
  }

  @CallSuper
  override fun onAppWidgetOptionsChanged(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    newOptions: Bundle,
  ) {
    goAsync {
      peekAppWidget.update(context, PeekAppWidgetId(appWidgetId), newOptions)
    }
  }

  @CallSuper
  override fun onDeleted(
    context: Context,
    appWidgetIds: IntArray,
  ) {
    goAsync {
      appWidgetIds
        .map { async { peekAppWidget.onDelete(context, PeekAppWidgetId(it)) } }
        .awaitAll()
    }
  }

  override fun onReceive(
    context: Context,
    intent: Intent,
  ) {
    when (intent.action) {
      Intent.ACTION_LOCALE_CHANGED,
      ACTION_DEBUG_UPDATE -> {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids =
          intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            ?: appWidgetManager.getAppWidgetIds(ComponentName(context, javaClass))
        onUpdate(context, appWidgetManager, ids)
      }
      else -> super.onReceive(context, intent)
    }
  }

  private fun goAsync(block: suspend CoroutineScope.() -> Unit) {
    val pendingResult = goAsync()
    CoroutineScope(SupervisorJob() + coroutineContext).launch {
      try {
        block()
      } catch (throwable: Throwable) {
        Log.e(TAG, "Error while updating Peek app widget.", throwable)
      } finally {
        pendingResult.finish()
      }
    }
  }
}
