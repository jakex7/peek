package io.github.jakex7.peek.core.layout

import io.github.jakex7.peek.core.Alignment
import io.github.jakex7.peek.core.ColorProvider
import io.github.jakex7.peek.core.DividerOrientation
import io.github.jakex7.peek.core.Emittable
import io.github.jakex7.peek.core.EmittableWithChildren
import io.github.jakex7.peek.core.PeekModifier
import io.github.jakex7.peek.core.PeekThemeDefaults

class EmittableBox : EmittableWithChildren() {
  override var modifier: PeekModifier = PeekModifier
  var contentAlignment: Alignment = Alignment.TopStart

  override fun copy(): Emittable =
    EmittableBox().also {
      it.modifier = modifier
      it.contentAlignment = contentAlignment
      it.children += children.map { child -> child.copy() }
    }
}

class EmittableRow : EmittableWithChildren() {
  override var modifier: PeekModifier = PeekModifier
  var horizontalAlignment: Alignment.Horizontal = Alignment.Start
  var verticalAlignment: Alignment.Vertical = Alignment.Top

  override fun copy(): Emittable =
    EmittableRow().also {
      it.modifier = modifier
      it.horizontalAlignment = horizontalAlignment
      it.verticalAlignment = verticalAlignment
      it.children += children.map { child -> child.copy() }
    }
}

class EmittableColumn : EmittableWithChildren() {
  override var modifier: PeekModifier = PeekModifier
  var verticalAlignment: Alignment.Vertical = Alignment.Top
  var horizontalAlignment: Alignment.Horizontal = Alignment.Start

  override fun copy(): Emittable =
    EmittableColumn().also {
      it.modifier = modifier
      it.verticalAlignment = verticalAlignment
      it.horizontalAlignment = horizontalAlignment
      it.children += children.map { child -> child.copy() }
    }
}

class EmittableSpacer : Emittable {
  override var modifier: PeekModifier = PeekModifier

  override fun copy(): Emittable =
    EmittableSpacer().also { it.modifier = modifier }
}

class EmittableDivider : Emittable {
  override var modifier: PeekModifier = PeekModifier
  var orientation: DividerOrientation = DividerOrientation.Horizontal
  var color: ColorProvider = PeekThemeDefaults.colors.outlineVariant

  override fun copy(): Emittable =
    EmittableDivider().also {
      it.modifier = modifier
      it.orientation = orientation
      it.color = color
    }
}
