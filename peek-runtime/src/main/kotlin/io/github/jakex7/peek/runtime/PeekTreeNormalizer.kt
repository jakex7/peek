package io.github.jakex7.peek.runtime

import androidx.annotation.RestrictTo
import io.github.jakex7.peek.core.Dimension
import io.github.jakex7.peek.core.Emittable
import io.github.jakex7.peek.core.EmittableWithChildren
import io.github.jakex7.peek.core.HeightModifier
import io.github.jakex7.peek.core.PeekModifier
import io.github.jakex7.peek.core.PeekRoot
import io.github.jakex7.peek.core.WidthModifier
import io.github.jakex7.peek.core.fillMaxHeight
import io.github.jakex7.peek.core.fillMaxWidth
import io.github.jakex7.peek.core.find
import io.github.jakex7.peek.core.layout.EmittableBox
import io.github.jakex7.peek.core.layout.EmittableColumn
import io.github.jakex7.peek.core.then

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object PeekTreeNormalizer {
  fun normalize(root: PeekRoot): PeekRoot =
    (root.copy() as PeekRoot).also { copied ->
      coerceToSingleRootChild(copied)
      normalizeChildren(copied)
    }

  private fun coerceToSingleRootChild(root: PeekRoot) {
    if (root.children.size <= 1) return

    // The notification root hosts a single child. Stack multiple top-level siblings in a Column so
    // they flow vertically; a Box (FrameLayout) would overlap them on top of each other.
    val wrapper = EmittableColumn()
    wrapper.children += root.children
    root.children.clear()
    root.children += wrapper
  }

  private fun normalizeChildren(parent: EmittableWithChildren) {
    parent.children.forEach { child ->
      if (child is EmittableWithChildren) normalizeChildren(child)
    }
    collapseRedundantFillBoxes(parent)
    parent.modifier = normalizeFillFromChildren(parent.modifier, parent.children)
  }

  private fun collapseRedundantFillBoxes(parent: EmittableWithChildren) {
    parent.children.forEach { child ->
      if (child is EmittableBox) {
        child.collapseRedundantFillBoxChild()
      }
    }
  }

  private fun EmittableBox.collapseRedundantFillBoxChild() {
    while (true) {
      val childBox = children.singleOrNull() as? EmittableBox ?: return
      if (contentAlignment != childBox.contentAlignment) return
      if (!childBox.modifier.isOnlyFillMaxSize()) return

      children.clear()
      children += childBox.children
    }
  }

  private fun normalizeFillFromChildren(
    modifier: PeekModifier,
    children: List<Emittable>,
  ): PeekModifier {
    val hasWidth = modifier.find<WidthModifier>() != null
    val hasHeight = modifier.find<HeightModifier>() != null
    val childFillsWidth =
      children.any { it.modifier.find<WidthModifier>()?.dimension == Dimension.Fill }
    val childFillsHeight =
      children.any { it.modifier.find<HeightModifier>()?.dimension == Dimension.Fill }

    var normalized = modifier
    if (!hasWidth && childFillsWidth) {
      normalized = normalized.then(PeekModifier.fillMaxWidth())
    }
    if (!hasHeight && childFillsHeight) {
      normalized = normalized.then(PeekModifier.fillMaxHeight())
    }
    return normalized
  }

  private fun PeekModifier.isOnlyFillMaxSize(): Boolean {
    val fillsWidth = find<WidthModifier>()?.dimension == Dimension.Fill
    val fillsHeight = find<HeightModifier>()?.dimension == Dimension.Fill
    return fillsWidth &&
      fillsHeight &&
      elements.all { it is WidthModifier || it is HeightModifier }
  }
}
