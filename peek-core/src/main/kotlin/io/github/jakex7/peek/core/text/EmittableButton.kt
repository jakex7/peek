package io.github.jakex7.peek.core.text

import androidx.compose.ui.graphics.Color
import io.github.jakex7.peek.core.ColorProvider
import io.github.jakex7.peek.core.Emittable
import io.github.jakex7.peek.core.PeekAction
import io.github.jakex7.peek.core.PeekModifier

class EmittableButton : Emittable {
  override var modifier: PeekModifier = PeekModifier
  var text: String = ""
  var onClick: PeekAction? = null
  var enabled: Boolean = true
  var containerColor: ColorProvider = ColorProvider(Color.Unspecified)
  var color: ColorProvider = ColorProvider(Color.Unspecified)

  override fun copy(): Emittable =
    EmittableButton().also {
      it.modifier = modifier
      it.text = text
      it.onClick = onClick
      it.enabled = enabled
      it.containerColor = containerColor
      it.color = color
    }
}
