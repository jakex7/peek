package io.github.jakex7.peek.appwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.util.SizeF
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import io.github.jakex7.peek.core.CircularProgressIndicator
import io.github.jakex7.peek.core.Column
import io.github.jakex7.peek.core.PeekComposable
import io.github.jakex7.peek.core.PeekModifier
import io.github.jakex7.peek.core.Text
import io.github.jakex7.peek.core.fillMaxSize
import io.github.jakex7.peek.testing.findFirstLinearLayout
import io.github.jakex7.peek.testing.findFirstProgressBar
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
class PeekAppWidgetTest {
  @Test
  fun composeRemoteViewsProvidesWidgetLocals() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val options =
      Bundle().apply {
        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 120)
        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 120)
        putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 80)
        putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 80)
      }
    val widget =
      object : PeekAppWidget() {
        override suspend fun provideContent(
          context: Context,
          id: PeekAppWidgetId,
        ): @Composable @PeekComposable () -> Unit = {
          val size = LocalPeekWidgetSize.current
          val localOptions = LocalPeekAppWidgetOptions.current
          Column {
            Text("id=${LocalPeekAppWidgetId.current.appWidgetId}")
            Text("size=${size.width.value.toInt()}x${size.height.value.toInt()}")
            Text("minWidth=${localOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)}")
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
  fun exactSizeModeUsesHostProvidedSizesOnAndroid12() {
    val options =
      Bundle().apply {
        putParcelableArrayList(
          AppWidgetManager.OPTION_APPWIDGET_SIZES,
          arrayListOf(SizeF(80f, 40f), SizeF(160f, 80f)),
        )
      }

    val sizes =
      resolvePeekAppWidgetSizes(
        sizeMode = PeekAppWidgetSizeMode.Exact,
        options = options,
        minSize = DpSize(50.dp, 20.dp),
      )

    assertEquals(listOf(DpSize(80.dp, 40.dp), DpSize(160.dp, 80.dp)), sizes)
  }

  @Test
  fun composeRemoteViewsUsesFullHeightRootForFillMaxSizeContent() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val widget =
      object : PeekAppWidget() {
        override suspend fun provideContent(
          context: Context,
          id: PeekAppWidgetId,
        ): @Composable @PeekComposable () -> Unit = {
          Column(modifier = PeekModifier.fillMaxSize()) {
            Text("Full height")
          }
        }
      }

    val remoteViews = widget.composeRemoteViews(context, PeekAppWidgetId(7), Bundle.EMPTY)
    val applied = remoteViews.apply(context, FrameLayout(context))
    val column = applied.findFirstLinearLayout()

    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, applied.layoutParams?.height)
    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, column?.layoutParams?.height)
  }

  @Test
  fun circularProgressWithProgressIsDeterminateInWidgets() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val widget =
      object : PeekAppWidget() {
        override suspend fun provideContent(
          context: Context,
          id: PeekAppWidgetId,
        ): @Composable @PeekComposable () -> Unit = {
          CircularProgressIndicator(progress = 0.4f)
        }
      }

    val remoteViews = widget.composeRemoteViews(context, PeekAppWidgetId(7), Bundle.EMPTY)
    val applied = remoteViews.apply(context, FrameLayout(context))
    val progressBar = applied.findFirstProgressBar()

    assertNotNull(progressBar)
    assertEquals(false, progressBar?.isIndeterminate)
    assertEquals(100, progressBar?.max)
    assertEquals(40, progressBar?.progress)
  }
}
