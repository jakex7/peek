package io.github.jakex7.peek.core.controls

import io.github.jakex7.peek.core.ColorProvider
import io.github.jakex7.peek.core.Emittable
import io.github.jakex7.peek.core.PeekAction
import io.github.jakex7.peek.core.PeekModifier
import io.github.jakex7.peek.core.PeekThemeDefaults

abstract class EmittableCompoundButton : Emittable {
  override var modifier: PeekModifier = PeekModifier
  var checked: Boolean = false
  var onClick: PeekAction? = null
  var text: String = ""
  var enabled: Boolean = true
  var color: ColorProvider = PeekThemeDefaults.colors.primary

  protected fun copyTo(target: EmittableCompoundButton) {
    target.modifier = modifier
    target.checked = checked
    target.onClick = onClick
    target.text = text
    target.enabled = enabled
    target.color = color
  }
}

class EmittableCheckBox : EmittableCompoundButton() {
  override fun copy(): Emittable =
    EmittableCheckBox().also(::copyTo)
}

class EmittableSwitch : EmittableCompoundButton() {
  override fun copy(): Emittable =
    EmittableSwitch().also(::copyTo)
}

class EmittableRadioButton : EmittableCompoundButton() {
  override fun copy(): Emittable =
    EmittableRadioButton().also(::copyTo)
}
