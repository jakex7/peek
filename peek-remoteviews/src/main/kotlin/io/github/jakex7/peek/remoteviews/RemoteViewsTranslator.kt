package io.github.jakex7.peek.remoteviews

import android.app.PendingIntent
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import io.github.jakex7.peek.core.Alignment
import io.github.jakex7.peek.core.BackgroundModifier
import io.github.jakex7.peek.core.ClickActionModifier
import io.github.jakex7.peek.core.ColorProvider
import io.github.jakex7.peek.core.ContentScale
import io.github.jakex7.peek.core.Dimension
import io.github.jakex7.peek.core.DividerOrientation
import io.github.jakex7.peek.core.Emittable
import io.github.jakex7.peek.core.EmittableWithChildren
import io.github.jakex7.peek.core.FontStyle
import io.github.jakex7.peek.core.FontWeight
import io.github.jakex7.peek.core.HeightModifier
import io.github.jakex7.peek.core.ImageProvider
import io.github.jakex7.peek.core.MinHeightModifier
import io.github.jakex7.peek.core.MinWidthModifier
import io.github.jakex7.peek.core.PaddingModifier
import io.github.jakex7.peek.core.PeekAction
import io.github.jakex7.peek.core.PeekActionModifier
import io.github.jakex7.peek.core.PeekModifier
import io.github.jakex7.peek.core.PeekRoot
import io.github.jakex7.peek.core.PendingIntentPeekAction
import io.github.jakex7.peek.core.SelectableGroupModifier
import io.github.jakex7.peek.core.SendBroadcastPeekAction
import io.github.jakex7.peek.core.StartActivityPeekAction
import io.github.jakex7.peek.core.TextAlign
import io.github.jakex7.peek.core.TextDecoration
import io.github.jakex7.peek.core.Visibility
import io.github.jakex7.peek.core.VisibilityModifier
import io.github.jakex7.peek.core.WidthModifier
import io.github.jakex7.peek.core.controls.EmittableCheckBox
import io.github.jakex7.peek.core.controls.EmittableCompoundButton
import io.github.jakex7.peek.core.controls.EmittableRadioButton
import io.github.jakex7.peek.core.controls.EmittableSwitch
import io.github.jakex7.peek.core.find
import io.github.jakex7.peek.core.image.EmittableImage
import io.github.jakex7.peek.core.layout.EmittableBox
import io.github.jakex7.peek.core.layout.EmittableColumn
import io.github.jakex7.peek.core.layout.EmittableDivider
import io.github.jakex7.peek.core.layout.EmittableRow
import io.github.jakex7.peek.core.layout.EmittableSpacer
import io.github.jakex7.peek.core.progress.EmittableCircularProgressIndicator
import io.github.jakex7.peek.core.progress.EmittableLinearProgressIndicator
import io.github.jakex7.peek.core.remoteviews.EmittableAndroidRemoteViews
import io.github.jakex7.peek.core.text.EmittableButton
import io.github.jakex7.peek.core.text.EmittableText
import kotlin.math.round
import android.graphics.drawable.Icon as AndroidIcon

