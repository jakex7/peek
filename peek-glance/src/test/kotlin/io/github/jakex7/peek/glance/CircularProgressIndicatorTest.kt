@file:Suppress(
  "INVISIBLE_MEMBER",
  "INVISIBLE_REFERENCE",
  "RestrictedApiAndroidX",
)

package io.github.jakex7.peek.glance

import android.content.ComponentName
import android.content.Context
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.compose.remote.core.Operation
import androidx.compose.remote.core.operations.DrawArc
import androidx.compose.remote.creation.CreationDisplayInfo
import androidx.compose.remote.creation.RemoteComposeContext
import androidx.compose.remote.creation.profile.RcPlatformProfiles
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceComponents
import androidx.glance.appwidget.remotecompose.TranslationContext
import androidx.glance.unit.ColorProvider
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class CircularProgressIndicatorTest {
  private val context = ApplicationProvider.getApplicationContext<Context>()

  @Test
  fun progressIsClampedAndPreservedByCopy() {
    val node = determinateCircularProgressIndicatorEmittable(
      progress = 1.5f,
      modifier = GlanceModifier,
      color = ColorProvider(Color.Red),
      trackColor = ColorProvider(Color.Blue),
    ) as EmittableDeterminateCircularProgressIndicator

    val copy = node.copy() as EmittableDeterminateCircularProgressIndicator

    assertEquals(1f, node.progress)
    assertEquals(node.progress, copy.progress)
    assertEquals(node.color, copy.color)
    assertEquals(node.trackColor, copy.trackColor)
    assertNotSame(node, copy)
  }

  @Test
  fun nonFiniteProgressIsRejected() {
    listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY).forEach { progress ->
      assertThrows(IllegalArgumentException::class.java) {
        determinateCircularProgressIndicatorEmittable(progress)
      }
    }
  }

  @Test
  fun remoteViewsUsesDeterminatePlatformProgressBar() {
    val node = determinateCircularProgressIndicatorEmittable(0.376f)
      as EmittableDeterminateCircularProgressIndicator

    val view = node.createRemoteViews(context).apply(context, FrameLayout(context)) as ProgressBar

    assertEquals(false, view.isIndeterminate)
    assertEquals(100, view.max)
    assertEquals(38, view.progress)
  }

  @Test
  fun remoteComposeUsesTwoNativeArcOperations() {
    val node = determinateCircularProgressIndicatorEmittable(0.375f)
      as EmittableDeterminateCircularProgressIndicator

    val component = ComponentName(context, javaClass)
    val remoteComposeContext = RemoteComposeContext(
      creationDisplayInfo = CreationDisplayInfo(32, 32, 1),
      contentDescription = "progress",
      profile = RcPlatformProfiles.WIDGETS_V7,
    ) {
      val translationContext = TranslationContext(
        context = context,
        remoteComposeContext = this,
        appWidgetId = 1,
        layoutSize = DpSize(32.dp, 32.dp),
        actionMap = mutableListOf(),
        glanceComponents = GlanceComponents(component, component, component, component),
        actionBroadcastReceiver = component,
      )
      root {
        node.translateRemoteCompose(translationContext)
          .writeComponent(translationContext)
      }
    }

    val operations = arrayListOf<Operation>()
    remoteComposeContext.buffer.inflateFromBuffer(operations)
    val arcs = operations.filterIsInstance<DrawArc>()

    assertEquals(2, arcs.size)
    assertEquals(-90f, arcs[0].rawValue(5))
    assertEquals(360f, arcs[0].rawValue(6))
    assertEquals(-90f, arcs[1].rawValue(5))
    assertEquals(135f, arcs[1].rawValue(6))
  }

  private fun DrawArc.rawValue(index: Int): Float {
    val field = javaClass.superclass.getDeclaredField("mValue$index")
    field.isAccessible = true
    return field.getFloat(this)
  }
}
