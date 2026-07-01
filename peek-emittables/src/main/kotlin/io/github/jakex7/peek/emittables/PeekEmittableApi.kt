package io.github.jakex7.peek.emittables

import android.app.PendingIntent
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.jakex7.peek.core.actionPendingIntent as coreActionPendingIntent
import io.github.jakex7.peek.core.actionSendBroadcast as coreActionSendBroadcast
import io.github.jakex7.peek.core.actionStartActivity as coreActionStartActivity
import io.github.jakex7.peek.core.background as coreBackground
import io.github.jakex7.peek.core.clickable as coreClickable
import io.github.jakex7.peek.core.defaultMinSize as coreDefaultMinSize
import io.github.jakex7.peek.core.fillMaxHeight as coreFillMaxHeight
import io.github.jakex7.peek.core.fillMaxSize as coreFillMaxSize
import io.github.jakex7.peek.core.fillMaxWidth as coreFillMaxWidth
import io.github.jakex7.peek.core.height as coreHeight
import io.github.jakex7.peek.core.padding as corePadding
import io.github.jakex7.peek.core.size as coreSize
import io.github.jakex7.peek.core.then as coreThen
import io.github.jakex7.peek.core.visibility as coreVisibility
import io.github.jakex7.peek.core.width as coreWidth
import io.github.jakex7.peek.core.wrapContentHeight as coreWrapContentHeight
import io.github.jakex7.peek.core.wrapContentSize as coreWrapContentSize
import io.github.jakex7.peek.core.wrapContentWidth as coreWrapContentWidth

fun ColorProvider(color: Color): ColorProvider =
  io.github.jakex7.peek.core.ColorProvider(color)

fun ColorProvider(day: Color, night: Color): ColorProvider =
  io.github.jakex7.peek.core.ColorProvider(day = day, night = night)

fun actionPendingIntent(pendingIntent: PendingIntent): PeekAction =
  coreActionPendingIntent(pendingIntent)

fun actionStartActivity(
  intent: Intent,
  requestCode: Int = 0,
  flags: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
): PeekAction =
  coreActionStartActivity(intent, requestCode, flags)

fun actionSendBroadcast(
  intent: Intent,
  requestCode: Int = 0,
  flags: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
): PeekAction =
  coreActionSendBroadcast(intent, requestCode, flags)

fun PeekModifier.then(other: PeekModifier): PeekModifier =
  coreThen(other)

fun PeekModifier.padding(all: Dp): PeekModifier =
  corePadding(all)

fun PeekModifier.padding(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): PeekModifier =
  corePadding(horizontal = horizontal, vertical = vertical)

fun PeekModifier.padding(start: Dp, top: Dp, end: Dp, bottom: Dp): PeekModifier =
  corePadding(start = start, top = top, end = end, bottom = bottom)

fun PeekModifier.background(color: ColorProvider): PeekModifier =
  coreBackground(color)

fun PeekModifier.width(width: Dp): PeekModifier =
  coreWidth(width)

fun PeekModifier.height(height: Dp): PeekModifier =
  coreHeight(height)

fun PeekModifier.defaultMinSize(
  minWidth: Dp = Dp.Unspecified,
  minHeight: Dp = Dp.Unspecified,
): PeekModifier =
  coreDefaultMinSize(minWidth = minWidth, minHeight = minHeight)

fun PeekModifier.size(size: Dp): PeekModifier =
  coreSize(size)

fun PeekModifier.size(width: Dp, height: Dp): PeekModifier =
  coreSize(width = width, height = height)

fun PeekModifier.wrapContentWidth(): PeekModifier =
  coreWrapContentWidth()

fun PeekModifier.wrapContentHeight(): PeekModifier =
  coreWrapContentHeight()

fun PeekModifier.wrapContentSize(): PeekModifier =
  coreWrapContentSize()

fun PeekModifier.fillMaxWidth(): PeekModifier =
  coreFillMaxWidth()

fun PeekModifier.fillMaxHeight(): PeekModifier =
  coreFillMaxHeight()

fun PeekModifier.fillMaxSize(): PeekModifier =
  coreFillMaxSize()

fun PeekModifier.visibility(visibility: Visibility): PeekModifier =
  coreVisibility(visibility)

fun PeekModifier.clickable(pendingIntent: PendingIntent): PeekModifier =
  coreClickable(pendingIntent)

fun PeekModifier.clickable(onClick: PeekAction): PeekModifier =
  coreClickable(onClick)
