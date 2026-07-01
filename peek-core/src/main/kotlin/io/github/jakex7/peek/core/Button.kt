@file:PeekComposable

package io.github.jakex7.peek.core

import android.app.PendingIntent
import androidx.compose.runtime.Composable
import io.github.jakex7.peek.core.text.EmittableButton

data class ButtonColors(
  val containerColor: ColorProvider,
  val contentColor: ColorProvider,
  val disabledContainerColor: ColorProvider,
  val disabledContentColor: ColorProvider,
)

object ButtonDefaults {
  @Composable
  fun buttonColors(
    containerColor: ColorProvider = PeekTheme.colors.primary,
    contentColor: ColorProvider = PeekTheme.colors.onPrimary,
    disabledContainerColor: ColorProvider = PeekTheme.colors.onSurface.withAlpha(0.12f),
    disabledContentColor: ColorProvider = PeekTheme.colors.onSurface.withAlpha(0.38f),
  ): ButtonColors =
    ButtonColors(
      containerColor = containerColor,
      contentColor = contentColor,
      disabledContainerColor = disabledContainerColor,
      disabledContentColor = disabledContentColor,
    )
}

@Composable
fun Button(
  text: String,
  onClick: PeekAction,
  modifier: PeekModifier = PeekModifier,
  enabled: Boolean = true,
  colors: ButtonColors = ButtonDefaults.buttonColors(),
) {
  val containerColor =
    if (enabled) colors.containerColor else colors.disabledContainerColor
  val contentColor =
    if (enabled) colors.contentColor else colors.disabledContentColor

  PeekNode(
    factory = ::EmittableButton,
    update = {
      setModifier(modifier)
      set(text) { this.text = it }
      set(onClick) { this.onClick = it }
      set(enabled) { this.enabled = it }
      set(containerColor) { this.containerColor = it }
      set(contentColor) { this.color = it }
    },
  )
}

@Composable
fun Button(
  text: String,
  onClick: PendingIntent,
  modifier: PeekModifier = PeekModifier,
  enabled: Boolean = true,
  colors: ButtonColors = ButtonDefaults.buttonColors(),
) {
  Button(
    text = text,
    onClick = actionPendingIntent(onClick),
    modifier = modifier,
    enabled = enabled,
    colors = colors,
  )
}
