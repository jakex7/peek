package io.github.jakex7.peek.sample

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.jakex7.peek.core.Alignment
import io.github.jakex7.peek.core.Button
import io.github.jakex7.peek.core.ColorProvider
import io.github.jakex7.peek.core.Column
import io.github.jakex7.peek.core.FontWeight
import io.github.jakex7.peek.core.Image
import io.github.jakex7.peek.core.ImageProvider
import io.github.jakex7.peek.core.LinearProgressIndicator
import io.github.jakex7.peek.core.PeekComposable
import io.github.jakex7.peek.core.PeekModifier
import io.github.jakex7.peek.core.Row
import io.github.jakex7.peek.core.Spacer
import io.github.jakex7.peek.core.Text
import io.github.jakex7.peek.core.TextAlign
import io.github.jakex7.peek.core.VerticalAlignment
import io.github.jakex7.peek.core.actionSendBroadcast
import io.github.jakex7.peek.core.background
import io.github.jakex7.peek.core.fillMaxWidth
import io.github.jakex7.peek.core.height
import io.github.jakex7.peek.core.padding
import io.github.jakex7.peek.core.width
import io.github.jakex7.peek.notification.setPeekContent
import kotlin.math.roundToInt

internal object SampleNotifications {
  const val ChannelId = "peek_sample"
  const val NotificationId = 1001
  const val MatchNotificationId = 1002
  const val ActionTogglePause = "io.github.jakex7.peek.sample.action.TOGGLE_PAUSE"
  const val ExtraPaused = "io.github.jakex7.peek.sample.extra.PAUSED"
  const val ExtraProgress = "io.github.jakex7.peek.sample.extra.PROGRESS"

  @SuppressLint("MissingPermission")
  suspend fun postOngoingUpdate(
    context: Context,
    progress: Float,
    paused: Boolean = false,
  ) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val progressPercent = (clampedProgress * 100).roundToInt()
    val statusText = if (paused) "Backup paused" else "Uploading backup"
    val notification =
      NotificationCompat.Builder(context, ChannelId).setSmallIcon(R.drawable.ic_peek_notification)
        .setContentTitle("Peek sample sync").setOnlyAlertOnce(true).setPeekContent(
          context = context,
          collapsed = {
            Text(statusText, maxLines = 1)
          },
          expanded = {
            Column(
              modifier = PeekModifier.fillMaxWidth().padding(vertical = 12.dp)
                .background(ColorProvider(Color.Red)),
            ) {
              Row(
                modifier = PeekModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Image(
                  provider = ImageProvider(android.R.drawable.stat_sys_upload),
                  contentDescription = null,
                )
                Spacer(modifier = PeekModifier.width(8.dp))
                Text(statusText, maxLines = 1)
              }
              Spacer(modifier = PeekModifier.height(8.dp))
              LinearProgressIndicator(
                progress = clampedProgress,
                modifier = PeekModifier.fillMaxWidth(),
              )
              Spacer(modifier = PeekModifier.height(8.dp))
              Row(
                modifier = PeekModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text("$progressPercent% complete", maxLines = 1)
                Spacer(modifier = PeekModifier.width(8.dp))
                Button(
                  text = if (paused) "Resume" else "Pause",
                  onClick = actionSendBroadcast(
                    Intent(context, SampleNotificationActionReceiver::class.java).apply {
                      action = ActionTogglePause
                      putExtra(ExtraProgress, clampedProgress)
                      putExtra(ExtraPaused, paused)
                    },
                  ),
                )
              }
            }
          },
          headsUp = {
            Text(
              text = if (paused) "Backup is paused" else "Backup is still running",
              modifier = PeekModifier.padding(12.dp),
              maxLines = 1,
            )
          },
        ).build()

    NotificationManagerCompat.from(context).notify(NotificationId, notification)
  }

  @SuppressLint("MissingPermission")
  suspend fun postMatchUpdate(context: Context) {
    val notification =
      NotificationCompat.Builder(context, ChannelId).setSmallIcon(R.drawable.ic_peek_notification)
        .setContentTitle("Real Madrid 0 : 0 Barcelona").setContentText("12' live")
        .setCategory(NotificationCompat.CATEGORY_EVENT).setOnlyAlertOnce(true).setPeekContent(
          context = context,
          collapsed = {
            MatchCollapsedContent()
          },
          expanded = {
            MatchScoreboardContent()
          },
          headsUp = {
            MatchScoreboardContent()
          },
        ).build()

    NotificationManagerCompat.from(context).notify(MatchNotificationId, notification)
  }

  fun cancel(context: Context) {
    NotificationManagerCompat.from(context).run {
      cancel(NotificationId)
      cancel(MatchNotificationId)
    }
  }

  fun isOngoingNotificationActive(context: Context): Boolean =
    context.getSystemService(NotificationManager::class.java).activeNotifications.any { it.id == NotificationId }

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val channel = NotificationChannel(
      ChannelId,
      "Peek sample",
      NotificationManager.IMPORTANCE_LOW,
    ).apply {
      description = "Ongoing notifications rendered with Peek."
    }
    context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
  }

  @Composable
  @PeekComposable
  private fun MatchCollapsedContent() {
    Row(
      modifier = PeekModifier.fillMaxWidth(),
      verticalAlignment = VerticalAlignment.CenterVertically
    ) {
      Text(
        text = "🇵🇱 2 : 1 🇦🇱",
        maxLines = 1,
        fontSizeSp = 32,
      )
      Spacer(modifier = PeekModifier.width(32.dp))
      Text(
        text = "73'",
        maxLines = 1,
        color = ColorProvider(Color(0x88000000)),
        fontWeight = FontWeight.Normal
      )
    }
  }

  @Composable
  @PeekComposable
  private fun MatchScoreboardContent() {
    Row(
      modifier = PeekModifier.fillMaxWidth().padding(vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      TeamColumn(
        name = "Poland",
        flag = "🇵🇱"
      )
      Spacer(modifier = PeekModifier.width(16.dp))
      Column(
        modifier = PeekModifier.width(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "2 : 1",
          fontSizeSp = 30,
          maxLines = 1,
          textAlign = TextAlign.Center,
        )
        Spacer(modifier = PeekModifier.height(2.dp))
        Text(
          text = "73'",
          color = MatchMinute,
          fontSizeSp = 12,
          maxLines = 1,
          textAlign = TextAlign.Center,
        )
      }
      Spacer(modifier = PeekModifier.width(16.dp))
      TeamColumn(
        name = "Albania",
        flag = "🇦🇱"
      )
    }
  }

  @Composable
  @PeekComposable
  private fun TeamColumn(
    name: String,
    flag: String
  ) {
    Column(
      modifier = PeekModifier.width(70.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = flag,
        fontSizeSp = 32,
        maxLines = 1,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = PeekModifier.height(4.dp))
      Text(
        text = name,
        fontSizeSp = 12,
        maxLines = 1,
        textAlign = TextAlign.Center,
      )
    }
  }

  private val MatchMinute = ColorProvider(Color(0xFF9D9D9D))
}
