package io.github.jakex7.peek.core

import androidx.compose.runtime.ComposableTargetMarker

// TODO(@jakex7): Remove `PeekComposable` annotation. It seems to do nothing.
@Retention(AnnotationRetention.BINARY)
@ComposableTargetMarker(description = "Peek Composable")
@Target(
  AnnotationTarget.FILE,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.TYPE,
  AnnotationTarget.TYPE_PARAMETER,
  AnnotationTarget.PROPERTY_GETTER,
)
annotation class PeekComposable
