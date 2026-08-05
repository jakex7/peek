# Peek

Peek supplies the Glance pieces AndroidX does not yet provide: notification composition and
extensible custom components translated to RemoteViews.

## Modules

- `peek-glance` provides custom Glance components.
- `peek-notification` composes Glance content into `NotificationCompat` custom views.
- `peek-emittables` inserts prebuilt Glance emittable trees and provides a multiprocess AppWidget
  adapter for low-level tree producers.

## Glance fork resolver

Peek `0.2.0` is pinned to Glance `1.2.0-rc01`. Apply the resolver plugin once in the consuming
Android project:

```kotlin
plugins {
  id("io.github.jakex7.peek.glance-fork") version "0.2.0"
}
```

Then add Peek dependency:

```kotlin
dependencies {
  implementation("io.github.jakex7.peek:peek-notification:0.2.0")
}
```

Continue declaring normal `androidx.glance` dependencies. The plugin substitutes only
`glance-appwidget` with
`io.github.jakex7.peek.forks:glance-appwidget:1.2.0-rc01-peek-0.2.0`, including transitive requests
from multiprocess and testing artifacts, and fails the build if another Glance version is present.

## License

Peek is released under the [MIT License](LICENSE).
