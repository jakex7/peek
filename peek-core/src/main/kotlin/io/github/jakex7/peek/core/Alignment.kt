package io.github.jakex7.peek.core

data class Alignment(
  val horizontal: Horizontal,
  val vertical: Vertical,
) {
  enum class Horizontal {
    Start,
    CenterHorizontally,
    End,
  }

  enum class Vertical {
    Top,
    CenterVertically,
    Bottom,
  }

  companion object {
    val TopStart = Alignment(Horizontal.Start, Vertical.Top)
    val TopCenter = Alignment(Horizontal.CenterHorizontally, Vertical.Top)
    val TopEnd = Alignment(Horizontal.End, Vertical.Top)
    val CenterStart = Alignment(Horizontal.Start, Vertical.CenterVertically)
    val Center = Alignment(Horizontal.CenterHorizontally, Vertical.CenterVertically)
    val CenterEnd = Alignment(Horizontal.End, Vertical.CenterVertically)
    val BottomStart = Alignment(Horizontal.Start, Vertical.Bottom)
    val BottomCenter = Alignment(Horizontal.CenterHorizontally, Vertical.Bottom)
    val BottomEnd = Alignment(Horizontal.End, Vertical.Bottom)

    val Start = Horizontal.Start
    val CenterHorizontally = Horizontal.CenterHorizontally
    val End = Horizontal.End

    val Top = Vertical.Top
    val CenterVertically = Vertical.CenterVertically
    val Bottom = Vertical.Bottom
  }
}

typealias HorizontalAlignment = Alignment.Horizontal
typealias VerticalAlignment = Alignment.Vertical
