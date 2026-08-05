# Peek Repository Instructions

## Architecture

- Peek extends official AndroidX Glance; do not recreate Glance's AppWidget runtime, state,
  previews, testing, sizing, or multiprocess support.
- Implement notification `RemoteViews` through Glance's experimental public
  `GlanceRemoteViews.compose()` API.
- Keep custom components extensible across RemoteViews translation. Follow the existing circular
  progress indicator implementation when adding components or modifiers.
- Keep `peek-emittables` available to non-Expo consumers. Expo Widgets may insert official Glance
  emittable trees directly, but Peek must work without Expo.
- Backwards compatibility with the removed Peek runtime is not required.

## Dependency Pins

- Keep Glance pinned exactly to `1.2.0-rc01`.
- The fork coordinate combines the original Glance version with the Peek version:
  `io.github.jakex7.peek.forks:glance-appwidget:1.2.0-rc01-peek-0.2.0`.
- Do not change `compileSdk`, `targetSdk`, `minSdk`, or AGP merely to work around fork publication
  metadata. Ask before changing these versions.

## Bumping Glance

1. Choose the released Glance version, then move `glance-fork/androidx` to the AndroidX source
   revision that produced that release. Do not relabel an arbitrary newer AndroidX checkout with an
   older Glance version.
2. Reapply only Peek's minimal translation extension changes and standalone-build support. Resolve
   upstream translator API changes in the fork rather than copying Glance implementations into
   Peek.
3. In the fork's `libraryversions.toml`, set `GLANCE` to the released version and keep
   `PEEK_GLANCE_APPWIDGET` referencing `versions.GLANCE`. The standalone publisher assigns the
   combined public fork version without relabeling the AndroidX dependency graph.
4. Update `androidx-glance` in `gradle/libs.versions.toml`, plus `peekVersion` and
   `supportedGlanceVersion` in `PeekGlanceForkCoordinates`. `forkVersion` must continue deriving
   from both versions.
5. Search for the old Glance version and update current documentation, the
   standalone-consumer fixture and its resolution assertions, and the equivalent Expo resolver.
   Never commit the Expo changes.
6. Publish the new fork coordinate, run the verification commands below, and resolve the standalone
   consumer's `debugRuntimeClasspath` with `--refresh-dependencies` to prove all transitive artifacts
   exist outside the AndroidX checkout.
7. Commit and publish the fork first, then update the parent repository's submodule pointer to that
   exact commit.

## Glance Fork

- `glance-fork/androidx` is a Git submodule. Never hardcode a local AOSP checkout path.
- Keep fork changes minimal and limited to the translation extension points and the standalone
  build support required to publish them.
- Do not add fork-only tests or change the fork's compile SDK unless explicitly requested.
- Do not add Google copyright headers to Peek-authored files.
- After changing and publishing the fork commit, update the parent repository's submodule pointer.

## Expo

- Expo integration may mirror the resolver behavior, but the standalone Peek resolver and
  libraries must remain fully functional without Expo.

## Verification

- Publish the fork, resolver plugin, and Peek artifacts with `./scripts/publish-local.sh`.
- Test the resolver plugin with `./gradlew -p peek-glance-gradle-plugin test`.
- Verify standalone resolution with
  `./gradlew -p integration-tests/standalone-consumer assembleDebug verifyStandaloneResolution`.
- After a dependency bump, force external transitive resolution with
  `./gradlew -p integration-tests/standalone-consumer dependencies --configuration debugRuntimeClasspath --refresh-dependencies`.
- Compile the remaining libraries with
  `./gradlew :peek-glance:compileDebugKotlin :peek-notification:compileDebugKotlin :peek-emittables:compileDebugKotlin`.
