package io.github.jakex7.peek.sample

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.jakex7.peek.appwidget.LocalPeekWidgetSize
import io.github.jakex7.peek.appwidget.PeekAppWidget
import io.github.jakex7.peek.appwidget.PeekAppWidgetId
import io.github.jakex7.peek.appwidget.PeekAppWidgetReceiver
import io.github.jakex7.peek.appwidget.PeekAppWidgetSizeMode
import io.github.jakex7.peek.core.Alignment
import io.github.jakex7.peek.core.Button
import io.github.jakex7.peek.core.CircularProgressIndicator
import io.github.jakex7.peek.core.ColorProvider
import io.github.jakex7.peek.core.Column
import io.github.jakex7.peek.core.FontWeight
import io.github.jakex7.peek.core.LinearProgressIndicator
import io.github.jakex7.peek.core.PeekComposable
import io.github.jakex7.peek.core.PeekModifier
import io.github.jakex7.peek.core.PeekTheme
import io.github.jakex7.peek.core.Row
import io.github.jakex7.peek.core.Spacer
import io.github.jakex7.peek.core.Text
import io.github.jakex7.peek.core.actionStartActivity
import io.github.jakex7.peek.core.background
import io.github.jakex7.peek.core.fillMaxSize
import io.github.jakex7.peek.core.fillMaxWidth
import io.github.jakex7.peek.core.height
import io.github.jakex7.peek.core.padding
import io.github.jakex7.peek.core.width

class SampleAppWidgetReceiver : PeekAppWidgetReceiver() {
  override val peekAppWidget: PeekAppWidget
    get() = SampleAppWidget
}

internal object SampleAppWidget : PeekAppWidget() {
  override val sizeMode: PeekAppWidgetSizeMode =
    PeekAppWidgetSizeMode.Responsive(
      setOf(
        DpSize(150.dp, 96.dp),
        DpSize(260.dp, 120.dp),
      ),
    )

  override suspend fun provideContent(
    context: Context,
    id: PeekAppWidgetId,
  ): @Composable @PeekComposable () -> Unit = {
    val size = LocalPeekWidgetSize.current
    val wide = size.width >= 220.dp
    Column(
      modifier =
        PeekModifier
          .fillMaxSize()
          .background(PeekTheme.colors.background)
          .padding(12.dp),
    ) {
      Text(
        text = "Peek widget",
        fontWeight = FontWeight.Bold,
        maxLines = 1,
      )
      Spacer(modifier = PeekModifier.height(6.dp))
      Text(
        text = if (wide) "RemoteViews from the same Peek DSL" else "RemoteViews DSL",
        maxLines = 2,
      )
      Spacer(modifier = PeekModifier.height(10.dp))
      Row(
        modifier = PeekModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Button(
          text = "Open",
          onClick =
            actionStartActivity(
              Intent(context, MainActivity::class.java)
//                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            ),
        )
        if (wide) {
          Spacer(modifier = PeekModifier.width(10.dp))
          Text(
            text = "${size.width.value.toInt()} x ${size.height.value.toInt()} dp",
//            color = PeekTheme.colors.onPrimary,
            maxLines = 1,
          )
        }
      }
      LinearProgressIndicator(progress = .3f)
      CircularProgressIndicator(
        progress = .5f,
        trackColor = ColorProvider(Color(0xffff0000)),
      )
    }
  }
}
