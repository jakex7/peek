@file:PeekComposable

package io.github.jakex7.peek.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Badge(
  text: String,
  modifier: PeekModifier = PeekModifier,
  color: ColorProvider = PeekTheme.colors.error,
  contentColor: ColorProvider = PeekTheme.colors.onError,
  fontSize: TextUnit = 12.sp,
) {
  val badgeModifier =
    modifier
      .background(color)
      .padding(horizontal = 6.dp, vertical = 2.dp)

  ResolvedText(
    text = text,
    modifier = badgeModifier,
    color = contentColor,
    fontSize = fontSize,
    maxLines = 1,
    textAlign = null,
    fontWeight = FontWeight.Bold,
    fontStyle = FontStyle.Normal,
    textDecoration = TextDecoration.None,
  )
}
