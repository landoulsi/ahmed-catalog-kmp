#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

./gradlew \
  :shared:testAndroidHostTest \
  :shared:iosSimulatorArm64Test \
  :androidApp:testDebugUnitTest

if ! adb devices | awk 'NR > 1 && $2 == "device" { found = 1 } END { exit !found }'; then
  echo "No Android device or emulator is connected. Start one, then re-run." >&2
  exit 1
fi

./gradlew :androidApp:connectedDebugAndroidTest