internal class RemoteViewsTranslator(
  private val context: Context,
  private val surfaceDescription: String,
  @param:LayoutRes
  private val rootLayoutId: Int = R.layout.peek_rv_root,
) {
  private var nextGeneratedViewId = FIRST_GENERATED_VIEW_ID

  fun translate(root: PeekRoot): RemoteViews =
    rootRemoteViews(rootLayoutId).apply {
      removeAllViews(R.id.peek_children)
      root.children.forEachIndexed { index, child ->
        addTranslatedChild(R.id.peek_children, translateNode(child), index)
      }
    }

  private fun translateNode(node: Emittable): TranslatedRemoteViews {
    val translated = when (node) {
      is EmittableBox -> translateBox(node)
      is EmittableRow -> translateRow(node)
      is EmittableColumn -> translateColumn(node)
      is EmittableSpacer -> remoteViews(R.layout.peek_rv_spacer).apply {
        remoteViews.applyModifiers(node.modifier, mainViewId)
      }

      is EmittableDivider -> translateDivider(node)
      is EmittableText -> translateText(node)
      is EmittableButton -> translateButton(node)
      is EmittableImage -> translateImage(node)
      is EmittableLinearProgressIndicator -> translateLinearProgress(node)
      is EmittableCircularProgressIndicator -> translateCircularProgress(node)
      is EmittableCheckBox -> translateCheckBox(node)
      is EmittableSwitch -> translateSwitch(node)
      is EmittableRadioButton -> translateRadioButton(node)
      is EmittableAndroidRemoteViews -> translateAndroidRemoteViews(node)
      else -> throw IllegalArgumentException(
        "Unsupported Peek node for $surfaceDescription: ${node::class.qualifiedName}",
      )
    }
    // Leaves that fill an axis through a dedicated match-parent layout below API 31 must not be
    // re-wrapped by the fixed-size wrapper, which would otherwise force the fill axis back to
    // wrap-content (see translateDivider / textLayoutId).
    val sized = if (node.usesFillLayoutPre31()) {
      translated
    } else {
      translated.wrapFixedSizeIfNeeded(node.modifier)
    }
    return sized.wrapMinimumSizeIfNeeded(node.modifier)
  }

  private fun translateBox(node: EmittableBox): TranslatedRemoteViews =
    if (node.contentAlignment != Alignment.TopStart && node.children.size == 1) {
      translateSingleChildAlignedBox(node)
    } else {
      translateMultiChildBox(node)
    }

  private fun translateSingleChildAlignedBox(node: EmittableBox): TranslatedRemoteViews =
    remoteViews(AlignedBoxLayouts.layoutFor(node.modifier)).apply {
      remoteViews.removeAllViews(R.id.peek_children)
      remoteViews.applyModifiers(node.modifier, mainViewId)
      node.contentAlignment.toGravityOrNull()?.let { gravity ->
        remoteViews.setInt(mainViewId, "setGravity", gravity)
      }
      remoteViews.addTranslatedChild(R.id.peek_children, translateNode(node.children.single()), 0)
    }

  private fun translateMultiChildBox(node: EmittableBox): TranslatedRemoteViews =
    remoteViews(BoxLayouts.layoutFor(node.modifier)).apply {
      remoteViews.removeAllViews(mainViewId)
      remoteViews.applyModifiers(node.modifier, mainViewId)
      node.children.forEachIndexed { index, child ->
        remoteViews.addTranslatedChild(
          mainViewId,
          translateAlignedBoxChild(node.contentAlignment, child),
          index,
        )
      }
    }

  private fun translateAlignedBoxChild(
    alignment: Alignment,
    child: Emittable,
  ): TranslatedRemoteViews =
    remoteViews(R.layout.peek_rv_aligned_child).apply {
      remoteViews.removeAllViews(R.id.peek_children)
      alignment.toGravityOrNull()?.let { gravity ->
        remoteViews.setInt(mainViewId, "setGravity", gravity)
      }
      remoteViews.addTranslatedChild(R.id.peek_children, translateNode(child), 0)
    }

  private fun translateRow(node: EmittableRow): TranslatedRemoteViews {
    val layouts = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && node.modifier.isSelectableGroup()) {
      RadioRowLayouts
    } else {
      RowLayouts
    }
    return translateContainer(layouts.layoutFor(node.modifier), node).apply {
      toGravityOrNull(node.horizontalAlignment, node.verticalAlignment)?.let { gravity ->
        remoteViews.setInt(mainViewId, "setGravity", gravity)
      }
      checkSelectableGroupChildren(node)
    }
  }

  private fun translateColumn(node: EmittableColumn): TranslatedRemoteViews {
    val layouts = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && node.modifier.isSelectableGroup()) {
      RadioColumnLayouts
    } else {
      ColumnLayouts
    }
    return translateContainer(layouts.layoutFor(node.modifier), node).apply {
      toGravityOrNull(node.horizontalAlignment, node.verticalAlignment)?.let { gravity ->
        remoteViews.setInt(mainViewId, "setGravity", gravity)
      }
      checkSelectableGroupChildren(node)
    }
  }

  private fun translateContainer(
    layoutId: Int,
    node: EmittableWithChildren,
  ): TranslatedRemoteViews =
    remoteViews(layoutId).apply {
      remoteViews.removeAllViews(mainViewId)
      remoteViews.applyModifiers(node.modifier, mainViewId)
      node.children.forEachIndexed { index, child ->
        remoteViews.addTranslatedChild(mainViewId, translateNode(child), index)
      }
    }

  private fun translateText(node: EmittableText): TranslatedRemoteViews =
    remoteViews(textLayoutId(node.modifier)).apply {
      remoteViews.setTextViewText(mainViewId, node.styledText())
      remoteViews.setTextColor(mainViewId, node.color.getColor(context).toArgb())
      if (node.fontSize.isSpecified) {
        remoteViews.setTextViewTextSize(
          mainViewId,
          TypedValue.COMPLEX_UNIT_SP,
          node.fontSize.value,
        )
      }
      if (node.maxLines != Int.MAX_VALUE) {
        remoteViews.setInt(mainViewId, "setMaxLines", node.maxLines)
      }
      // TextView.setGravity is only a remotable method on API 31+. Below 31 it throws when the
      // RemoteViews is applied, so text alignment is left to the default (start); use container
      // alignment (Row/Column/Box) to position text on older platforms.
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        node.textAlign?.let {
          remoteViews.setInt(mainViewId, "setGravity", it.toGravity())
        }
      }
      remoteViews.applyModifiers(node.modifier, mainViewId)
      remoteViews.applyTextViewDimensionFallback(node.modifier, mainViewId)
      remoteViews.applyTextViewMinimumSizeModifiers(node.modifier, mainViewId)
    }

  private fun translateDivider(node: EmittableDivider): TranslatedRemoteViews {
    // API 31+ resolves fill/fixed dimensions through setViewLayout*, so the plain FrameLayout
    // template works for every orientation. Below 31 there is no runtime layout-size setter, so a
    // filling divider needs a template whose fill axis is match-parent in XML, with the thickness
    // applied through TextView.setWidth/setHeight (a remotable method available on all API levels).
    if (!node.usesFillLayoutPre31()) {
      return remoteViews(R.layout.peek_rv_divider).apply {
        node.applyDividerColor(this)
        remoteViews.applyModifiers(node.modifier, mainViewId)
      }
    }

    val horizontal = node.orientation == DividerOrientation.Horizontal
    val layoutId = if (horizontal) {
      R.layout.peek_rv_divider_horizontal
    } else {
      R.layout.peek_rv_divider_vertical
    }

    return remoteViews(layoutId).apply {
      node.applyDividerColor(this)
      val thicknessPx = node.thicknessPx()
      if (horizontal) {
        remoteViews.setInt(mainViewId, "setHeight", thicknessPx)
      } else {
        remoteViews.setInt(mainViewId, "setWidth", thicknessPx)
      }
      remoteViews.applyModifiers(node.modifier, mainViewId)
    }
  }

  private fun EmittableDivider.applyDividerColor(translated: TranslatedRemoteViews) {
    val resolvedColor = color.getColor(context)
    if (resolvedColor != Color.Unspecified) {
      translated.remoteViews.setInt(
        translated.mainViewId,
        "setBackgroundColor",
        resolvedColor.toArgb()
      )
    }
  }

  // True when a leaf renders through a dedicated match-parent ("fill") layout below API 31. Such
  // leaves own their sizing and must skip the generic fixed-size wrapper.
  private fun Emittable.usesFillLayoutPre31(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      return false
    }

    return when (this) {
      is EmittableDivider -> fillsMainAxis()
      is EmittableText -> fillsEitherAxis()
      else -> false
    }
  }

  private fun EmittableDivider.fillsMainAxis(): Boolean {
    val fillAxis = when (orientation) {
      DividerOrientation.Horizontal -> modifier.find<WidthModifier>()?.dimension
      DividerOrientation.Vertical -> modifier.find<HeightModifier>()?.dimension
    }
    return fillAxis == Dimension.Fill
  }

  private fun Emittable.fillsEitherAxis(): Boolean =
    modifier.find<WidthModifier>()?.dimension == Dimension.Fill ||
      modifier.find<HeightModifier>()?.dimension == Dimension.Fill

  // Below API 31 there is no runtime layout-size setter, so a filling TextView needs a template
  // whose fill axis is match-parent in XML. API 31+ uses the base template and setViewLayout*.
  private fun textLayoutId(modifier: PeekModifier): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      return R.layout.peek_rv_text
    }

    val fillWidth = modifier.find<WidthModifier>()?.dimension == Dimension.Fill
    val fillHeight = modifier.find<HeightModifier>()?.dimension == Dimension.Fill

    return when {
      fillWidth && fillHeight -> R.layout.peek_rv_text_fill_size
      fillWidth -> R.layout.peek_rv_text_fill_width
      fillHeight -> R.layout.peek_rv_text_fill_height
      else -> R.layout.peek_rv_text
    }
  }

  private fun EmittableDivider.thicknessPx(): Int {
    val thickness = when (orientation) {
      DividerOrientation.Horizontal -> modifier.find<HeightModifier>()?.dimension
      DividerOrientation.Vertical -> modifier.find<WidthModifier>()?.dimension
    } as? Dimension.Fixed

    return (thickness?.value ?: 1.dp).toPx()
  }

  private fun translateButton(node: EmittableButton): TranslatedRemoteViews =
    remoteViews(R.layout.peek_rv_button).apply {
      remoteViews.setTextViewText(mainViewId, node.text)
      remoteViews.setBoolean(mainViewId, "setEnabled", node.enabled)
      val resolvedColor = node.color.getColor(context)
      if (resolvedColor != Color.Unspecified) {
        remoteViews.setTextColor(mainViewId, resolvedColor.toArgb())
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        remoteViews.setBackgroundTint(mainViewId, node.containerColor)
      }
      remoteViews.applyModifiers(node.modifier, mainViewId)
      remoteViews.applyTextViewDimensionFallback(node.modifier, mainViewId)
      remoteViews.applyTextViewMinimumSizeModifiers(node.modifier, mainViewId)
      if (node.enabled) {
        node.onClick?.let {
          remoteViews.setOnClickPendingIntent(mainViewId, it.toPendingIntent())
        }
      }
    }

  private fun translateImage(node: EmittableImage): TranslatedRemoteViews =
    remoteViews(node.contentScale.toImageLayoutId(isDecorative = node.isDecorative())).apply {
      when (val provider = node.provider) {
        is ImageProvider.Resource -> remoteViews.setImageViewResource(mainViewId, provider.resId)
        is ImageProvider.BitmapImage -> remoteViews.setImageViewBitmap(mainViewId, provider.bitmap)
        is ImageProvider.UriImage -> remoteViews.setImageViewUri(mainViewId, provider.uri)
        is ImageProvider.IconImage -> remoteViews.setImageViewIcon(mainViewId, provider.icon)
        null -> throw IllegalArgumentException("Peek Image requires an ImageProvider.")
      }
      if (!node.isDecorative()) {
        remoteViews.setContentDescription(mainViewId, node.contentDescription)
      }
      node.colorFilter?.tint?.let { tint ->
        val resolvedTint = tint.getColor(context)
        if (resolvedTint != Color.Unspecified) {
          remoteViews.setInt(mainViewId, "setColorFilter", resolvedTint.toArgb())
        }
      }
      node.alpha?.let { alpha ->
        val convertedAlpha = round(alpha.coerceIn(0f, 1f) * 255).toInt().coerceIn(0, 255)
        remoteViews.setInt(mainViewId, "setImageAlpha", convertedAlpha)
      }
      remoteViews.applyModifiers(node.modifier, mainViewId)
      remoteViews.setBoolean(mainViewId, "setAdjustViewBounds", node.shouldAdjustViewBounds())
    }

  private fun translateLinearProgress(
    node: EmittableLinearProgressIndicator,
  ): TranslatedRemoteViews =
    remoteViews(R.layout.peek_rv_linear_progress).apply {
      remoteViews.setProgressBar(
        mainViewId,
        PROGRESS_MAX,
        node.progress?.toProgressInt() ?: 0,
        node.progress == null,
      )
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        remoteViews.setProgressTint(mainViewId, node.color)
        remoteViews.setProgressBackgroundTint(mainViewId, node.trackColor)
      }
      remoteViews.applyModifiers(node.modifier, mainViewId)
    }

  private fun translateCircularProgress(
    node: EmittableCircularProgressIndicator,
  ): TranslatedRemoteViews =
    if (node.progress == null) {
      translateIndeterminateCircularProgress(node)
    } else {
      translateDeterminateCircularProgress(node)
    }

  private fun translateIndeterminateCircularProgress(
    node: EmittableCircularProgressIndicator,
  ): TranslatedRemoteViews =
    remoteViews(R.layout.peek_rv_circular_progress).apply {
      remoteViews.setProgressBar(
        mainViewId,
        PROGRESS_MAX,
        0,
        true,
      )
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        remoteViews.setIndeterminateTint(mainViewId, node.color)
      }
      remoteViews.applyModifiers(node.modifier, mainViewId)
    }

  private fun translateDeterminateCircularProgress(
    node: EmittableCircularProgressIndicator,
  ): TranslatedRemoteViews =
    remoteViews(R.layout.peek_rv_circular_progress_determinate).apply {
      remoteViews.setProgressBar(
        mainViewId,
        PROGRESS_MAX,
        requireNotNull(node.progress).toProgressInt(),
        false,
      )
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        remoteViews.setProgressTint(mainViewId, node.color)
        remoteViews.setProgressBackgroundTint(mainViewId, node.trackColor)
      }
      remoteViews.applyModifiers(node.modifier, mainViewId)
    }

  private fun translateAndroidRemoteViews(
    node: EmittableAndroidRemoteViews,
  ): TranslatedRemoteViews {
    val embedded =
      if (node.children.isEmpty()) {
        node.remoteViews.copyCompat()
      } else {
        require(node.containerViewId != View.NO_ID) {
          "To add children to AndroidRemoteViews, containerViewId must be set."
        }
        node.remoteViews.copyCompat().apply {
          removeAllViews(node.containerViewId)
          node.children.forEachIndexed { index, child ->
            addTranslatedChild(node.containerViewId, translateNode(child), index)
          }
        }
      }
    return remoteViews(BoxLayouts.layoutFor(node.modifier)).apply {
      remoteViews.removeAllViews(mainViewId)
      remoteViews.applyModifiers(node.modifier, mainViewId)
      remoteViews.addTranslatedChild(
        mainViewId,
        TranslatedRemoteViews(remoteViews = embedded, mainViewId = node.containerViewId),
        0,
      )
    }
  }

  private fun translateCheckBox(node: EmittableCheckBox): TranslatedRemoteViews =
    translateCompoundButton(
      node = node,
      nativeLayoutId = R.layout.peek_rv_check_box,
      checkedIcon = "[x]",
      uncheckedIcon = "[ ]",
    )

  private fun translateSwitch(node: EmittableSwitch): TranslatedRemoteViews =
    translateCompoundButton(
      node = node,
      nativeLayoutId = R.layout.peek_rv_switch,
      checkedIcon = "ON",
      uncheckedIcon = "OFF",
    )

  private fun translateRadioButton(node: EmittableRadioButton): TranslatedRemoteViews =
    translateCompoundButton(
      node = node,
      nativeLayoutId = R.layout.peek_rv_radio_button,
      checkedIcon = "(*)",
      uncheckedIcon = "( )",
    )

  private fun translateCompoundButton(
    node: EmittableCompoundButton,
    nativeLayoutId: Int,
    checkedIcon: String,
    uncheckedIcon: String,
  ): TranslatedRemoteViews {
    val layoutId =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        nativeLayoutId
      } else {
        R.layout.peek_rv_compound_button_backport
      }
    return remoteViews(layoutId).apply {
      val resolvedColor = node.color.getColor(context)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        remoteViews.setTextViewText(mainViewId, node.text)
        if (resolvedColor != Color.Unspecified) {
          remoteViews.setTextColor(mainViewId, resolvedColor.toArgb())
        }
        RemoteViewsApi31Impl.setCompoundButtonChecked(
          remoteViews,
          mainViewId,
          node.checked,
        )
      } else {
        val icon = if (node.checked) checkedIcon else uncheckedIcon
        remoteViews.setTextViewText(R.id.peek_icon, icon)
        remoteViews.setTextViewText(R.id.peek_label, node.text)
        if (resolvedColor != Color.Unspecified) {
          remoteViews.setTextColor(R.id.peek_icon, resolvedColor.toArgb())
          remoteViews.setTextColor(R.id.peek_label, resolvedColor.toArgb())
        }
      }
      remoteViews.setBoolean(mainViewId, "setEnabled", node.enabled)
      remoteViews.applyModifiers(node.modifier, mainViewId)
      if (node.enabled) {
        node.onClick?.let {
          remoteViews.setOnClickPendingIntent(mainViewId, it.toPendingIntent())
        }
      }
    }
  }

  private fun RemoteViews.applyModifiers(
    modifier: PeekModifier,
    viewId: Int,
  ): RemoteViews =
    apply {
      modifier.find<PaddingModifier>()?.let {
        setViewPadding(
          viewId,
          it.start.toPx(),
          it.top.toPx(),
          it.end.toPx(),
          it.bottom.toPx(),
        )
      }
      modifier.find<BackgroundModifier>()?.let {
        val resolvedColor = it.color.getColor(context)
        if (resolvedColor != Color.Unspecified) {
          setInt(viewId, "setBackgroundColor", resolvedColor.toArgb())
        }
      }
      modifier.find<VisibilityModifier>()?.let {
        setViewVisibility(viewId, it.visibility.toViewVisibility())
      }
      modifier.findClickAction()?.let {
        setOnClickPendingIntent(viewId, it.toPendingIntent())
      }
      applyDimensionModifiers(modifier, viewId)
    }

  private fun RemoteViews.applyDimensionModifiers(
    modifier: PeekModifier,
    viewId: Int,
  ) {
    val width = modifier.find<WidthModifier>()?.dimension
    val height = modifier.find<HeightModifier>()?.dimension
    if (Build.VERSION.SDK_INT >= 31) {
      width?.let {
        setViewLayoutWidth(
          viewId,
          it.toLayoutSize(),
          TypedValue.COMPLEX_UNIT_PX,
        )
      }
      height?.let {
        setViewLayoutHeight(
          viewId,
          it.toLayoutSize(),
          TypedValue.COMPLEX_UNIT_PX,
        )
      }
    }
  }

  private fun RemoteViews.applyTextViewDimensionFallback(
    modifier: PeekModifier,
    viewId: Int,
  ) {
    if (Build.VERSION.SDK_INT >= 31) {
      return
    }

    val width = modifier.find<WidthModifier>()?.dimension
    val height = modifier.find<HeightModifier>()?.dimension
    if (width is Dimension.Fixed) {
      setInt(viewId, "setWidth", width.value.toPx())
    }
    if (height is Dimension.Fixed) {
      setInt(viewId, "setHeight", height.value.toPx())
    }
  }

  private fun RemoteViews.applyTextViewMinimumSizeModifiers(
    modifier: PeekModifier,
    viewId: Int,
  ) {
    modifier.find<MinWidthModifier>()?.let {
      setInt(viewId, "setMinWidth", it.width.toPx())
    }
    modifier.find<MinHeightModifier>()?.let {
      setInt(viewId, "setMinHeight", it.height.toPx())
    }
  }

  private fun rootRemoteViews(layoutId: Int): RemoteViews =
    RemoteViews(context.packageName, layoutId)

  private fun remoteViews(layoutId: Int): TranslatedRemoteViews {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val viewId = nextGeneratedViewId()
      return TranslatedRemoteViews(
        remoteViews = RemoteViewsApi31Impl.remoteViews(context.packageName, layoutId, viewId),
        mainViewId = viewId,
      )
    }
    return TranslatedRemoteViews(
      remoteViews = RemoteViews(context.packageName, layoutId),
      mainViewId = R.id.peek_main,
    )
  }

  private fun RemoteViews.addTranslatedChild(
    parentViewId: Int,
    child: TranslatedRemoteViews,
    stableId: Int,
  ) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      RemoteViewsApi31Impl.addStableView(this, parentViewId, child.remoteViews, stableId)
      return
    }
    addView(parentViewId, child.remoteViews)
  }

  private fun nextGeneratedViewId(): Int =
    nextGeneratedViewId++

  private fun ContainerLayouts.layoutFor(modifier: PeekModifier): Int {
    val width = modifier.find<WidthModifier>()?.dimension
    val height = modifier.find<HeightModifier>()?.dimension
    val shouldFillWidth =
      width == Dimension.Fill || (Build.VERSION.SDK_INT < 31 && width is Dimension.Fixed)
    val shouldFillHeight =
      height == Dimension.Fill || (Build.VERSION.SDK_INT < 31 && height is Dimension.Fixed)
    return when {
      shouldFillWidth && shouldFillHeight -> fillSize
      shouldFillWidth -> fillWidth
      shouldFillHeight -> fillHeight
      else -> wrap
    }
  }

  private fun TranslatedRemoteViews.wrapFixedSizeIfNeeded(
    modifier: PeekModifier,
  ): TranslatedRemoteViews {
    if (Build.VERSION.SDK_INT >= 31) {
      return this
    }

    val width = modifier.find<WidthModifier>()?.dimension as? Dimension.Fixed
    val height = modifier.find<HeightModifier>()?.dimension as? Dimension.Fixed
    if (width == null && height == null) {
      return this
    }

    val wrapperLayout = when {
      width != null && height != null -> R.layout.peek_rv_fixed_size_wrapper
      width != null -> R.layout.peek_rv_fixed_width_wrapper
      else -> R.layout.peek_rv_fixed_height_wrapper
    }

    val wrapped = this
    return TranslatedRemoteViews(
      remoteViews =
        RemoteViews(context.packageName, wrapperLayout).apply {
          removeAllViews(R.id.peek_children)
          width?.let { setInt(R.id.peek_size, "setWidth", it.value.toPx()) }
          height?.let { setInt(R.id.peek_size, "setHeight", it.value.toPx()) }
          addTranslatedChild(R.id.peek_children, wrapped, 0)
        },
      mainViewId = R.id.peek_main,
    )
  }

  private fun TranslatedRemoteViews.wrapMinimumSizeIfNeeded(
    modifier: PeekModifier,
  ): TranslatedRemoteViews {
    val minWidth = modifier.find<MinWidthModifier>()
    val minHeight = modifier.find<MinHeightModifier>()
    if (minWidth == null && minHeight == null) {
      return this
    }

    val wrapped = this
    return TranslatedRemoteViews(
      remoteViews =
        RemoteViews(context.packageName, R.layout.peek_rv_min_size_wrapper).apply {
          removeAllViews(R.id.peek_children)
          minWidth?.let { setInt(R.id.peek_size, "setMinWidth", it.width.toPx()) }
          minHeight?.let { setInt(R.id.peek_size, "setMinHeight", it.height.toPx()) }
          addTranslatedChild(R.id.peek_children, wrapped, 0)
        },
      mainViewId = R.id.peek_main,
    )
  }

  private fun PeekModifier.findClickAction(): PeekAction? =
    elements.asReversed().firstNotNullOfOrNull { element ->
      when (element) {
        is ClickActionModifier -> PendingIntentPeekAction(element.pendingIntent)
        is PeekActionModifier -> element.action
        else -> null
      }
    }

  private fun PeekModifier.isSelectableGroup(): Boolean =
    find<SelectableGroupModifier>() != null

  private fun checkSelectableGroupChildren(node: EmittableWithChildren) {
    if (!node.modifier.isSelectableGroup()) {
      return
    }

    check(node.children.count { it is EmittableRadioButton && it.checked } <= 1) {
      "When using PeekModifier.selectableGroup(), no more than one RadioButton may be checked at a time."
    }
  }

  @androidx.annotation.RequiresApi(Build.VERSION_CODES.S)
  private fun RemoteViews.setBackgroundTint(
    viewId: Int,
    color: ColorProvider,
  ) {
    val resolvedColor = color.getColor(context)
    if (resolvedColor != Color.Unspecified) {
      RemoteViewsApi31Impl.setColorStateList(
        this,
        viewId,
        "setBackgroundTintList",
        ColorStateList.valueOf(resolvedColor.toArgb()),
      )
    }
  }

  @androidx.annotation.RequiresApi(Build.VERSION_CODES.S)
  private fun RemoteViews.setProgressTint(
    viewId: Int,
    color: ColorProvider,
  ) {
    val resolvedColor = color.getColor(context)
    if (resolvedColor != Color.Unspecified) {
      RemoteViewsApi31Impl.setColorStateList(
        this,
        viewId,
        "setProgressTintList",
        ColorStateList.valueOf(resolvedColor.toArgb()),
      )
    }
  }

  @androidx.annotation.RequiresApi(Build.VERSION_CODES.S)
  private fun RemoteViews.setProgressBackgroundTint(
    viewId: Int,
    color: ColorProvider,
  ) {
    val resolvedColor = color.getColor(context)
    if (resolvedColor != Color.Unspecified) {
      RemoteViewsApi31Impl.setColorStateList(
        this,
        viewId,
        "setProgressBackgroundTintList",
        ColorStateList.valueOf(resolvedColor.toArgb()),
      )
    }
  }

  @androidx.annotation.RequiresApi(Build.VERSION_CODES.S)
  private fun RemoteViews.setIndeterminateTint(
    viewId: Int,
    color: ColorProvider,
  ) {
    val resolvedColor = color.getColor(context)
    if (resolvedColor != Color.Unspecified) {
      RemoteViewsApi31Impl.setColorStateList(
        this,
        viewId,
        "setIndeterminateTintList",
        ColorStateList.valueOf(resolvedColor.toArgb()),
      )
    }
  }

  private fun PeekAction.toPendingIntent(): PendingIntent =
    when (this) {
      is PendingIntentPeekAction -> pendingIntent
      is StartActivityPeekAction ->
        PendingIntent.getActivity(
          context,
          requestCode,
          intent,
          flags,
        )

      is SendBroadcastPeekAction ->
        PendingIntent.getBroadcast(
          context,
          requestCode,
          intent,
          flags,
        )
    }

  private fun Dp.toPx(): Int =
    TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      value,
      context.resources.displayMetrics,
    ).toInt()

  private fun Float.toProgressInt(): Int =
    (coerceIn(0f, 1f) * PROGRESS_MAX).toInt()

  private fun Dimension.toLayoutSize(): Float =
    when (this) {
      Dimension.Fill -> ViewGroup.LayoutParams.MATCH_PARENT.toFloat()
      Dimension.Wrap -> ViewGroup.LayoutParams.WRAP_CONTENT.toFloat()
      is Dimension.Fixed -> value.toPx().toFloat()
    }

  private fun Visibility.toViewVisibility(): Int =
    when (this) {
      Visibility.Visible -> View.VISIBLE
      Visibility.Invisible -> View.INVISIBLE
      Visibility.Gone -> View.GONE
    }

  private fun Alignment.toGravityOrNull(): Int? =
    toGravityOrNull(horizontal, vertical)

  private fun toGravityOrNull(
    horizontal: Alignment.Horizontal,
    vertical: Alignment.Vertical,
  ): Int? {
    if (horizontal == Alignment.Start && vertical == Alignment.Top) return null
    return horizontal.toGravity() or vertical.toGravity()
  }

  private fun Alignment.Horizontal.toGravity(): Int =
    when (this) {
      Alignment.Horizontal.Start -> Gravity.START
      Alignment.Horizontal.CenterHorizontally -> Gravity.CENTER_HORIZONTAL
      Alignment.Horizontal.End -> Gravity.END
    }

  private fun Alignment.Vertical.toGravity(): Int =
    when (this) {
      Alignment.Vertical.Top -> Gravity.TOP
      Alignment.Vertical.CenterVertically -> Gravity.CENTER_VERTICAL
      Alignment.Vertical.Bottom -> Gravity.BOTTOM
    }

  private fun TextAlign.toGravity(): Int =
    when (this) {
      TextAlign.Left -> Gravity.LEFT
      TextAlign.Right -> Gravity.RIGHT
      TextAlign.Center -> Gravity.CENTER_HORIZONTAL
      TextAlign.Start -> Gravity.START
      TextAlign.End -> Gravity.END
    }

  private fun EmittableImage.isDecorative(): Boolean =
    contentDescription.isNullOrEmpty()

  private fun EmittableImage.shouldAdjustViewBounds(): Boolean =
    contentScale == ContentScale.Fit &&
      (
        modifier.find<WidthModifier>()?.dimension == Dimension.Wrap ||
          modifier.find<HeightModifier>()?.dimension == Dimension.Wrap
        )

  private fun ContentScale.toImageLayoutId(isDecorative: Boolean): Int =
    when (this) {
      ContentScale.Crop ->
        if (isDecorative) {
          R.layout.peek_rv_image_crop_decorative
        } else {
          R.layout.peek_rv_image_crop
        }

      ContentScale.Fit ->
        if (isDecorative) {
          R.layout.peek_rv_image_fit_decorative
        } else R.layout.peek_rv_image_fit

      ContentScale.FillBounds ->
        if (isDecorative) {
          R.layout.peek_rv_image_fill_bounds_decorative
        } else {
          R.layout.peek_rv_image_fill_bounds
        }
    }

  private fun EmittableText.styledText(): CharSequence {
    if (text.isEmpty()) {
      return text
    }

    val style = toTypefaceStyle(fontWeight, fontStyle)
    val decoration = textDecoration
    if (style == Typeface.NORMAL && decoration == TextDecoration.None) {
      return text
    }

    return SpannableString(text).apply {
      val setDefaultSpan: (Any) -> Unit = { what ->
        setSpan(
          what,
          0,
          length,
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
      }

      if (style != Typeface.NORMAL) {
        setDefaultSpan(StyleSpan(style))
      }
      if (decoration.underline) {
        setDefaultSpan(UnderlineSpan())
      }
      if (decoration.lineThrough) {
        setDefaultSpan(StrikethroughSpan())
      }
    }
  }

  private fun toTypefaceStyle(
    fontWeight: FontWeight,
    fontStyle: FontStyle,
  ): Int =
    when {
      fontWeight == FontWeight.Bold && fontStyle == FontStyle.Italic -> Typeface.BOLD_ITALIC
      fontWeight == FontWeight.Bold -> Typeface.BOLD
      fontStyle == FontStyle.Italic -> Typeface.ITALIC
      else -> Typeface.NORMAL
    }

  private companion object {
    const val PROGRESS_MAX = 100
    const val FIRST_GENERATED_VIEW_ID = 1

    val RowLayouts = ContainerLayouts(
      wrap = R.layout.peek_rv_row,
      fillWidth = R.layout.peek_rv_row_fill_width,
      fillHeight = R.layout.peek_rv_row_fill_height,
      fillSize = R.layout.peek_rv_row_fill_size,
    )
    val ColumnLayouts = ContainerLayouts(
      wrap = R.layout.peek_rv_column,
      fillWidth = R.layout.peek_rv_column_fill_width,
      fillHeight = R.layout.peek_rv_column_fill_height,
      fillSize = R.layout.peek_rv_column_fill_size,
    )
    val RadioRowLayouts = ContainerLayouts(
      wrap = R.layout.peek_rv_radio_row,
      fillWidth = R.layout.peek_rv_radio_row_fill_width,
      fillHeight = R.layout.peek_rv_radio_row_fill_height,
      fillSize = R.layout.peek_rv_radio_row_fill_size,
    )
    val RadioColumnLayouts = ContainerLayouts(
      wrap = R.layout.peek_rv_radio_column,
      fillWidth = R.layout.peek_rv_radio_column_fill_width,
      fillHeight = R.layout.peek_rv_radio_column_fill_height,
      fillSize = R.layout.peek_rv_radio_column_fill_size,
    )
    val BoxLayouts = ContainerLayouts(
      wrap = R.layout.peek_rv_box,
      fillWidth = R.layout.peek_rv_box_fill_width,
      fillHeight = R.layout.peek_rv_box_fill_height,
      fillSize = R.layout.peek_rv_box_fill_size,
    )
    val AlignedBoxLayouts = ContainerLayouts(
      wrap = R.layout.peek_rv_box_aligned_child,
      fillWidth = R.layout.peek_rv_box_aligned_child_fill_width,
      fillHeight = R.layout.peek_rv_box_aligned_child_fill_height,
      fillSize = R.layout.peek_rv_box_aligned_child_fill_size,
    )
  }
}

