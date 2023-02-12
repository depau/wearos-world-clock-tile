#!/bin/bash
set -euo pipefail

# (Ab)use the unit tests to pre-generate the Room database ahead of time.
# Room can be used on the host thanks to Roboelectric

./gradlew :tzdb_generator:testDebugUnitTest --tests "gay.depau.tzdb_generator.TzDBGeneratorTest.generateTzDB"

