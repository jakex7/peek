package io.github.jakex7.peek.runtime

import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.jakex7.peek.core.BackgroundModifier
import io.github.jakex7.peek.core.Badge
import io.github.jakex7.peek.core.Button
import io.github.jakex7.peek.core.ButtonDefaults
import io.github.jakex7.peek.core.CheckBox
import io.github.jakex7.peek.core.CircularProgressIndicator
import io.github.jakex7.peek.core.Column
import io.github.jakex7.peek.core.ColorProvider
import io.github.jakex7.peek.core.Dimension
import io.github.jakex7.peek.core.HeightModifier
import io.github.jakex7.peek.core.HorizontalDivider
import io.github.jakex7.peek.core.Icon
import io.github.jakex7.peek.core.ImageProvider
import io.github.jakex7.peek.core.LinearProgressIndicator
import io.github.jakex7.peek.core.PeekModifier
import io.github.jakex7.peek.core.PeekRoot
import io.github.jakex7.peek.core.PeekTheme
import io.github.jakex7.peek.core.RadioButton
import io.github.jakex7.peek.core.Switch
import io.github.jakex7.peek.core.Text
import io.github.jakex7.peek.core.VerticalDivider
import io.github.jakex7.peek.core.WidthModifier
import io.github.jakex7.peek.core.actionSendBroadcast
import io.github.jakex7.peek.core.find
import io.github.jakex7.peek.core.height
import io.github.jakex7.peek.core.image.EmittableImage
import io.github.jakex7.peek.core.layout.EmittableDivider
import io.github.jakex7.peek.core.layout.EmittableColumn
import io.github.jakex7.peek.core.padding
import io.github.jakex7.peek.core.progress.EmittableCircularProgressIndicator
import io.github.jakex7.peek.core.peekColors
import io.github.jakex7.peek.core.progress.EmittableLinearProgressIndicator
import io.github.jakex7.peek.core.singleAs
import io.github.jakex7.peek.core.singleChild
import io.github.jakex7.peek.core.controls.EmittableCheckBox
import io.github.jakex7.peek.core.controls.EmittableRadioButton
import io.github.jakex7.peek.core.controls.EmittableSwitch
import io.github.jakex7.peek.core.text.EmittableButton
import io.github.jakex7.peek.core.text.EmittableText
import io.github.jakex7.peek.core.withAlpha
import io.github.jakex7.peek.core.width
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class PeekCompositionTest {
  @Test
  fun compositionProducesEmittableTree() = runBlocking {
    val root =
      PeekComposition.render {
        Column(modifier = PeekModifier.padding(12.dp)) {
          Text("Syncing")
        }
      }

    val column = root.singleChild<EmittableColumn>()
    assertEquals(1, column.children.size)
    assertEquals("Syncing", column.children.singleAs<EmittableText>().text)
  }

  @Test
  fun compositionAllowsEmptyContent() = runBlocking {
    val root = PeekComposition.render { }

    assertTrue(root.children.isEmpty())
  }

  @Test
  fun compositionAllowsConditionallyEmptyContent() = runBlocking {
    val show = false
    val root =
      PeekComposition.render {
        if (show) {
          Text("Never emitted")
        }
      }

    assertTrue(root.children.isEmpty())
  }

  @Test
  fun compositionProducesButtonNode() = runBlocking {
    val root =
      PeekComposition.render {
        Button(
          text = "Pause",
          onClick = actionSendBroadcast(Intent("io.github.jakex7.peek.BUTTON")),
          enabled = false,
        )
      }

    val button = root.singleChild<EmittableButton>()
    assertEquals("Pause", button.text)
    assertEquals(false, button.enabled)
  }

  @Test
  fun componentsUseMaterial3ThemeColorRolesByDefault() = runBlocking {
    val primary = ColorProvider(Color(0xFF010101))
    val onPrimary = ColorProvider(Color(0xFF111111))
    val error = ColorProvider(Color(0xFF020202))
    val onError = ColorProvider(Color(0xFF030303))
    val outlineVariant = ColorProvider(Color(0xFF040404))
    val surfaceContainerHighest = ColorProvider(Color(0xFF050505))
    val root =
      PeekComposition.render {
        PeekTheme(
          colors =
            peekColors(
              primary = primary,
              onPrimary = onPrimary,
              error = error,
              onError = onError,
              outlineVariant = outlineVariant,
              surfaceContainerHighest = surfaceContainerHighest,
            ),
        ) {
          Button(text = "Open", onClick = actionSendBroadcast(Intent("OPEN")))
          Badge(text = "3")
          CheckBox(checked = true, onCheckedChange = null, text = "Enabled")
          HorizontalDivider()
          LinearProgressIndicator(progress = 0.5f)
        }
      }

    val column = root.singleChild<EmittableColumn>()
    val badge = column.children[1] as EmittableText
    val checkBox = column.children[2] as EmittableCheckBox
    val divider = column.children[3] as EmittableDivider
    val progress = column.children[4] as EmittableLinearProgressIndicator

    assertEquals(error, badge.modifier.find<BackgroundModifier>()?.color)
    assertEquals(onError, badge.color)
    assertEquals(primary, checkBox.color)
    assertEquals(outlineVariant, divider.color)
    assertEquals(primary, progress.color)
    assertEquals(surfaceContainerHighest, progress.trackColor)
  }

  @Test
  fun buttonUsesThemeButtonColorsByDefault() = runBlocking {
    val primary = ColorProvider(Color(0xFF010101))
    val onPrimary = ColorProvider(Color(0xFF020202))
    val root =
      PeekComposition.render {
        PeekTheme(colors = peekColors(primary = primary, onPrimary = onPrimary)) {
          Button(
            text = "Open",
            onClick = actionSendBroadcast(Intent("OPEN")),
          )
        }
      }

    val button = root.singleChild<EmittableButton>()

    assertEquals(primary, button.containerColor)
    assertEquals(onPrimary, button.color)
  }

  @Test
  fun disabledButtonUsesThemeButtonColorsByDefault() = runBlocking {
    val onSurface = ColorProvider(Color(0xFF030303))
    val root =
      PeekComposition.render {
        PeekTheme(colors = peekColors(onSurface = onSurface)) {
          Button(
            text = "Open",
            onClick = actionSendBroadcast(Intent("OPEN")),
            enabled = false,
          )
        }
      }

    val button = root.singleChild<EmittableButton>()

    assertEquals(onSurface.withAlpha(0.12f), button.containerColor)
    assertEquals(onSurface.withAlpha(0.38f), button.color)
  }

  @Test
  fun composableColorParametersAcceptThemeColorProviders() = runBlocking {
    val primary = ColorProvider(Color(0xFF010101))
    val onPrimary = ColorProvider(Color(0xFF020202))
    val primaryContainer = ColorProvider(Color(0xFF030303))
    val onPrimaryContainer = ColorProvider(Color(0xFF040404))
    val secondary = ColorProvider(Color(0xFF050505))
    val tertiary = ColorProvider(Color(0xFF060606))
    val error = ColorProvider(Color(0xFF070707))
    val outline = ColorProvider(Color(0xFF080808))
    val outlineVariant = ColorProvider(Color(0xFF090909))
    val surfaceContainerHigh = ColorProvider(Color(0xFF0A0A0A))
    val surfaceContainerHighest = ColorProvider(Color(0xFF0B0B0B))
    val root =
      PeekComposition.render {
        PeekTheme(
          colors =
            peekColors(
              primary = primary,
              onPrimary = onPrimary,
              primaryContainer = primaryContainer,
              onPrimaryContainer = onPrimaryContainer,
              secondary = secondary,
              tertiary = tertiary,
              error = error,
              outline = outline,
              outlineVariant = outlineVariant,
              surfaceContainerHigh = surfaceContainerHigh,
              surfaceContainerHighest = surfaceContainerHighest,
            ),
        ) {
          Text("Themed", color = PeekTheme.colors.onPrimary)
          Badge(
            text = "LIVE",
            color = PeekTheme.colors.primaryContainer,
            contentColor = PeekTheme.colors.onPrimaryContainer,
          )
          CheckBox(checked = true, onCheckedChange = null, color = PeekTheme.colors.secondary)
          Switch(checked = true, onCheckedChange = null, color = PeekTheme.colors.tertiary)
          RadioButton(checked = true, onClick = null, color = PeekTheme.colors.error)
          HorizontalDivider(color = PeekTheme.colors.outline)
          VerticalDivider(color = PeekTheme.colors.outlineVariant)
          LinearProgressIndicator(
            color = PeekTheme.colors.primary,
            trackColor = PeekTheme.colors.surfaceContainerHighest,
          )
          CircularProgressIndicator(
            color = PeekTheme.colors.secondary,
            trackColor = PeekTheme.colors.surfaceContainerHigh,
          )
          Icon(
            provider = ImageProvider(1),
            contentDescription = null,
            tint = PeekTheme.colors.onPrimary,
          )
        }
      }

    val column = root.singleChild<EmittableColumn>()

    assertEquals(onPrimary, (column.children[0] as EmittableText).color)
    assertEquals(onPrimaryContainer, (column.children[1] as EmittableText).color)
    assertEquals(primaryContainer, (column.children[1] as EmittableText).modifier.find<BackgroundModifier>()?.color)
    assertEquals(secondary, (column.children[2] as EmittableCheckBox).color)
    assertEquals(tertiary, (column.children[3] as EmittableSwitch).color)
    assertEquals(error, (column.children[4] as EmittableRadioButton).color)
    assertEquals(outline, (column.children[5] as EmittableDivider).color)
    assertEquals(outlineVariant, (column.children[6] as EmittableDivider).color)
    assertEquals(primary, (column.children[7] as EmittableLinearProgressIndicator).color)
    assertEquals(
      surfaceContainerHighest,
      (column.children[7] as EmittableLinearProgressIndicator).trackColor,
    )
    assertEquals(secondary, (column.children[8] as EmittableCircularProgressIndicator).color)
    assertEquals(
      surfaceContainerHigh,
      (column.children[8] as EmittableCircularProgressIndicator).trackColor,
    )
    assertEquals(onPrimary, (column.children[9] as EmittableImage).colorFilter?.tint)
  }

  @Test
  fun circularProgressIndicatorCapturesTrackColor() = runBlocking {
    val color = ColorProvider(Color.Red)
    val trackColor = ColorProvider(Color.Blue)
    val root =
      PeekComposition.render {
        CircularProgressIndicator(
          progress = 0.25f,
          color = color,
          trackColor = trackColor,
        )
      }

    val progress = root.singleChild<EmittableCircularProgressIndicator>()

    assertEquals(0.25f, progress.progress)
    assertEquals(color, progress.color)
    assertEquals(trackColor, progress.trackColor)
  }

  @Test
  fun buttonUsesButtonColorsForEnabledState() = runBlocking {
    val containerColor = ColorProvider(Color(0xFF654321))
    val contentColor = ColorProvider(Color(0xFF123456))
    val root =
      PeekComposition.render {
        Button(
          text = "Open",
          onClick = actionSendBroadcast(Intent("OPEN")),
          colors =
            ButtonDefaults.buttonColors(
              containerColor = containerColor,
              contentColor = contentColor,
            ),
        )
      }

    val button = root.singleChild<EmittableButton>()

    assertEquals(containerColor, button.containerColor)
    assertEquals(contentColor, button.color)
  }

  @Test
  fun buttonUsesButtonColorsForDisabledState() = runBlocking {
    val disabledContainerColor = ColorProvider(Color(0xFF222222))
    val disabledContentColor = ColorProvider(Color(0xFF333333))
    val root =
      PeekComposition.render {
        Button(
          text = "Open",
          onClick = actionSendBroadcast(Intent("OPEN")),
          enabled = false,
          colors =
            ButtonDefaults.buttonColors(
              disabledContainerColor = disabledContainerColor,
              disabledContentColor = disabledContentColor,
            ),
        )
      }

    val button = root.singleChild<EmittableButton>()

    assertEquals(disabledContainerColor, button.containerColor)
    assertEquals(disabledContentColor, button.color)
  }

  @Test
  fun normalizationWrapsMultipleRootChildrenInColumn() {
    val root =
      PeekRoot().apply {
        children += EmittableText().apply { text = "One" }
        children += EmittableText().apply { text = "Two" }
      }

    val normalized = PeekTreeNormalizer.normalize(root)

    assertEquals(1, normalized.children.size)
    assertTrue(normalized.children.single() is EmittableColumn)
  }

  @Test
  fun normalizationDoesNotPromoteFixedChildDimensionsToFillParentDimensions() {
    val parent =
      EmittableColumn().apply {
        children +=
          EmittableText().apply {
            text = "Sized"
            modifier = PeekModifier.width(40.dp).height(20.dp)
          }
      }
    val root = PeekRoot().apply { children += parent }

    val normalizedParent = PeekTreeNormalizer.normalize(root).singleChild<EmittableColumn>()

    assertEquals(null, normalizedParent.modifier.find<WidthModifier>())
    assertEquals(null, normalizedParent.modifier.find<HeightModifier>())
    val child = normalizedParent.singleChild<EmittableText>()
    assertEquals(Dimension.Fixed(40.dp), child.modifier.find<WidthModifier>()?.dimension)
    assertEquals(Dimension.Fixed(20.dp), child.modifier.find<HeightModifier>()?.dimension)
  }
}
