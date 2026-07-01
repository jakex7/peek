package io.github.jakex7.peek.remoteviews

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Icon as AndroidIcon
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RotateDrawable
import android.net.Uri
import android.os.Parcel
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.RemoteViews
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import io.github.jakex7.peek.core.Alignment
import io.github.jakex7.peek.core.AndroidRemoteViews
import io.github.jakex7.peek.core.Badge
import io.github.jakex7.peek.core.Box
import io.github.jakex7.peek.core.Button
import io.github.jakex7.peek.core.CheckBox
import io.github.jakex7.peek.core.CircularProgressIndicator
import io.github.jakex7.peek.core.ColorFilter
import io.github.jakex7.peek.core.ColorProvider
import io.github.jakex7.peek.core.Column
import io.github.jakex7.peek.core.ContentScale
import io.github.jakex7.peek.core.FontStyle
import io.github.jakex7.peek.core.FontWeight
import io.github.jakex7.peek.core.HorizontalDivider
import io.github.jakex7.peek.core.Icon
import io.github.jakex7.peek.core.Image
import io.github.jakex7.peek.core.ImageProvider
import io.github.jakex7.peek.core.LinearProgressIndicator
import io.github.jakex7.peek.core.PeekComposable
import io.github.jakex7.peek.core.PeekModifier
import io.github.jakex7.peek.core.PeekRoot
import io.github.jakex7.peek.core.PeekTheme
import io.github.jakex7.peek.core.RadioButton
import io.github.jakex7.peek.core.Row
import io.github.jakex7.peek.core.Spacer
import io.github.jakex7.peek.core.Switch
import io.github.jakex7.peek.core.Text
import io.github.jakex7.peek.core.TextAlign
import io.github.jakex7.peek.core.TextDecoration
import io.github.jakex7.peek.core.VerticalDivider
import io.github.jakex7.peek.core.Visibility
import io.github.jakex7.peek.core.actionSendBroadcast
import io.github.jakex7.peek.core.background
import io.github.jakex7.peek.core.fillMaxHeight
import io.github.jakex7.peek.core.fillMaxSize
import io.github.jakex7.peek.core.fillMaxWidth
import io.github.jakex7.peek.core.height
import io.github.jakex7.peek.core.padding
import io.github.jakex7.peek.core.peekColors
import io.github.jakex7.peek.core.selectableGroup
import io.github.jakex7.peek.core.size
import io.github.jakex7.peek.core.layout.EmittableColumn
import io.github.jakex7.peek.core.text.EmittableText
import io.github.jakex7.peek.core.text.EmittableButton
import io.github.jakex7.peek.core.visibility
import io.github.jakex7.peek.core.width
import io.github.jakex7.peek.core.wrapContentWidth
import io.github.jakex7.peek.core.defaultMinSize
import io.github.jakex7.peek.testing.findAllImageViews
import io.github.jakex7.peek.testing.findAllTextViews
import io.github.jakex7.peek.testing.findColorView
import io.github.jakex7.peek.testing.findFirstCheckBox
import io.github.jakex7.peek.testing.findFirstCompoundButton
import io.github.jakex7.peek.testing.findFirstImageView
import io.github.jakex7.peek.testing.findFirstLinearLayout
import io.github.jakex7.peek.testing.findFirstProgressBar
import io.github.jakex7.peek.testing.findFirstRadioButton
import io.github.jakex7.peek.testing.findFirstRadioGroup
import io.github.jakex7.peek.testing.findFirstSwitch
import io.github.jakex7.peek.testing.findText
import io.github.jakex7.peek.testing.findViewWithExactTag
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class PeekRemoteViewsTest {
  @Test
  fun renderRootTranslatesEmittableTreeWithoutComposition() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val root =
      PeekRoot().also { root ->
        root.children +=
          EmittableColumn().also { column ->
            column.children += EmittableText().also { it.text = "Direct root" }
          }
      }

    val remoteViews = PeekRemoteViews.render(context, root)
    val applied = remoteViews.apply(context, FrameLayout(context))

    assertNotNull(applied.findText("Direct root"))
  }

  @Test
  fun renderUsesWrapContentRootForHostNeutralRemoteViews() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      runBlocking {
        PeekRemoteViews.render(context) {
          Column(modifier = PeekModifier.fillMaxSize()) {
            Text("Wrapped root")
          }
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))

    assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, applied.layoutParams?.height)
  }

  @Test
  fun renderUsesWrapContentForDefaultRowWidth() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Row {
          Text("One")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val row = applied.findFirstLinearLayout()

    assertNotNull(row)
    assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, row?.layoutParams?.width)
  }

  @Test
  fun renderUsesUniqueLeafIdsOnAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Row {
          Text("One")
          Text("Two")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val textViews = applied.findAllTextViews()
    val textViewIds = textViews.map { it.id }

    assertEquals(listOf("One", "Two"), textViews.map { it.text.toString() })
    assertEquals(textViewIds.size, textViewIds.toSet().size)
    assertEquals(false, textViewIds.any { it == R.id.peek_main })
  }

  @Test
  fun renderAppliesExplicitRowVerticalAlignment() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("Uploading")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val row = applied.findFirstLinearLayout()

    assertNotNull(row)
    assertEquals(Gravity.CENTER_VERTICAL, row?.gravity?.and(Gravity.VERTICAL_GRAVITY_MASK))
  }

  @Test
  fun renderAppliesExplicitColumnAlignment() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalAlignment = Alignment.Bottom,
        ) {
          Text("Uploading")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val column = applied.findFirstLinearLayout()

    assertNotNull(column)
    assertEquals(Gravity.CENTER_HORIZONTAL, column?.gravity?.and(Gravity.HORIZONTAL_GRAVITY_MASK))
    assertEquals(Gravity.BOTTOM, column?.gravity?.and(Gravity.VERTICAL_GRAVITY_MASK))
  }

  @Test
  fun renderAppliesExplicitBoxContentAlignment() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Box(
          modifier = PeekModifier.size(48.dp),
          contentAlignment = Alignment.Center,
        ) {
          Text("Centered")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val alignedContainer = applied.findFirstLinearLayout()

    assertNotNull(alignedContainer)
    assertEquals(
      Gravity.CENTER_HORIZONTAL,
      alignedContainer?.gravity?.and(Gravity.HORIZONTAL_GRAVITY_MASK),
    )
    assertEquals(
      Gravity.CENTER_VERTICAL,
      alignedContainer?.gravity?.and(Gravity.VERTICAL_GRAVITY_MASK),
    )
  }

  @Test
  fun renderAppliesSpacer() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Row {
          Text("Left")
          Spacer(modifier = PeekModifier.size(8.dp))
          Text("Right")
        }
      }

    // apply() throws if the Spacer template root is not a RemoteViews-allowed widget.
    val applied = remoteViews.apply(context, FrameLayout(context))

    assertNotNull(applied.findText("Left"))
    assertNotNull(applied.findText("Right"))
  }

  @Test
  fun renderWidgetLikeNestedTreeCanBeParceled() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Column(modifier = PeekModifier.fillMaxWidth()) {
          Row {
            Box(
              modifier = PeekModifier.width(166.dp).height(148.dp),
              contentAlignment = Alignment.Center,
            ) {
              Box(
                modifier = PeekModifier.width(156.dp).height(132.dp),
                contentAlignment = Alignment.Center,
              ) {
                CircularProgressIndicator(
                  progress = 0.7f,
                  modifier = PeekModifier.size(118.dp),
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("1299")
                  Text("Calories Left")
                }
              }
            }

            Column(modifier = PeekModifier.width(78.dp)) {
              Box(
                modifier = PeekModifier.fillMaxWidth().fillMaxHeight(),
                contentAlignment = Alignment.Center,
              ) {
                Box(
                  modifier = PeekModifier.fillMaxWidth().height(72.dp),
                  contentAlignment = Alignment.Center,
                ) {
                  Box(
                    modifier = PeekModifier.width(56.dp).height(46.dp),
                    contentAlignment = Alignment.Center,
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(modifier = PeekModifier.width(3.dp).height(30.dp)) {}
                      Spacer(modifier = PeekModifier.width(4.dp))
                      Box(modifier = PeekModifier.width(5.dp).height(34.dp)) {}
                      Spacer(modifier = PeekModifier.width(3.dp))
                      Box(modifier = PeekModifier.width(2.dp).height(31.dp)) {}
                    }
                    Text("[ ]")
                  }
                }
              }
            }
          }
        }
      }

    val parcel = Parcel.obtain()
    try {
      remoteViews.writeToParcel(parcel, 0)
      parcel.setDataPosition(0)
      RemoteViews.CREATOR.createFromParcel(parcel)
    } finally {
      parcel.recycle()
    }
  }

  @Test
  fun renderAppliesTextAndBackgroundColor() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Column(
          modifier =
            PeekModifier
              .padding(8.dp)
              .background(ColorProvider(Color(0xFF102030))),
        ) {
          Text("Uploading", color = ColorProvider(Color.White), maxLines = 1)
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val textView = applied.findText("Uploading")

    assertNotNull(textView)
    assertEquals("Uploading", (textView as TextView).text.toString())
    assertEquals(Color.White.toArgb(), textView.currentTextColor)
  }

  @Test
  fun renderUsesNightThemeForDefaultTextColor() {
    val context = ApplicationProvider.getApplicationContext<Context>().withNightMode()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Text("Themed")
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val textView = applied.findText("Themed")

    assertNotNull(textView)
    assertEquals(context.getColor(android.R.color.system_neutral1_100), textView?.currentTextColor)
  }

  @Test
  fun renderUsesPeekThemeForDefaultTextColor() {
    val context = ApplicationProvider.getApplicationContext<Context>().withNightMode()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        PeekTheme(
          colors =
            peekColors(
              onSurface = ColorProvider(day = Color.Red, night = Color.Yellow),
            ),
        ) {
          Text("Themed")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val textView = applied.findText("Themed")

    assertNotNull(textView)
    assertEquals(Color.Yellow.toArgb(), textView?.currentTextColor)
  }

  @Test
  fun explicitTextColorOverridesPeekTheme() {
    val context = ApplicationProvider.getApplicationContext<Context>().withNightMode()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        PeekTheme(
          colors =
            peekColors(
              onSurface = ColorProvider(day = Color.Red, night = Color.Yellow),
            ),
        ) {
          Text("Themed", color = ColorProvider(Color.Green))
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val textView = applied.findText("Themed")

    assertNotNull(textView)
    assertEquals(Color.Green.toArgb(), textView?.currentTextColor)
  }

  @Test
  fun renderAppliesTextAlignment() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Text("Uploading", textAlign = TextAlign.Center)
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val textView = applied.findText("Uploading")

    assertNotNull(textView)
    assertEquals(Gravity.CENTER_HORIZONTAL, textView?.gravity?.and(Gravity.HORIZONTAL_GRAVITY_MASK))
  }

  @Test
  fun renderAppliesTextStyleSpans() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Text(
          text = "Final",
          fontWeight = FontWeight.Bold,
          fontStyle = FontStyle.Italic,
          textDecoration = TextDecoration.combine(
            TextDecoration.Underline,
            TextDecoration.LineThrough,
          ),
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val textView = applied.findText("Final")
    val styledText = textView?.text as Spanned
    val styleSpans = styledText.getSpans(0, styledText.length, StyleSpan::class.java)

    assertEquals(1, styleSpans.size)
    assertEquals(Typeface.BOLD_ITALIC, styleSpans.single().style)
    assertEquals(1, styledText.getSpans(0, styledText.length, UnderlineSpan::class.java).size)
    assertEquals(1, styledText.getSpans(0, styledText.length, StrikethroughSpan::class.java).size)
  }

  @Test
  fun renderAppliesHorizontalDividerColorAndThickness() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Column {
          Text("One")
          HorizontalDivider(
            modifier = PeekModifier.fillMaxWidth(),
            thickness = 2.dp,
            color = ColorProvider(Color.Red),
          )
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val divider = applied.findColorView(Color.Red.toArgb())

    assertNotNull(divider)
    assertEquals(2.dp.toPx(context), divider?.layoutParams?.height)
    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, divider?.layoutParams?.width)
  }

  @Test
  fun renderAppliesBadgeTextColorsAndPadding() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Badge(
          text = "LIVE",
          color = ColorProvider(Color.Red),
          contentColor = ColorProvider(Color.White),
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val badge = applied.findText("LIVE")

    assertNotNull(badge)
    assertEquals(Color.White.toArgb(), badge?.currentTextColor)
    assertEquals(Color.Red.toArgb(), (badge?.background as ColorDrawable).color)
    assertEquals(6.dp.toPx(context), badge.paddingStart)
    assertEquals(2.dp.toPx(context), badge.paddingTop)
  }

  @Test
  fun renderAppliesIconDefaultSizeTintAndAlpha() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Icon(
          provider = ImageProvider(android.R.drawable.stat_sys_upload),
          contentDescription = "Upload",
          tint = ColorProvider(Color.Black),
          alpha = 0.5f,
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val icon = applied.findFirstImageView()

    assertNotNull(icon)
    assertEquals("Upload", icon?.contentDescription)
    assertEquals(24.dp.toPx(context), icon?.layoutParams?.width)
    assertEquals(24.dp.toPx(context), icon?.layoutParams?.height)
    assertEquals(128, icon?.imageAlpha)
    assertNotNull(icon?.colorFilter)
  }

  @Test
  fun renderAppliesButtonTextEnabledStateAndClickAction() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        Button(
          text = "Pause",
          onClick = actionSendBroadcast(
            Intent("io.github.jakex7.peek.BUTTON").setPackage(context.packageName),
          ),
          enabled = true,
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val button = applied.findText("Pause")

    assertNotNull(button)
    assertEquals(true, button?.isEnabled)
    assertEquals(true, button?.hasOnClickListeners())
  }

  @Test
  fun renderDefaultButtonPreservesNativeBackgroundShape() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        Button(
          text = "Pause",
          onClick = actionSendBroadcast(
            Intent("io.github.jakex7.peek.BUTTON").setPackage(context.packageName),
          ),
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val button = applied.findText("Pause")

    assertNotNull(button)
    assertTrue(button?.background !is ColorDrawable)
    assertTrue(button?.minWidth ?: 0 > 0)
    assertTrue(button?.minHeight ?: 0 > 0)
    assertTrue(button?.paddingStart ?: 0 > 0)
    assertTrue(button?.paddingEnd ?: 0 > 0)
    assertTrue(button?.paddingTop ?: 0 > 0)
    assertTrue(button?.paddingBottom ?: 0 > 0)
  }

  @Test
  fun renderButtonPaddingModifierOverridesDefaultPadding() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        Button(
          text = "Pause",
          onClick = actionSendBroadcast(
            Intent("io.github.jakex7.peek.BUTTON").setPackage(context.packageName),
          ),
          modifier = PeekModifier.padding(2.dp),
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val button = applied.findText("Pause")

    assertNotNull(button)
    assertEquals(2.dp.toPx(context), button?.paddingStart)
    assertEquals(2.dp.toPx(context), button?.paddingTop)
    assertEquals(2.dp.toPx(context), button?.paddingEnd)
    assertEquals(2.dp.toPx(context), button?.paddingBottom)
  }

  @Test
  fun renderDoesNotAttachClickActionToDisabledButton() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val pendingIntent =
      PendingIntent.getBroadcast(
        context,
        3,
        Intent("io.github.jakex7.peek.DISABLED_BUTTON").setPackage(context.packageName),
        PendingIntent.FLAG_IMMUTABLE,
      )
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        Button(
          text = "Pause",
          onClick = pendingIntent,
          enabled = false,
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val button = applied.findText("Pause")

    assertNotNull(button)
    assertEquals(false, button?.isEnabled)
    assertEquals(false, button?.hasOnClickListeners())
  }

  @Test
  fun renderAllowsEmittableButtonWithoutClickAction() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val root =
      PeekRoot().also { root ->
        root.children +=
          EmittableButton().also {
            it.text = "Details"
            it.onClick = null
          }
      }

    val remoteViews = PeekRemoteViews.render(context, root)
    val applied = remoteViews.apply(context, FrameLayout(context))
    val button = applied.findText("Details")

    assertNotNull(button)
    assertEquals(false, button?.hasOnClickListeners())
  }

  @Test
  fun renderAppliesEmittableButtonTextColor() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val root =
      PeekRoot().also { root ->
        root.children +=
          EmittableButton().also {
            it.text = "Details"
            it.color = ColorProvider(Color.Red)
          }
      }

    val remoteViews = PeekRemoteViews.render(context, root)
    val applied = remoteViews.apply(context, FrameLayout(context))
    val button = applied.findText("Details")

    assertNotNull(button)
    assertEquals(Color.Red.toArgb(), button?.currentTextColor)
  }

  @Test
  fun renderAppliesMinimumSizeModifiers() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        Text(
          text = "Minimum",
          modifier = PeekModifier.defaultMinSize(minWidth = 32.dp, minHeight = 18.dp),
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val text = applied.findText("Minimum")

    assertNotNull(text)
    assertEquals(32.dp.toPx(context), text?.minWidth)
    assertEquals(18.dp.toPx(context), text?.minHeight)
  }

  @Test
  fun renderAppliesCheckBoxStateTextAndClickAction() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        CheckBox(
          checked = true,
          onCheckedChange = actionSendBroadcast(
            Intent("io.github.jakex7.peek.CHECKBOX").setPackage(context.packageName),
          ),
          text = "Done",
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val checkBox = applied.findFirstCheckBox()

    assertNotNull(checkBox)
    assertEquals("Done", checkBox?.text.toString())
    assertEquals(true, checkBox?.isChecked)
    assertEquals(true, checkBox?.isEnabled)
    assertEquals(true, checkBox?.hasOnClickListeners())
  }

  @Test
  fun renderAppliesSwitchStateTextAndClickAction() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        Switch(
          checked = false,
          onCheckedChange = actionSendBroadcast(
            Intent("io.github.jakex7.peek.SWITCH").setPackage(context.packageName),
          ),
          text = "Wi-Fi",
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val switch = applied.findFirstSwitch()

    assertNotNull(switch)
    assertEquals("Wi-Fi", switch?.text.toString())
    assertEquals(false, switch?.isChecked)
    assertEquals(true, switch?.isEnabled)
    assertEquals(true, switch?.hasOnClickListeners())
  }

  @Test
  fun renderAppliesRadioButtonStateTextAndDisabledClickBehavior() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        RadioButton(
          checked = true,
          onClick = actionSendBroadcast(
            Intent("io.github.jakex7.peek.RADIO").setPackage(context.packageName),
          ),
          text = "Option A",
          enabled = false,
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val radioButton = applied.findFirstRadioButton()

    assertNotNull(radioButton)
    assertEquals("Option A", radioButton?.text.toString())
    assertEquals(true, radioButton?.isChecked)
    assertEquals(false, radioButton?.isEnabled)
    assertEquals(false, radioButton?.hasOnClickListeners())
  }

  @Test
  fun renderEmbedsAndroidRemoteViews() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val embedded =
      RemoteViews(context.packageName, R.layout.peek_rv_text).apply {
        setTextViewText(R.id.peek_main, "Embedded")
      }
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        AndroidRemoteViews(embedded)
      }

    val applied = remoteViews.apply(context, FrameLayout(context))

    assertNotNull(applied.findText("Embedded"))
  }

  @Test
  fun renderEmbedsAndroidRemoteViewsContainerWithPeekChildren() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val embedded = RemoteViews(context.packageName, R.layout.peek_rv_column)
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        AndroidRemoteViews(embedded, R.id.peek_main) {
          Text("Child")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))

    assertNotNull(applied.findText("Child"))
  }

  @Test
  fun renderUsesRadioGroupForSelectableRow() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        Row(modifier = PeekModifier.selectableGroup()) {
          RadioButton(checked = true, onClick = null, text = "Option A")
          RadioButton(checked = false, onClick = null, text = "Option B")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val radioGroup = applied.findFirstRadioGroup()

    assertNotNull(radioGroup)
    assertEquals(RadioGroup.HORIZONTAL, radioGroup?.orientation)
  }

  @Test
  fun renderUsesRadioGroupForSelectableColumn() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        Column(modifier = PeekModifier.selectableGroup()) {
          RadioButton(checked = true, onClick = null, text = "Option A")
          RadioButton(checked = false, onClick = null, text = "Option B")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val radioGroup = applied.findFirstRadioGroup()

    assertNotNull(radioGroup)
    assertEquals(RadioGroup.VERTICAL, radioGroup?.orientation)
  }

  @Test
  fun renderRejectsSelectableGroupWithMultipleCheckedRadioButtons() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    assertThrows(IllegalStateException::class.java) {
      renderBlocking(context, PeekNotificationSize.Expanded) {
        Row(modifier = PeekModifier.selectableGroup()) {
          RadioButton(checked = true, onClick = null, text = "Option A")
          RadioButton(checked = true, onClick = null, text = "Option B")
        }
      }
    }
  }

  @Test
  fun renderAppliesProgressIndicatorColors() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        LinearProgressIndicator(
          progress = 0.4f,
          color = ColorProvider(Color.Red),
          trackColor = ColorProvider(Color.Blue),
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val progressBar = applied.findFirstProgressBar()

    assertNotNull(progressBar)
    assertEquals(Color.Red.toArgb(), progressBar?.progressTintList?.defaultColor)
    assertEquals(Color.Blue.toArgb(), progressBar?.progressBackgroundTintList?.defaultColor)
  }

  @Test
  fun renderAppliesCircularProgressIndicatorColor() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        CircularProgressIndicator(color = ColorProvider(Color.Green))
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val progressBar = applied.findFirstProgressBar()

    assertNotNull(progressBar)
    assertEquals(Color.Green.toArgb(), progressBar?.indeterminateTintList?.defaultColor)
  }

  @Test
  fun renderDoesNotApplyCircularTrackColorWhenIndeterminate() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        CircularProgressIndicator(trackColor = ColorProvider(Color.Blue))
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val progressBar = applied.findFirstProgressBar()

    assertNotNull(progressBar)
    assertNotEquals(Color.Blue.toArgb(), progressBar?.progressBackgroundTintList?.defaultColor)
  }

  @Test
  fun renderAppliesDeterminateCircularProgressIndicatorTrackColor() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        CircularProgressIndicator(
          progress = 0.4f,
          color = ColorProvider(Color.Red),
          trackColor = ColorProvider(Color.Blue),
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val progressBar = applied.findFirstProgressBar()

    assertNotNull(progressBar)
    assertEquals(Color.Red.toArgb(), progressBar?.progressTintList?.defaultColor)
    assertEquals(Color.Blue.toArgb(), progressBar?.progressBackgroundTintList?.defaultColor)
  }

  @Test
  fun renderUsesDeterminateCircularProgressWhenProgressIsProvided() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        CircularProgressIndicator(progress = 0.4f)
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val progressBar = applied.findFirstProgressBar()

    assertNotNull(progressBar)
    assertEquals(false, progressBar?.isIndeterminate)
    assertEquals(100, progressBar?.max)
    assertEquals(40, progressBar?.progress)
  }

  @Test
  fun determinateCircularProgressDrawableStartsAtTop() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val drawable =
      context.getDrawable(R.drawable.peek_rv_circular_progress_determinate) as LayerDrawable
    val progressDrawable = drawable.findDrawableByLayerId(android.R.id.progress)

    assertTrue(progressDrawable is RotateDrawable)
    val rotateDrawable = progressDrawable as RotateDrawable
    assertEquals(-90f, rotateDrawable.fromDegrees, 0f)
    assertEquals(-90f, rotateDrawable.toDegrees, 0f)
    assertEquals(0.5f, rotateDrawable.pivotX, 0f)
    assertEquals(0.5f, rotateDrawable.pivotY, 0f)
    assertTrue(rotateDrawable.isPivotXRelative)
    assertTrue(rotateDrawable.isPivotYRelative)
  }

  @Test
  fun renderClampsOutOfRangeProgressAndAlpha() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        Column {
          LinearProgressIndicator(progress = 1.5f)
          Image(
            provider = ImageProvider(android.R.drawable.stat_sys_upload),
            contentDescription = "Overscaled",
            alpha = 2f,
          )
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val progressBar = applied.findFirstProgressBar()
    val image = applied.findFirstImageView()

    assertNotNull(progressBar)
    assertEquals(100, progressBar?.progress)
    assertEquals(255, image?.imageAlpha)
  }

  @Test
  fun renderAppliesVisibilityModifier() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Text(
          text = "Hidden",
          modifier = PeekModifier.visibility(Visibility.Gone),
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val text = applied.findText("Hidden")

    assertNotNull(text)
    assertEquals(View.GONE, text?.visibility)
  }

  @Test
  fun renderAppliesImageTintAndAlpha() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Image(
          provider = ImageProvider(android.R.drawable.stat_sys_upload),
          contentDescription = "Upload",
          colorFilter = ColorFilter.tint(ColorProvider(Color.Black)),
          alpha = 0.5f,
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val image = applied.findFirstImageView()

    assertNotNull(image)
    assertEquals("Upload", image?.contentDescription)
    assertEquals(128, image?.imageAlpha)
    assertNotNull(image?.colorFilter)
  }

  @Test
  fun renderAppliesGlanceStyleImageAlphaOverload() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Image(
          ImageProvider(android.R.drawable.stat_sys_upload),
          "Upload",
          0.5f,
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val image = applied.findFirstImageView()

    assertNotNull(image)
    assertEquals(128, image?.imageAlpha)
  }

  @Test
  fun renderSupportsUriAndIconImageProviders() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val icon = AndroidIcon.createWithResource(context, android.R.drawable.stat_sys_upload)
    val uri = Uri.parse("android.resource://android/${android.R.drawable.stat_sys_upload}")
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Row {
          Image(
            provider = ImageProvider(uri),
            contentDescription = "Uri image",
          )
          Image(
            provider = ImageProvider(icon),
            contentDescription = "Icon image",
          )
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))

    assertEquals(2, applied.findAllImageViews().size)
  }

  @Test
  fun renderUsesWrapContentForDefaultImageSize() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Image(
          provider = ImageProvider(android.R.drawable.stat_sys_upload),
          contentDescription = "Upload",
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val image = applied.findFirstImageView()

    assertNotNull(image)
    assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, image?.layoutParams?.width)
    assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, image?.layoutParams?.height)
  }

  @Test
  fun renderAdjustsImageBoundsOnlyForFitWithExplicitWrapDimension() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    val fixedFit =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Image(
          provider = ImageProvider(android.R.drawable.stat_sys_upload),
          contentDescription = "Fixed fit",
          modifier = PeekModifier.size(48.dp),
          contentScale = ContentScale.Fit,
        )
      }.apply(context, FrameLayout(context)).findFirstImageView()
    val wrapFit =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Image(
          provider = ImageProvider(android.R.drawable.stat_sys_upload),
          contentDescription = "Wrap fit",
          modifier = PeekModifier.wrapContentWidth().height(48.dp),
          contentScale = ContentScale.Fit,
        )
      }.apply(context, FrameLayout(context)).findFirstImageView()

    assertNotNull(fixedFit)
    assertNotNull(wrapFit)
    assertEquals(false, fixedFit?.adjustViewBounds)
    assertEquals(true, wrapFit?.adjustViewBounds)
  }

  @Test
  fun renderMarksDecorativeImageAsNotImportantForAccessibility() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Image(
          provider = ImageProvider(android.R.drawable.stat_sys_upload),
          contentDescription = null,
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val image = applied.findFirstImageView()

    assertNotNull(image)
    assertEquals(View.IMPORTANT_FOR_ACCESSIBILITY_NO, image?.importantForAccessibility)
  }

  @Test
  fun renderAppliesImageContentScale() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    val crop = context.renderImageScale(ContentScale.Crop)
    val fit = context.renderImageScale(ContentScale.Fit)
    val fillBounds = context.renderImageScale(ContentScale.FillBounds)

    assertEquals(ImageView.ScaleType.CENTER_CROP, crop?.scaleType)
    assertEquals(ImageView.ScaleType.FIT_CENTER, fit?.scaleType)
    assertEquals(ImageView.ScaleType.FIT_XY, fillBounds?.scaleType)
  }

  @Test
  fun renderComposesContent() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      PeekRemoteViews.render(context, PeekNotificationSize.Collapsed) {
        Text("Async")
      }

    val applied = remoteViews.apply(context, FrameLayout(context))

    assertNotNull(applied.findText("Async"))
  }

  @Test
  fun renderStacksMultipleRootChildrenVertically() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Text("First")
        Text("Second")
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val column = applied.findFirstLinearLayout()

    assertNotNull(column)
    assertEquals(LinearLayout.VERTICAL, column?.orientation)
    assertNotNull(applied.findText("First"))
    assertNotNull(applied.findText("Second"))
  }

  @Test
  fun renderAllowsEmptyContent() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        // No content emitted: must produce a valid, empty RemoteViews instead of timing out.
      }

    val applied = remoteViews.apply(context, FrameLayout(context))

    assertNotNull(applied)
    assertEquals(0, applied.findAllTextViews().size)
  }

}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26])
class PeekRemoteViewsApi26Test {
  @Test
  fun renderEmbedsAndroidRemoteViewsContainerWithPeekChildrenBeforeApi28() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val embedded = RemoteViews(context.packageName, R.layout.peek_rv_column)
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        AndroidRemoteViews(embedded, R.id.peek_main) {
          Text("Child")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val original = embedded.apply(context, FrameLayout(context)) as LinearLayout

    assertNotNull(applied.findText("Child"))
    assertEquals(0, original.childCount)
  }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class PeekRemoteViewsPreAndroid12Test {
  @Test
  fun renderAppliesFixedDimensionsAsMinimumSizeBeforeAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Text(
          text = "Sized",
          modifier = PeekModifier.size(40.dp, 20.dp),
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val textView = applied.findText("Sized")

    assertNotNull(textView)
    assertEquals(40.dp.toPx(context), textView?.minWidth)
    assertEquals(20.dp.toPx(context), textView?.minHeight)
  }

  @Test
  fun renderUsesMatchParentXmlForFillMaxWidthRowBeforeAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Row(modifier = PeekModifier.fillMaxWidth()) {
          Text("One")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val row = applied.findFirstLinearLayout()

    assertNotNull(row)
    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, row?.layoutParams?.width)
  }

