package io.github.jakex7.peek.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import io.github.jakex7.peek.core.Text
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class PeekNotificationBuilderTest {
  @Test
  fun setPeekContentAttachesCustomNotificationViews() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    val notification = runBlocking {
      NotificationCompat.Builder(context, "sync")
        .setSmallIcon(android.R.drawable.stat_sys_upload)
        .setContentTitle("Sync")
        .setPeekContent(
          context = context,
          collapsed = { Text("Uploading") },
          expanded = { Text("Uploading details") },
          headsUp = { Text("Upload running") },
        )
        .build()
    }

    assertNotNull(notification.contentView)
    assertNotNull(notification.bigContentView)
    assertNotNull(notification.headsUpContentView)
    assertEquals(
      Notification.FLAG_ONGOING_EVENT,
      notification.flags and Notification.FLAG_ONGOING_EVENT
    )
  }

  @Test
  fun peekNotificationViewsComposesAllSurfacesOffTheBuilder() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()

    val views =
      peekNotificationViews(
        context = context,
        collapsed = { Text("Collapsed") },
        expanded = { Text("Expanded") },
        headsUp = { Text("HeadsUp") },
      )

    val notification =
      NotificationCompat.Builder(context, "sync")
        .setSmallIcon(android.R.drawable.stat_sys_upload)
        .setPeekContent(views)
        .build()

    assertNotNull(views.expanded)
    assertNotNull(views.headsUp)
    assertNotNull(notification.contentView)
    assertNotNull(notification.bigContentView)
    assertNotNull(notification.headsUpContentView)
  }

  @Test
  fun addPeekActionAttachesNotificationAction() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val pendingIntent = pendingBroadcast(context, "pause")

    val notification =
      NotificationCompat.Builder(context, "sync")
        .setSmallIcon(android.R.drawable.stat_sys_upload)
        .addPeekAction(
          PeekNotificationAction(
            iconResId = android.R.drawable.ic_media_pause,
            title = "Pause",
            pendingIntent = pendingIntent,
          ),
        )
        .build()

    assertEquals(1, notification.actions.size)
    assertEquals("Pause", notification.actions[0].title.toString())
    assertEquals(pendingIntent, notification.actions[0].actionIntent)
  }

  @Test
  fun addPeekActionAttachesDirectReplyRemoteInput() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    val notification =
      NotificationCompat.Builder(context, "sync")
        .setSmallIcon(android.R.drawable.stat_sys_upload)
        .addPeekAction(
          PeekNotificationAction(
            iconResId = android.R.drawable.ic_dialog_email,
            title = "Reply",
            pendingIntent = pendingBroadcast(context, "reply"),
            remoteInputs =
              listOf(
                PeekNotificationRemoteInput(
                  resultKey = "peek_reply",
                  label = "Status update",
                  choices = listOf("Done", "Blocked"),
                ),
              ),
            semanticAction = NotificationCompat.Action.SEMANTIC_ACTION_REPLY,
          ),
        )
        .build()

    val action = notification.actions.single()
    val remoteInput = action.remoteInputs.single()
    assertEquals("Reply", action.title.toString())
    assertEquals(Notification.Action.SEMANTIC_ACTION_REPLY, action.semanticAction)
    assertEquals("peek_reply", remoteInput.resultKey)
    assertEquals("Status update", remoteInput.label.toString())
    assertEquals(listOf("Done", "Blocked"), remoteInput.choices.map { it.toString() })
    assertEquals(true, remoteInput.allowFreeFormInput)
  }

  @Test
  fun setPeekContentAttachesActions() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    val notification = runBlocking {
      NotificationCompat.Builder(context, "sync")
        .setSmallIcon(android.R.drawable.stat_sys_upload)
        .setPeekContent(
          context = context,
          collapsed = { Text("Uploading") },
          actions =
            listOf(
              PeekNotificationAction(
                iconResId = android.R.drawable.ic_menu_close_clear_cancel,
                title = "Cancel",
                pendingIntent = pendingBroadcast(context, "cancel"),
              ),
            ),
        )
        .build()
    }

    assertNotNull(notification.contentView)
    assertEquals(1, notification.actions.size)
    assertEquals("Cancel", notification.actions[0].title.toString())
  }

  @Test
  fun peekRemoteInputReadsDirectReplyResults() {
    val input = PeekNotificationRemoteInput("peek_reply", "Status update")
    val intent = Intent("reply")
    val results =
      Bundle().apply {
        putCharSequence("peek_reply", "Done")
      }

    PeekNotificationRemoteInput.addResultsToIntent(listOf(input), intent, results)

    val readResults = PeekNotificationRemoteInput.getResultsFromIntent(intent)
    assertEquals("Done", readResults?.getCharSequence("peek_reply").toString())
  }

  private fun pendingBroadcast(
    context: Context,
    action: String,
  ): PendingIntent =
    PendingIntent.getBroadcast(
      context,
      action.hashCode(),
      Intent(action).setPackage(context.packageName),
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )
}
