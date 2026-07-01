package io.github.jakex7.peek.appwidget

import androidx.compose.ui.unit.DpSize

sealed interface PeekAppWidgetSizeMode {
  data object Single : PeekAppWidgetSizeMode

  data object Exact : PeekAppWidgetSizeMode

  class Responsive(
    val sizes: Set<DpSize>,
  ) : PeekAppWidgetSizeMode {
    init {
      require(sizes.isNotEmpty()) { "Responsive widget sizes cannot be empty." }
    }

    override fun equals(other: Any?): Boolean =
      other is Responsive && sizes == other.sizes

    override fun hashCode(): Int =
      sizes.hashCode()

    override fun toString(): String =
      "PeekAppWidgetSizeMode.Responsive(sizes=$sizes)"
  }
}
