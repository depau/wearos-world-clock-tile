# Timezone database generator

The only purpose of this module is to pre-populate the timezone SQLite3 database during development.

The database is generated within a unit test of the module.

This is because the tests use Roboelectric to run with an Android-like environment, which is
required in order for Room to operate correctly.


## Regenerating the database

```bash
./gradlew :tzdb_generator:testDebugUnitTest --tests "gay.depau.tzdb_generator.TzDBGeneratorTest.generateTzDB"
```
