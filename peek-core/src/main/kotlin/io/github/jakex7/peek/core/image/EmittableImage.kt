package io.github.jakex7.peek.core.image

import io.github.jakex7.peek.core.ColorFilter
import io.github.jakex7.peek.core.ContentScale
import io.github.jakex7.peek.core.Emittable
import io.github.jakex7.peek.core.ImageProvider
import io.github.jakex7.peek.core.PeekModifier

class EmittableImage : Emittable {
  override var modifier: PeekModifier = PeekModifier
  var provider: ImageProvider? = null
  var contentDescription: String? = null
  var colorFilter: ColorFilter? = null
  var alpha: Float? = null
  var contentScale: ContentScale = ContentScale.Fit

  override fun copy(): Emittable =
    EmittableImage().also {
      it.modifier = modifier
      it.provider = provider
      it.contentDescription = contentDescription
      it.colorFilter = colorFilter
      it.alpha = alpha
      it.contentScale = contentScale
    }
}
