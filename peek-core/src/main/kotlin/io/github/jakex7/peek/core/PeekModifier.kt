package io.github.jakex7.peek.core

import android.app.PendingIntent
import androidx.annotation.RestrictTo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

open class PeekModifier internal constructor(
  val elements: List<PeekModifierElement> = emptyList(),
) {
  companion object : PeekModifier()
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
sealed interface PeekModifierElement

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class PaddingModifier(
  val start: Dp,
  val top: Dp,
  val end: Dp,
  val bottom: Dp,
) : PeekModifierElement

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class BackgroundModifier(val color: ColorProvider) : PeekModifierElement

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class WidthModifier(val dimension: Dimension) : PeekModifierElement

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class HeightModifier(val dimension: Dimension) : PeekModifierElement

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class MinWidthModifier(val width: Dp) : PeekModifierElement

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class MinHeightModifier(val height: Dp) : PeekModifierElement

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class VisibilityModifier(val visibility: Visibility) : PeekModifierElement

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class ClickActionModifier(val pendingIntent: PendingIntent) : PeekModifierElement

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class PeekActionModifier(val action: PeekAction) : PeekModifierElement

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data object SelectableGroupModifier : PeekModifierElement

enum class Visibility {
  Visible,
  Invisible,
  Gone,
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
sealed interface Dimension {
  data object Wrap : Dimension
  data object Fill : Dimension
  data class Fixed(val value: Dp) : Dimension
}

fun PeekModifier.then(other: PeekModifier): PeekModifier =
  if (elements.isEmpty()) {
    other
  } else if (other.elements.isEmpty()) {
    this
  } else {
    PeekModifier(elements + other.elements)
  }

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
inline fun <reified T : PeekModifierElement> PeekModifier.find(): T? =
  elements.lastOrNull { it is T } as? T

fun PeekModifier.padding(all: Dp): PeekModifier =
  then(PeekModifier(listOf(PaddingModifier(all, all, all, all))))

fun PeekModifier.padding(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): PeekModifier =
  then(PeekModifier(listOf(PaddingModifier(horizontal, vertical, horizontal, vertical))))

fun PeekModifier.padding(start: Dp, top: Dp, end: Dp, bottom: Dp): PeekModifier =
  then(PeekModifier(listOf(PaddingModifier(start, top, end, bottom))))

fun PeekModifier.background(color: ColorProvider): PeekModifier =
  then(PeekModifier(listOf(BackgroundModifier(color))))

fun PeekModifier.width(width: Dp): PeekModifier =
  then(PeekModifier(listOf(WidthModifier(Dimension.Fixed(width)))))

fun PeekModifier.height(height: Dp): PeekModifier =
  then(PeekModifier(listOf(HeightModifier(Dimension.Fixed(height)))))

fun PeekModifier.defaultMinSize(
  minWidth: Dp = Dp.Unspecified,
  minHeight: Dp = Dp.Unspecified,
): PeekModifier {
  var result = this
  if (minWidth.isSpecified) {
    result = result.then(PeekModifier(listOf(MinWidthModifier(minWidth))))
  }
  if (minHeight.isSpecified) {
    result = result.then(PeekModifier(listOf(MinHeightModifier(minHeight))))
  }
  return result
}

fun PeekModifier.size(size: Dp): PeekModifier =
  size(width = size, height = size)

fun PeekModifier.size(width: Dp, height: Dp): PeekModifier =
  this
    .width(width)
    .height(height)

fun PeekModifier.wrapContentWidth(): PeekModifier =
  then(PeekModifier(listOf(WidthModifier(Dimension.Wrap))))

fun PeekModifier.wrapContentHeight(): PeekModifier =
  then(PeekModifier(listOf(HeightModifier(Dimension.Wrap))))

fun PeekModifier.wrapContentSize(): PeekModifier =
  wrapContentWidth().wrapContentHeight()

fun PeekModifier.fillMaxWidth(): PeekModifier =
  then(PeekModifier(listOf(WidthModifier(Dimension.Fill))))

fun PeekModifier.fillMaxHeight(): PeekModifier =
  then(PeekModifier(listOf(HeightModifier(Dimension.Fill))))

fun PeekModifier.fillMaxSize(): PeekModifier =
  fillMaxWidth().fillMaxHeight()

fun PeekModifier.visibility(visibility: Visibility): PeekModifier =
  then(PeekModifier(listOf(VisibilityModifier(visibility))))

fun PeekModifier.clickable(pendingIntent: PendingIntent): PeekModifier =
  then(PeekModifier(listOf(ClickActionModifier(pendingIntent))))

fun PeekModifier.clickable(onClick: PeekAction): PeekModifier =
  then(PeekModifier(listOf(PeekActionModifier(onClick))))

fun PeekModifier.selectableGroup(): PeekModifier =
  then(PeekModifier(listOf(SelectableGroupModifier)))
