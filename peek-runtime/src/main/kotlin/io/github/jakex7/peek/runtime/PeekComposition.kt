package io.github.jakex7.peek.runtime

import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.Recomposer
import io.github.jakex7.peek.core.PeekApplier
import io.github.jakex7.peek.core.PeekComposable
import io.github.jakex7.peek.core.PeekRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object PeekComposition {
  /**
   * Composes [content] into a normalized [PeekRoot].
   *
   * Main-safe: composition runs on a single-threaded view of [Dispatchers.Default] regardless of
   * the calling dispatcher, so callers may invoke this from the main thread without blocking it.
   * The single-thread confinement keeps composition and recomposition off the main thread while
   * never running them concurrently.
   */
  suspend fun render(
    content: @Composable @PeekComposable () -> Unit,
  ): PeekRoot =
    withContext(CompositionContext) {
      coroutineScope {
        val root = PeekRoot()
        val recomposer = Recomposer(coroutineContext)
        val composition = Composition(PeekApplier(root), recomposer)
        val recomposerJob = launch { recomposer.runRecomposeAndApplyChanges() }

        composition.setContent(content)
        // `setContent` applies the initial composition synchronously, so `root` already
        // reflects the composed tree here. We then wait for the recomposer to settle so any
        // effect-driven recomposition is included. We intentionally do not require a non-empty
        // tree: legitimately empty content (e.g. an empty slot or a false conditional) must
        // resolve instead of spinning until the timeout and crashing the caller.
        withTimeout(RENDER_TIMEOUT_MILLIS.milliseconds) {
          recomposer.currentState.first { it == Recomposer.State.Idle }
        }

        try {
          PeekTreeNormalizer.normalize(root)
        } finally {
          composition.dispose()
          recomposer.cancel()
          recomposerJob.cancelAndJoin()
        }
      }
    }

  private const val RENDER_TIMEOUT_MILLIS = 5_000L

  // A single-threaded view of Dispatchers.Default plus the immediate frame clock. Confining
  // composition to one lane mirrors the single-threaded model Compose expects (as on the main
  // thread) while keeping the work off the caller's thread.
  private val CompositionContext: CoroutineContext =
    Dispatchers.Default.limitedParallelism(1) + ImmediateFrameClock
}

private object ImmediateFrameClock : MonotonicFrameClock {
  private val lastFrame = AtomicLong(System.nanoTime())

  override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R =
    onFrame(lastFrame.incrementAndGet())
}