  @Test
  fun renderUsesStaticSizingWrapperForFixedRowBeforeAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Row(modifier = PeekModifier.size(40.dp, 20.dp)) {
          Text("One")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val sizeViewId = context.resources.getIdentifier("peek_size", "id", context.packageName)

    assertNotEquals(0, sizeViewId)
    val sizeView = applied.findViewById<TextView>(sizeViewId)
    assertNotNull(sizeView)
    assertEquals(40.dp.toPx(context), sizeView?.minWidth)
    assertEquals(20.dp.toPx(context), sizeView?.minHeight)
  }

  @Test
  fun appliesRowGravityBeforeAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Row(verticalAlignment = Alignment.CenterVertically) { Text("One") }
      }
    val applied = remoteViews.apply(context, FrameLayout(context))
    val row = applied.findFirstLinearLayout()
    assertNotNull(row)
    assertEquals(Gravity.CENTER_VERTICAL, row?.gravity?.and(Gravity.VERTICAL_GRAVITY_MASK))
  }

  @Test
  fun appliesBoxAlignmentBeforeAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Box(modifier = PeekModifier.size(48.dp), contentAlignment = Alignment.Center) {
          Text("C")
        }
      }
    val applied = remoteViews.apply(context, FrameLayout(context))
    assertNotNull(applied.findFirstLinearLayout())
  }

  @Test
  fun appliesTextMaxLinesBeforeAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Text("Status update", maxLines = 1)
      }
    val applied = remoteViews.apply(context, FrameLayout(context))
    val text = applied.findText("Status update")
    assertNotNull(text)
    assertEquals(1, text?.maxLines)
  }

  @Test
  fun appliesImageTintAlphaAndBoundsBeforeAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Image(
          provider = ImageProvider(android.R.drawable.stat_sys_upload),
          contentDescription = "Upload",
          colorFilter = ColorFilter.tint(ColorProvider(Color.Black)),
          alpha = 0.5f,
        )
      }
    val applied = remoteViews.apply(context, FrameLayout(context))
    val image = applied.findFirstImageView()
    assertNotNull(image)
    assertEquals(128, image?.imageAlpha)
    assertNotNull(image?.colorFilter)
  }

  @Test
  fun ignoresTextAlignWithoutCrashingBeforeAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Text("Centered", textAlign = TextAlign.Center)
      }
    val applied = remoteViews.apply(context, FrameLayout(context))
    assertNotNull(applied.findText("Centered"))
  }

  @Test
  fun renderFillsTextWidthBeforeAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Column(modifier = PeekModifier.fillMaxWidth()) {
          Text(
            text = "Filled",
            modifier = PeekModifier.fillMaxWidth(),
          )
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val text = applied.findText("Filled")

    assertNotNull(text)
    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, text?.layoutParams?.width)
  }

  @Test
  fun renderFillsHorizontalDividerWidthBeforeAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Column {
          Text("One")
          HorizontalDivider(thickness = 2.dp, color = ColorProvider(Color.Red))
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val divider = applied.findColorView(Color.Red.toArgb())

    assertNotNull(divider)
    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, divider?.layoutParams?.width)
  }

  @Test
  fun renderFillsVerticalDividerHeightBeforeAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Collapsed) {
        Row {
          Text("One")
          VerticalDivider(thickness = 2.dp, color = ColorProvider(Color.Red))
          Text("Two")
        }
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val divider = applied.findColorView(Color.Red.toArgb())

    assertNotNull(divider)
    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, divider?.layoutParams?.height)
  }

  @Test
  fun renderUsesStaticCheckBoxBackportBeforeAndroid12() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val remoteViews =
      renderBlocking(context, PeekNotificationSize.Expanded) {
        CheckBox(
          checked = true,
          onCheckedChange = actionSendBroadcast(
            Intent("io.github.jakex7.peek.CHECKBOX").setPackage(context.packageName),
          ),
          text = "Done",
        )
      }

    val applied = remoteViews.apply(context, FrameLayout(context))
    val compoundButton = applied.findFirstCompoundButton()
    val wrapper = applied.findViewWithExactTag("peekCompoundButton")

    assertEquals(null, compoundButton)
    assertNotNull(wrapper)
    assertNotNull(applied.findText("Done"))
    assertEquals(true, wrapper?.hasOnClickListeners())
  }
}

private fun renderBlocking(
  context: Context,
  size: PeekNotificationSize,
  content: @Composable @PeekComposable () -> Unit,
): RemoteViews = runBlocking { PeekRemoteViews.render(context, size, content) }

private fun Context.renderImageScale(contentScale: ContentScale): ImageView? {
  val remoteViews =
    renderBlocking(this, PeekNotificationSize.Collapsed) {
      Image(
        provider = ImageProvider(android.R.drawable.stat_sys_upload),
        contentDescription = "Upload",
        modifier = PeekModifier.size(48.dp, 32.dp),
        contentScale = contentScale,
      )
    }
  return remoteViews.apply(this, FrameLayout(this)).findFirstImageView()
}

private fun androidx.compose.ui.unit.Dp.toPx(context: Context): Int =
  TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    value,
    context.resources.displayMetrics,
  ).toInt()

private fun Context.withNightMode(): Context {
  val configuration =
    Configuration(resources.configuration).apply {
      uiMode =
        (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
           Configuration.UI_MODE_NIGHT_YES
    }
  return createConfigurationContext(configuration)
}
