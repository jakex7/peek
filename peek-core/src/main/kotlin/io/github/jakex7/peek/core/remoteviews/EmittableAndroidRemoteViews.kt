package io.github.jakex7.peek.core.remoteviews

import android.view.View
import android.widget.RemoteViews
import io.github.jakex7.peek.core.Emittable
import io.github.jakex7.peek.core.EmittableWithChildren
import io.github.jakex7.peek.core.PeekModifier

class EmittableAndroidRemoteViews : EmittableWithChildren() {
  override var modifier: PeekModifier = PeekModifier
  var containerViewId: Int = View.NO_ID
  lateinit var remoteViews: RemoteViews

  override fun copy(): Emittable =
    EmittableAndroidRemoteViews().also {
      it.modifier = modifier
      it.containerViewId = containerViewId
      if (::remoteViews.isInitialized) {
        it.remoteViews = remoteViews
      }
      it.children += children.map { child -> child.copy() }
    }
}
