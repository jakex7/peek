@file:PeekComposable

package io.github.jakex7.peek.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.jakex7.peek.core.layout.EmittableDivider

enum class DividerOrientation {
  Horizontal,
  Vertical,
}

@Composable
fun HorizontalDivider(
  modifier: PeekModifier = PeekModifier.fillMaxWidth(),
  thickness: Dp = 1.dp,
  color: ColorProvider = PeekTheme.colors.outlineVariant,
) {
  Divider(
    orientation = DividerOrientation.Horizontal,
    modifier = modifier.height(thickness),
    color = color,
  )
}

@Composable
fun VerticalDivider(
  modifier: PeekModifier = PeekModifier.fillMaxHeight(),
  thickness: Dp = 1.dp,
  color: ColorProvider = PeekTheme.colors.outlineVariant,
) {
  Divider(
    orientation = DividerOrientation.Vertical,
    modifier = modifier.width(thickness),
    color = color,
  )
}

@Composable
private fun Divider(
  orientation: DividerOrientation,
  modifier: PeekModifier,
  color: ColorProvider,
) {
  PeekNode(
    factory = ::EmittableDivider,
    update = {
      setModifier(modifier)
      set(orientation) { this.orientation = it }
      set(color) { this.color = it }
    },
  )
}
