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

Run it with `./gradlew :app:run`, and everything with `./gradlew build`. The API documents itself at
`/docs`, off the routes that are actually mounted.
