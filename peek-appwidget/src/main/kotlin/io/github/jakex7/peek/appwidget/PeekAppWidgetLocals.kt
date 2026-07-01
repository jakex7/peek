package io.github.jakex7.peek.appwidget

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.DpSize

val LocalPeekAppWidgetId: ProvidableCompositionLocal<PeekAppWidgetId> =
  staticCompositionLocalOf { PeekAppWidgetId(AppWidgetManager.INVALID_APPWIDGET_ID) }

val LocalPeekAppWidgetOptions: ProvidableCompositionLocal<Bundle> =
  staticCompositionLocalOf { Bundle.EMPTY }

val LocalPeekWidgetSize: ProvidableCompositionLocal<DpSize> =
  staticCompositionLocalOf { DpSize.Unspecified }
