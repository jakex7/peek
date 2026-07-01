package io.github.jakex7.peek.appwidget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.SizeF
import android.widget.RemoteViews
import androidx.annotation.RestrictTo
import androidx.annotation.RequiresApi
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.min

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun resolvePeekAppWidgetSizes(
  sizeMode: PeekAppWidgetSizeMode,
  options: Bundle,
  minSize: DpSize,
): List<DpSize> =
  when (sizeMode) {
    PeekAppWidgetSizeMode.Single -> listOf(minSize)
    PeekAppWidgetSizeMode.Exact ->
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        options.extractAllSizes { minSize }
      } else {
        options.extractOrientationSizes().ifEmpty { listOf(minSize) }
      }
    is PeekAppWidgetSizeMode.Responsive ->
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        sizeMode.sizes.sortedBySize()
      } else {
        val smallestSize = sizeMode.sizes.sortedBySize().first()
        options
          .extractOrientationSizes()
          .map { findBestSize(it, sizeMode.sizes) ?: smallestSize }
          .ifEmpty { listOf(smallestSize, smallestSize) }
      }
  }.distinct()

@Suppress("DEPRECATION")
@SuppressLint("PrimitiveInCollection", "ListIterator")
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun Bundle.extractAllSizes(minSize: () -> DpSize): List<DpSize> {
  val sizes = getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES)
  return if (sizes.isNullOrEmpty()) {
    estimateSizes(minSize)
  } else {
    sizes.map { DpSize(it.width.dp, it.height.dp) }
  }
}

@SuppressLint("PrimitiveInCollection")
private fun Bundle.estimateSizes(minSize: () -> DpSize): List<DpSize> {
  val minHeight = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
  val maxHeight = getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 0)
  val minWidth = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)
  val maxWidth = getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 0)
  if (minHeight == 0 || maxHeight == 0 || minWidth == 0 || maxWidth == 0) {
    return listOf(minSize())
  }
  return listOf(DpSize(minWidth.dp, maxHeight.dp), DpSize(maxWidth.dp, minHeight.dp))
}

private fun Bundle.extractLandscapeSize(): DpSize? {
  val minHeight = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
  val maxWidth = getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 0)
  return if (minHeight == 0 || maxWidth == 0) null else DpSize(maxWidth.dp, minHeight.dp)
}

private fun Bundle.extractPortraitSize(): DpSize? {
  val maxHeight = getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 0)
  val minWidth = getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)
  return if (maxHeight == 0 || minWidth == 0) null else DpSize(minWidth.dp, maxHeight.dp)
}

internal fun Bundle.extractOrientationSizes(): List<DpSize> =
  listOfNotNull(extractLandscapeSize(), extractPortraitSize())

internal fun AppWidgetProviderInfo.getMinSize(displayMetrics: DisplayMetrics): DpSize {
  val resolvedMinWidth =
    min(
      minWidth,
      if (resizeMode and AppWidgetProviderInfo.RESIZE_HORIZONTAL != 0) {
        minResizeWidth
      } else {
        Int.MAX_VALUE
      },
    )
  val resolvedMinHeight =
    min(
      minHeight,
      if (resizeMode and AppWidgetProviderInfo.RESIZE_VERTICAL != 0) {
        minResizeHeight
      } else {
        Int.MAX_VALUE
      },
    )
  return DpSize(
    resolvedMinWidth.pixelsToDp(displayMetrics).dp,
    resolvedMinHeight.pixelsToDp(displayMetrics).dp,
  )
}

internal fun Collection<DpSize>.sortedBySize(): List<DpSize> =
  sortedWith(compareBy({ it.width.value * it.height.value }, { it.width.value }))

internal fun findBestSize(widgetSize: DpSize, layoutSizes: Collection<DpSize>): DpSize? =
  layoutSizes
    .mapNotNull { layoutSize ->
      if (layoutSize fitsIn widgetSize) {
        layoutSize to squareDistance(widgetSize, layoutSize)
      } else {
        null
      }
    }
    .minByOrNull { it.second }
    ?.first

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun combineRemoteViewsBySize(
  views: List<Pair<DpSize, RemoteViews>>,
  sizeMode: PeekAppWidgetSizeMode,
): RemoteViews {
  require(views.isNotEmpty()) { "At least one widget RemoteViews is required." }
  if (sizeMode == PeekAppWidgetSizeMode.Single) return views.single().second
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    return RemoteViewsApi31Impl.createRemoteViews(
      views.associate { (size, remoteViews) -> size.toSizeF() to remoteViews },
    )
  }
  require(views.size <= 2) {
    "Pre-Android 12 widgets support at most landscape and portrait RemoteViews."
  }
  return if (views.size == 1) views.single().second else RemoteViews(views[0].second, views[1].second)
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun AppWidgetManager.getWidgetMinSize(
  context: Context,
  id: PeekAppWidgetId,
  options: Bundle,
): DpSize {
  val providerMinSize =
    getAppWidgetInfo(id.appWidgetId)?.getMinSize(context.resources.displayMetrics)
  if (providerMinSize != null && providerMinSize != DpSize.Zero) return providerMinSize
  return options.extractAllSizes { DpSize.Zero }.firstOrNull() ?: DpSize.Zero
}

private infix fun DpSize.fitsIn(other: DpSize): Boolean =
  (ceil(other.width.value) + 1 > width.value) &&
     (ceil(other.height.value) + 1 > height.value)

private fun squareDistance(widgetSize: DpSize, layoutSize: DpSize): Float {
  val dw = widgetSize.width.value - layoutSize.width.value
  val dh = widgetSize.height.value - layoutSize.height.value
  return dw * dw + dh * dh
}

private fun DpSize.toSizeF(): SizeF =
  SizeF(width.value, height.value)

private fun Int.pixelsToDp(displayMetrics: DisplayMetrics): Float =
  this / displayMetrics.density

@RequiresApi(Build.VERSION_CODES.S)
private object RemoteViewsApi31Impl {
  fun createRemoteViews(sizeMap: Map<SizeF, RemoteViews>): RemoteViews =
    RemoteViews(sizeMap)
}
