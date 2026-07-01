@file:PeekComposable

package io.github.jakex7.peek.core

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun Icon(
  provider: ImageProvider,
  contentDescription: String?,
  modifier: PeekModifier = PeekModifier.size(24.dp),
  tint: ColorProvider? = null,
  alpha: Float = 1f,
) {
  Image(
    provider = provider,
    contentDescription = contentDescription,
    modifier = modifier,
    colorFilter = tint?.let(ColorFilter::tint),
    alpha = alpha,
  )
}

@Composable
fun Icon(
  @DrawableRes resId: Int,
  contentDescription: String?,
  modifier: PeekModifier = PeekModifier.size(24.dp),
  tint: ColorProvider? = null,
  alpha: Float = 1f,
) {
  Icon(
    provider = ImageProvider(resId),
    contentDescription = contentDescription,
    modifier = modifier,
    tint = tint,
    alpha = alpha,
  )
}
