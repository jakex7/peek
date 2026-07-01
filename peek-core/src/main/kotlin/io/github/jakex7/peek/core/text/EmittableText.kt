package io.github.jakex7.peek.core.text

import androidx.compose.ui.unit.TextUnit
import io.github.jakex7.peek.core.ColorProvider
import io.github.jakex7.peek.core.Emittable
import io.github.jakex7.peek.core.FontStyle
import io.github.jakex7.peek.core.FontWeight
import io.github.jakex7.peek.core.PeekModifier
import io.github.jakex7.peek.core.PeekThemeDefaults
import io.github.jakex7.peek.core.TextAlign
import io.github.jakex7.peek.core.TextDecoration

class EmittableText : Emittable {
  override var modifier: PeekModifier = PeekModifier
  var text: String = ""
  var color: ColorProvider = PeekThemeDefaults.colors.onSurface
  var fontSize: TextUnit = TextUnit.Unspecified
  var maxLines: Int = Int.MAX_VALUE
  var textAlign: TextAlign? = null
  var fontWeight: FontWeight = FontWeight.Normal
  var fontStyle: FontStyle = FontStyle.Normal
  var textDecoration: TextDecoration = TextDecoration.None

  override fun copy(): Emittable =
    EmittableText().also {
      it.modifier = modifier
      it.text = text
      it.color = color
      it.fontSize = fontSize
      it.maxLines = maxLines
      it.textAlign = textAlign
      it.fontWeight = fontWeight
      it.fontStyle = fontStyle
      it.textDecoration = textDecoration
    }
}
