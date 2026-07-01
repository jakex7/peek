package io.github.jakex7.peek.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class PeekModifierTest {
  @Test
  fun modifierOrderIsPreserved() {
      val modifier =
      PeekModifier
        .padding(8.dp)
        .background(ColorProvider(Color.Red))
        .fillMaxWidth()

    assertEquals(
      listOf(
        PaddingModifier::class,
        BackgroundModifier::class,
        WidthModifier::class,
      ),
      modifier.elements.map { it::class },
    )
  }

  @Test
  fun clickableStoresHostProvidedPendingIntent() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val pendingIntent =
      PendingIntent.getActivity(
        context,
        42,
        Intent("io.github.jakex7.peek.TEST_ACTION").setPackage(context.packageName),
        PendingIntent.FLAG_IMMUTABLE,
      )

    val action = PeekModifier.clickable(pendingIntent).find<ClickActionModifier>()

    assertSame(pendingIntent, action?.pendingIntent)
  }

  @Test
  fun clickableStoresPeekAction() {
    val intent = Intent("io.github.jakex7.peek.TEST_ACTION")
    val peekAction = actionSendBroadcast(intent)

    val action = PeekModifier.clickable(peekAction).find<PeekActionModifier>()

    assertSame(peekAction, action?.action)
  }

  @Test
  fun sizeHelpersStoreWidthAndHeightDimensions() {
    val fixed = PeekModifier.size(24.dp)
    val wrapped = PeekModifier.wrapContentSize()

    assertEquals(Dimension.Fixed(24.dp), fixed.find<WidthModifier>()?.dimension)
    assertEquals(Dimension.Fixed(24.dp), fixed.find<HeightModifier>()?.dimension)
    assertEquals(Dimension.Wrap, wrapped.find<WidthModifier>()?.dimension)
    assertEquals(Dimension.Wrap, wrapped.find<HeightModifier>()?.dimension)
  }

  @Test
  fun visibilityStoresVisibilityModifier() {
    val modifier = PeekModifier.visibility(Visibility.Gone)

    assertEquals(Visibility.Gone, modifier.find<VisibilityModifier>()?.visibility)
  }

  @Test
  fun actionPendingIntentWrapsHostProvidedPendingIntent() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val pendingIntent =
      PendingIntent.getActivity(
        context,
        43,
        Intent("io.github.jakex7.peek.TEST_ACTION").setPackage(context.packageName),
        PendingIntent.FLAG_IMMUTABLE,
      )

    val action = actionPendingIntent(pendingIntent)

    assertSame(pendingIntent, (action as PendingIntentPeekAction).pendingIntent)
  }
}
