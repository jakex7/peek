package io.github.jakex7.peek.core

import androidx.annotation.RestrictTo
import androidx.compose.runtime.AbstractApplier

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class PeekApplier(root: EmittableWithChildren) : AbstractApplier<Emittable>(root) {
  override fun insertBottomUp(index: Int, instance: Emittable) = Unit

  override fun insertTopDown(index: Int, instance: Emittable) {
    currentChildren.add(index, instance)
  }

  override fun move(from: Int, to: Int, count: Int) {
    currentChildren.move(from, to, count)
  }

  override fun onClear() {
    (root as EmittableWithChildren).children.clear()
  }

  override fun remove(index: Int, count: Int) {
    repeat(count) { currentChildren.removeAt(index) }
  }

  private val currentChildren: MutableList<Emittable>
    get() = (current as? EmittableWithChildren)?.children
      ?: error("Current Peek node cannot accept children: ${current::class.qualifiedName}")
}

private fun <T> MutableList<T>.move(from: Int, to: Int, count: Int) {
  if (from == to || count == 0) return
  val moved = subList(from, from + count).toList()
  repeat(count) { removeAt(from) }
  addAll(if (to > from) to - count else to, moved)
}
