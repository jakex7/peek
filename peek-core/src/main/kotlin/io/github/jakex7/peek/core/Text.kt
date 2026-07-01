@file:PeekComposable

package io.github.jakex7.peek.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import io.github.jakex7.peek.core.text.EmittableText

@Composable
fun Text(
  text: String,
  modifier: PeekModifier = PeekModifier,
  color: ColorProvider = PeekTheme.colors.onSurface,
  fontSize: TextUnit = TextUnit.Unspecified,
  maxLines: Int = Int.MAX_VALUE,
  textAlign: TextAlign? = null,
  fontWeight: FontWeight = FontWeight.Normal,
  fontStyle: FontStyle = FontStyle.Normal,
  textDecoration: TextDecoration = TextDecoration.None,
) {
  require(maxLines > 0) { "maxLines must be greater than zero." }
  require(fontSize == TextUnit.Unspecified || fontSize.type == TextUnitType.Sp) {
    "Peek Text fontSize must use sp units."
  }

  ResolvedText(
    text = text,
    modifier = modifier,
    color = color,
    fontSize = fontSize,
    maxLines = maxLines,
    textAlign = textAlign,
    fontWeight = fontWeight,
    fontStyle = fontStyle,
    textDecoration = textDecoration,
  )
}

@Composable
internal fun ResolvedText(
  text: String,
  modifier: PeekModifier,
  color: ColorProvider,
  fontSize: TextUnit,
  maxLines: Int,
  textAlign: TextAlign?,
  fontWeight: FontWeight,
  fontStyle: FontStyle,
  textDecoration: TextDecoration,
) {
  PeekNode(
    factory = ::EmittableText,
    update = {
      setModifier(modifier)
      set(text) { this.text = it }
      set(color) { this.color = it }
      set(fontSize) { this.fontSize = it }
      set(maxLines) { this.maxLines = it }
      set(textAlign) { this.textAlign = it }
      set(fontWeight) { this.fontWeight = it }
      set(fontStyle) { this.fontStyle = it }
      set(textDecoration) { this.textDecoration = it }
    },
  )
}

@Composable
fun Text(
  text: String,
  modifier: PeekModifier = PeekModifier,
  color: ColorProvider = PeekTheme.colors.onSurface,
  fontSizeSp: Int,
  maxLines: Int = Int.MAX_VALUE,
  textAlign: TextAlign? = null,
  fontWeight: FontWeight = FontWeight.Normal,
  fontStyle: FontStyle = FontStyle.Normal,
  textDecoration: TextDecoration = TextDecoration.None,
) {
  Text(
    text = text,
    modifier = modifier,
    color = color,
    fontSize = fontSizeSp.sp,
    maxLines = maxLines,
    textAlign = textAlign,
    fontWeight = fontWeight,
    fontStyle = fontStyle,
    textDecoration = textDecoration,
  )
}
