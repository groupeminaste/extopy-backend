# extopy-backend

[![License](https://img.shields.io/github/license/groupeminaste/extopy-backend)](LICENSE)
[![Issues](https://img.shields.io/github/issues/groupeminaste/extopy-backend)]()
[![Pull Requests](https://img.shields.io/github/issues-pr/groupeminaste/extopy-backend)]()
[![Code Size](https://img.shields.io/github/languages/code-size/groupeminaste/extopy-backend)]()
[![codecov](https://codecov.io/gh/groupeminaste/extopy-backend/graph/badge.svg?token=iIM9xwE4QT)](https://codecov.io/gh/groupeminaste/extopy-backend)

The Backend server and API of Extopy, with common models and utilities.

## Modules

The dependencies only ever point inwards: `domain` knows nothing about Ktor or about the database,
and `presentation` knows nothing about the database.

| Module           | Depends on                | What lives there                                                                        |
|------------------|---------------------------|-----------------------------------------------------------------------------------------|
| `api`            | —                         | The contract: serializable models, the `@Resource` of every endpoint, requests, responses |
| `client`         | `api`                     | The HTTP client the applications talk to the API through                                  |
| `domain`         | —                         | The business rules: models, repository and service contracts, use cases                   |
| `infrastructure` | `domain`                  | What implements those contracts: database, tokens, passwords, emails, i18n                |
| `presentation`   | `api`, `domain`           | The HTTP layer: routes, mappers, web pages, ktor configuration                            |
| `app`            | all four of the above     | The assembly, the configuration, and the end-to-end tests                                 |

`api` and `client` are multiplatform and published to the clients; the rest is JVM only.

## Database

MySQL 8.4. The schema is Flyway's: the server applies the pending migrations of
`infrastructure/src/main/resources/db/migration` when it first connects, and nothing else creates or
alters a table. After changing a table in `infrastructure/.../database/tables`, generate its
migration with `./gradlew :infrastructure:generateMigrations` (Docker required: it replays the
history in a throwaway MySQL container), review it, and commit it. A merged migration is never
edited. The tests also run on MySQL 8.4 through Testcontainers, so Docker is needed for them too.

Run it with `./gradlew :app:run`, and everything with `./gradlew build`. The API documents itself at
`/docs`, off the routes that are actually mounted.

## Publishing `api` and `client`

Only `api` and `client` are published; the server modules never leave this repository.

**During development**, publish snapshots to Maven Local and point the app at them:

```sh
./gradlew :api:publishToMavenLocal :client:publishToMavenLocal   # com.extopy:*:0.2.0-SNAPSHOT
```

**Maven Central** is for occasional releases only. Since October 2026 Central enforces monthly
publishing limits per organization (number of releases, number of files, total size), so a release
is a deliberate step, run from a machine holding the Central credentials and the signing key:

```sh
./gradlew :api:publishToMavenCentral :client:publishToMavenCentral -PextopyVersion=0.2.0
```

The build refuses to send a `-SNAPSHOT` version to Central. Empty javadoc jars and the checksums
Central does not need are left out, to keep each release as small as possible. The deployment is
then released by hand from the Central Portal, and the app's catalog bumped to that version.
