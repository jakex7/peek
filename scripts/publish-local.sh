#!/usr/bin/env bash
# Publishes all Peek library artifacts to the local Maven cache.
#
# Usage:
#   ./scripts/publish-local.sh

set -euo pipefail

if [[ $# -ne 0 ]]; then
  echo "Usage: $0" >&2
  exit 1
fi

echo ""
echo "Publishing Peek libraries to local Maven..."
echo ""

./gradlew \
  :peek-core:publishToMavenLocal \
  :peek-runtime:publishToMavenLocal \
  :peek-remoteviews:publishToMavenLocal \
  :peek-notification:publishToMavenLocal \
  :peek-appwidget:publishToMavenLocal \
  :peek-emittables:publishToMavenLocal \
  :peek-testing:publishToMavenLocal \
  --no-configuration-cache

echo ""
echo "Done. Artifacts published to ~/.m2/repository/io/github/jakex7/peek/"
