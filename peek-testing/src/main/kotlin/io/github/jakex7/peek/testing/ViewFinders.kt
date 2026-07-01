package io.github.jakex7.peek.testing

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView

/**
 * View-tree search helpers for asserting on an applied [android.widget.RemoteViews] hierarchy in
 * Robolectric tests. Each walks the receiver's subtree depth-first.
 */

fun View.findText(text: String): TextView? {
  if (this is TextView && this.text.toString() == text) return this
  if (this !is ViewGroup) return null
  for (index in 0 until childCount) {
    val found = getChildAt(index).findText(text)
    if (found != null) return found
  }
  return null
}

fun View.findFirstLinearLayout(): LinearLayout? {
  if (this is LinearLayout) return this
  if (this !is ViewGroup) return null
  return (0 until childCount).firstNotNullOfOrNull { index ->
    getChildAt(index).findFirstLinearLayout()
  }
}

fun View.findAllTextViews(): List<TextView> {
  if (this is TextView) return listOf(this)
  if (this !is ViewGroup) return emptyList()
  return (0 until childCount).flatMap { index ->
    getChildAt(index).findAllTextViews()
  }
}

fun View.findFirstImageView(): ImageView? {
  if (this is ImageView) return this
  if (this !is ViewGroup) return null
  return (0 until childCount).firstNotNullOfOrNull { index ->
    getChildAt(index).findFirstImageView()
  }
}

fun View.findAllImageViews(): List<ImageView> {
  if (this is ImageView) return listOf(this)
  if (this !is ViewGroup) return emptyList()
  return (0 until childCount).flatMap { index ->
    getChildAt(index).findAllImageViews()
  }
}

fun View.findFirstCheckBox(): CheckBox? {
  if (this is CheckBox) return this
  if (this !is ViewGroup) return null
  return (0 until childCount).firstNotNullOfOrNull { index ->
    getChildAt(index).findFirstCheckBox()
  }
}

fun View.findFirstSwitch(): Switch? {
  if (this is Switch) return this
  if (this !is ViewGroup) return null
  return (0 until childCount).firstNotNullOfOrNull { index ->
    getChildAt(index).findFirstSwitch()
  }
}

fun View.findFirstRadioButton(): RadioButton? {
  if (this is RadioButton) return this
  if (this !is ViewGroup) return null
  return (0 until childCount).firstNotNullOfOrNull { index ->
    getChildAt(index).findFirstRadioButton()
  }
}

fun View.findFirstRadioGroup(): RadioGroup? {
  if (this is RadioGroup) return this
  if (this !is ViewGroup) return null
  return (0 until childCount).firstNotNullOfOrNull { index ->
    getChildAt(index).findFirstRadioGroup()
  }
}

fun View.findFirstProgressBar(): ProgressBar? {
  if (this is ProgressBar) return this
  if (this !is ViewGroup) return null
  return (0 until childCount).firstNotNullOfOrNull { index ->
    getChildAt(index).findFirstProgressBar()
  }
}

fun View.findFirstCompoundButton(): CompoundButton? {
  if (this is CompoundButton) return this
  if (this !is ViewGroup) return null
  return (0 until childCount).firstNotNullOfOrNull { index ->
    getChildAt(index).findFirstCompoundButton()
  }
}

fun View.findViewWithExactTag(tag: String): View? {
  if (this.tag == tag) return this
  if (this !is ViewGroup) return null
  return (0 until childCount).firstNotNullOfOrNull { index ->
    getChildAt(index).findViewWithExactTag(tag)
  }
}

fun View.findColorView(color: Int): View? {
  if ((background as? ColorDrawable)?.color == color) {
    return this
  }
  if (this !is ViewGroup) return null
  return (0 until childCount).firstNotNullOfOrNull { index ->
    getChildAt(index).findColorView(color)
  }
}
