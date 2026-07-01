@file:PeekComposable

package io.github.jakex7.peek.core

import android.graphics.Bitmap
import android.graphics.drawable.Icon as AndroidIcon
import android.net.Uri
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import io.github.jakex7.peek.core.image.EmittableImage

sealed interface ImageProvider {
  data class Resource(@param:DrawableRes val resId: Int) : ImageProvider
  data class BitmapImage(val bitmap: Bitmap) : ImageProvider
  data class UriImage(val uri: Uri) : ImageProvider
  data class IconImage(val icon: AndroidIcon) : ImageProvider
}

data class ColorFilter(
  val tint: ColorProvider,
) {
  companion object {
    fun tint(colorProvider: ColorProvider): ColorFilter =
      ColorFilter(colorProvider)
  }
}

fun ImageProvider(@DrawableRes resId: Int): ImageProvider = ImageProvider.Resource(resId)

fun ImageProvider(bitmap: Bitmap): ImageProvider = ImageProvider.BitmapImage(bitmap)

fun ImageProvider(uri: Uri): ImageProvider = ImageProvider.UriImage(uri)

fun ImageProvider(icon: AndroidIcon): ImageProvider = ImageProvider.IconImage(icon)

enum class ContentScale {
  Crop,
  Fit,
  FillBounds,
}

@Composable
fun Image(
  provider: ImageProvider,
  contentDescription: String?,
  modifier: PeekModifier = PeekModifier,
  contentScale: ContentScale = ContentScale.Fit,
  colorFilter: ColorFilter? = null,
) {
  ImageElement(provider, contentDescription, modifier, contentScale, colorFilter)
}

@Composable
fun Image(
  provider: ImageProvider,
  contentDescription: String?,
  alpha: Float,
  modifier: PeekModifier = PeekModifier,
  contentScale: ContentScale = ContentScale.Fit,
  colorFilter: ColorFilter? = null,
) {
  ImageElement(provider, contentDescription, modifier, contentScale, colorFilter, alpha)
}

@Composable
internal fun ImageElement(
  provider: ImageProvider,
  contentDescription: String?,
  modifier: PeekModifier = PeekModifier,
  contentScale: ContentScale = ContentScale.Fit,
  colorFilter: ColorFilter? = null,
  alpha: Float? = null,
) {
  PeekNode(
    factory = ::EmittableImage,
    update = {
      setModifier(modifier)
      set(provider) { this.provider = it }
      set(contentDescription) { this.contentDescription = it }
      set(colorFilter) { this.colorFilter = it }
      set(alpha) { this.alpha = it }
      set(contentScale) { this.contentScale = it }
    },
  )
}
