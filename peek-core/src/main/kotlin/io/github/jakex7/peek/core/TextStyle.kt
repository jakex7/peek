package io.github.jakex7.peek.core

enum class FontWeight {
  Normal,
  Bold,
}

enum class FontStyle {
  Normal,
  Italic,
}

data class TextDecoration(
  val underline: Boolean = false,
  val lineThrough: Boolean = false,
) {
  companion object {
    val None = TextDecoration()
    val Underline = TextDecoration(underline = true)
    val LineThrough = TextDecoration(lineThrough = true)

    fun combine(vararg decorations: TextDecoration): TextDecoration =
      TextDecoration(
        underline = decorations.any { it.underline },
        lineThrough = decorations.any { it.lineThrough },
      )
  }
}
