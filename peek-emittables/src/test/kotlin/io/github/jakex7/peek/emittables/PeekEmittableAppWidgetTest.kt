@file:Suppress("RestrictedApi")

package io.github.jakex7.peek.emittables

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.unit.DpSize
import androidx.test.core.app.ApplicationProvider
import io.github.jakex7.peek.appwidget.PeekAppWidget
import io.github.jakex7.peek.appwidget.PeekAppWidgetId
import io.github.jakex7.peek.appwidget.PeekAppWidgetSizeMode
import io.github.jakex7.peek.appwidget.resolvePeekAppWidgetSizes
import io.github.jakex7.peek.testing.findFirstLinearLayout
import io.github.jakex7.peek.testing.findText
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class PeekEmittableAppWidgetTest {
  @Test
  fun emittableAppWidgetCanBeUsedAsPeekAppWidget() {
    val widget: PeekAppWidget =
      object : PeekEmittableAppWidget() {
        override suspend fun provideRoot(
          context: Context,
          id: PeekAppWidgetId,
          options: Bundle,
          size: DpSize,
        ): PeekRoot = PeekRoot()
      }

    assertNotNull(widget)
  }

  @Test
  fun emittablesCanUseSharedAppWidgetSizeResolver() {
    val sizes =
      resolvePeekAppWidgetSizes(
        sizeMode = PeekAppWidgetSizeMode.Single,
        options = Bundle.EMPTY,
        minSize = DpSize.Zero,
      )

    assertEquals(listOf(DpSize.Zero), sizes)
  }

  @Test
  fun composeRemoteViewsPassesWidgetContextToRootProvider() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val options =
      Bundle().apply {
        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 120)
        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 120)
        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 80)
        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 80)
      }
    val widget =
      object : PeekEmittableAppWidget() {
        override suspend fun provideRoot(
          context: Context,
          id: PeekAppWidgetId,
          options: Bundle,
          size: DpSize,
        ): PeekRoot =
          PeekRoot().also { root ->
            root.children +=
              EmittableColumn().also { column ->
                column.children += EmittableText().also { it.text = "id=${id.appWidgetId}" }
                column.children +=
                  EmittableText().also {
                    it.text = "size=${size.width.value.toInt()}x${size.height.value.toInt()}"
                  }
                column.children +=
                  EmittableText().also {
                    it.text =
                      "minWidth=${options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)}"
                  }
              }
          }
      }

    val remoteViews = widget.composeRemoteViews(context, PeekAppWidgetId(42), options)
    val applied = remoteViews.apply(context, FrameLayout(context))

    assertNotNull(applied.findText("id=42"))
    assertNotNull(applied.findText("size=120x80"))
    assertNotNull(applied.findText("minWidth=120"))
  }

  @Test
  fun composeRemoteViewsUsesFullHeightRootForFillMaxSizeContent() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val widget =
      object : PeekEmittableAppWidget() {
        override suspend fun provideRoot(
          context: Context,
          id: PeekAppWidgetId,
          options: Bundle,
          size: DpSize,
        ): PeekRoot =
          PeekRoot().also { root ->
            root.children +=
              EmittableColumn().also { column ->
                column.modifier = PeekModifier.fillMaxSize()
                column.children += EmittableText().also { it.text = "Full height" }
              }
          }
      }

    val remoteViews = widget.composeRemoteViews(context, PeekAppWidgetId(7), Bundle.EMPTY)
    val applied = remoteViews.apply(context, FrameLayout(context))
    val column = applied.findFirstLinearLayout()

    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, applied.layoutParams?.height)
    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, column?.layoutParams?.height)
  }
}
