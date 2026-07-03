package io.github.jakex7.peek.core

import androidx.compose.runtime.ComposableTargetMarker

@ComposableTargetMarker(description = "Peek Composable")
@Retention(AnnotationRetention.BINARY)
@Target(
  AnnotationTarget.FILE,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.TYPE,
  AnnotationTarget.TYPE_PARAMETER,
  AnnotationTarget.PROPERTY_GETTER,
)
annotation class PeekComposable
