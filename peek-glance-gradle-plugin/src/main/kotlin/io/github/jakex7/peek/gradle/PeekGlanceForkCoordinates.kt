package io.github.jakex7.peek.gradle

public object PeekGlanceForkCoordinates {
  public const val supportedGlanceVersion: String = "1.2.0-rc01"
  public const val forkVersion: String = supportedGlanceVersion

  public const val officialGroup: String = "androidx.glance"
  public const val appWidgetModule: String = "glance-appwidget"
  public const val officialAppWidgetModule: String =
    "$officialGroup:$appWidgetModule"

  public const val forkGroup: String = "io.github.jakex7.peek.forks"
  public const val forkModule: String = "glance-appwidget"
  public const val forkGroupAndModule: String = "$forkGroup:$forkModule"
  public const val forkCoordinate: String = "$forkGroupAndModule:$forkVersion"
}
