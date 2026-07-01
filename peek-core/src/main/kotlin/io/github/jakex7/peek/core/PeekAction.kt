package io.github.jakex7.peek.core

import android.app.PendingIntent
import android.content.Intent

sealed interface PeekAction

data class PendingIntentPeekAction(
  val pendingIntent: PendingIntent,
) : PeekAction

data class StartActivityPeekAction(
  val intent: Intent,
  val requestCode: Int = 0,
  val flags: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
) : PeekAction

data class SendBroadcastPeekAction(
  val intent: Intent,
  val requestCode: Int = 0,
  val flags: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
) : PeekAction

fun actionPendingIntent(pendingIntent: PendingIntent): PeekAction =
  PendingIntentPeekAction(pendingIntent)

fun actionStartActivity(
  intent: Intent,
  requestCode: Int = 0,
  flags: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
): PeekAction =
  StartActivityPeekAction(intent, requestCode, flags)

fun actionSendBroadcast(
  intent: Intent,
  requestCode: Int = 0,
  flags: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
): PeekAction =
  SendBroadcastPeekAction(intent, requestCode, flags)
