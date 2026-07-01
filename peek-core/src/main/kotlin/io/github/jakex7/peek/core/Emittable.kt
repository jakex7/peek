package io.github.jakex7.peek.core

import androidx.annotation.RestrictTo

interface Emittable {
  var modifier: PeekModifier

  fun copy(): Emittable
}

abstract class EmittableWithChildren : Emittable {
  val children: MutableList<Emittable> = mutableListOf()
}

class PeekRoot : EmittableWithChildren() {
  override var modifier: PeekModifier = PeekModifier

  override fun copy(): Emittable =
    PeekRoot().also { root ->
      root.modifier = modifier
      root.children += children.map { it.copy() }
    }
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
inline fun <reified T : Emittable> EmittableWithChildren.singleChild(): T =
  children.single() as T

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
inline fun <reified T : Emittable> List<Emittable>.singleAs(): T =
  single() as T