private data class ContainerLayouts(
  val wrap: Int,
  val fillWidth: Int,
  val fillHeight: Int,
  val fillSize: Int,
)

private data class TranslatedRemoteViews(
  val remoteViews: RemoteViews,
  val mainViewId: Int,
)

private fun RemoteViews.copyCompat(): RemoteViews =
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    RemoteViewsApi28Impl.copy(this)
  } else {
    @Suppress("DEPRECATION")
    clone()
  }

@androidx.annotation.RequiresApi(Build.VERSION_CODES.P)
private object RemoteViewsApi28Impl {
  fun copy(remoteViews: RemoteViews): RemoteViews = RemoteViews(remoteViews)
}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.S)
private object RemoteViewsApi31Impl {
  fun remoteViews(
    packageName: String,
    layoutId: Int,
    viewId: Int,
  ): RemoteViews =
    RemoteViews(packageName, layoutId, viewId)

  fun addStableView(
    parent: RemoteViews,
    parentViewId: Int,
    child: RemoteViews,
    stableId: Int,
  ) {
    parent.addStableView(parentViewId, child, stableId)
  }

  fun setCompoundButtonChecked(
    remoteViews: RemoteViews,
    viewId: Int,
    checked: Boolean,
  ) {
    remoteViews.setCompoundButtonChecked(viewId, checked)
  }

  fun setColorStateList(
    remoteViews: RemoteViews,
    viewId: Int,
    methodName: String,
    value: ColorStateList,
  ) {
    remoteViews.setColorStateList(viewId, methodName, value)
  }
}

private object RemoteViewsApi23Impl {
  fun setImageViewIcon(
    remoteViews: RemoteViews,
    viewId: Int,
    icon: AndroidIcon,
  ) {
    remoteViews.setImageViewIcon(viewId, icon)
  }
}
