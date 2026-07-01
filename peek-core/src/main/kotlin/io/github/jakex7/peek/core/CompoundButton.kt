@file:PeekComposable

package io.github.jakex7.peek.core

import android.app.PendingIntent
import androidx.compose.runtime.Composable
import io.github.jakex7.peek.core.controls.EmittableCheckBox
import io.github.jakex7.peek.core.controls.EmittableRadioButton
import io.github.jakex7.peek.core.controls.EmittableSwitch

@Composable
fun CheckBox(
  checked: Boolean,
  onCheckedChange: PeekAction?,
  modifier: PeekModifier = PeekModifier,
  text: String = "",
  enabled: Boolean = true,
  color: ColorProvider = PeekTheme.colors.primary,
) {
  PeekNode(
    factory = ::EmittableCheckBox,
    update = {
      setModifier(modifier)
      set(checked) { this.checked = it }
      set(onCheckedChange) { this.onClick = it }
      set(text) { this.text = it }
      set(enabled) { this.enabled = it }
      set(color) { this.color = it }
    },
  )
}

@Composable
fun CheckBox(
  checked: Boolean,
  onCheckedChange: PendingIntent,
  modifier: PeekModifier = PeekModifier,
  text: String = "",
  enabled: Boolean = true,
  color: ColorProvider = PeekTheme.colors.primary,
) {
  CheckBox(
    checked = checked,
    onCheckedChange = actionPendingIntent(onCheckedChange),
    modifier = modifier,
    text = text,
    enabled = enabled,
    color = color,
  )
}

@Composable
fun Switch(
  checked: Boolean,
  onCheckedChange: PeekAction?,
  modifier: PeekModifier = PeekModifier,
  text: String = "",
  enabled: Boolean = true,
  color: ColorProvider = PeekTheme.colors.primary,
) {
  PeekNode(
    factory = ::EmittableSwitch,
    update = {
      setModifier(modifier)
      set(checked) { this.checked = it }
      set(onCheckedChange) { this.onClick = it }
      set(text) { this.text = it }
      set(enabled) { this.enabled = it }
      set(color) { this.color = it }
    },
  )
}

@Composable
fun Switch(
  checked: Boolean,
  onCheckedChange: PendingIntent,
  modifier: PeekModifier = PeekModifier,
  text: String = "",
  enabled: Boolean = true,
  color: ColorProvider = PeekTheme.colors.primary,
) {
  Switch(
    checked = checked,
    onCheckedChange = actionPendingIntent(onCheckedChange),
    modifier = modifier,
    text = text,
    enabled = enabled,
    color = color,
  )
}

@Composable
fun RadioButton(
  checked: Boolean,
  onClick: PeekAction?,
  modifier: PeekModifier = PeekModifier,
  text: String = "",
  enabled: Boolean = true,
  color: ColorProvider = PeekTheme.colors.primary,
) {
  PeekNode(
    factory = ::EmittableRadioButton,
    update = {
      setModifier(modifier)
      set(checked) { this.checked = it }
      set(onClick) { this.onClick = it }
      set(text) { this.text = it }
      set(enabled) { this.enabled = it }
      set(color) { this.color = it }
    },
  )
}

@Composable
fun RadioButton(
  checked: Boolean,
  onClick: PendingIntent,
  modifier: PeekModifier = PeekModifier,
  text: String = "",
  enabled: Boolean = true,
  color: ColorProvider = PeekTheme.colors.primary,
) {
  RadioButton(
    checked = checked,
    onClick = actionPendingIntent(onClick),
    modifier = modifier,
    text = text,
    enabled = enabled,
    color = color,
  )
}
