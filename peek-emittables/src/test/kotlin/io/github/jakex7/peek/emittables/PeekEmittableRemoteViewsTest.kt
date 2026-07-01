package io.github.jakex7.peek.emittables

import android.content.Context
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import io.github.jakex7.peek.testing.findText
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class PeekEmittableRemoteViewsTest {
  @Test
  fun renderRootTranslatesEmittableTreeWithoutComposition() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val root =
      PeekRoot().also { root ->
        root.children +=
          EmittableColumn().also { column ->
            column.children += EmittableText().also { it.text = "Direct root" }
          }
      }

    val remoteViews = PeekEmittableRemoteViews.render(context, root)
    val applied = remoteViews.apply(context, FrameLayout(context))

    assertNotNull(applied.findText("Direct root"))
  }
}
