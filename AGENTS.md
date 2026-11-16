# Extopy backend: contributor and agent instructions

These rules hold for every change, by a person or by an agent. When a rule and the code disagree,
the rule wins and the code is fixed in the same PR, or the rule is changed on purpose in that PR.

## Working

- All repository content is in English. Root package and Maven group: `com.extopy`.
- Work on a branch, never on `main`; one subject per PR. PRs are squash-merged.
- Put the evidence in the PR description (tests run, EXPLAIN plans, what was checked by hand), not
  in a shared log or changelog file.
- Never stop processes or Gradle daemons you did not start (no `./gradlew --stop`, no `pkill java`).
- Build with JDK 21 and the checked-in Gradle wrapper. CI (`.github/workflows/build.yml`) is the
  authoritative list of commands.

## Choosing a library or a technology

- **Do not add a dependency, a Gradle plugin, an external service or a new technology on your own.**
  Ask Nathan first, with the options and their trade-offs; he decides. Upgrading a version that is
  already in the catalog is fine.
- Every version lives in `gradle/libs.versions.toml`; no version written in a build script.

## Architecture

- Modules and their only allowed dependencies (they point inwards):
  - `api` → nothing. The contract: `@Resource` of each endpoint, serializable requests and
    responses. Multiplatform, published.
  - `client` → `api`. The HTTP client the apps use. Multiplatform, published.
  - `domain` → nothing (JVM). Models, repository and service contracts, use cases. No Ktor, no
    Exposed, no serialization of the wire format.
  - `infrastructure` → `domain`. Exposed tables and repositories, Flyway, JWT, bcrypt, emails, i18n.
  - `presentation` → `api`, `domain`. Routes, mappers between `api` and `domain`, web pages.
  - `app` → all of them: the composition root (Koin), configuration, end-to-end tests.
- Business rules live in use cases (`XxxUseCase` interface + `XxxUseCaseImpl`), never in routes,
  repositories or mappers. A route reads the request, calls a use case, maps the result.
- Constructor injection everywhere; Koin only in the modules' `di` files and in `app`.
- Domain models never leak to the wire: `presentation/mappers` translate them to `api` responses,
  and a change of the API contract stops at that boundary.
- Errors are domain exceptions (`domain/exceptions`), mapped to HTTP statuses in one place
  (`presentation/config/ErrorHandling.kt`).
- `api` and `client` are common code compiled for jvm, js, iosArm64 and iosSimulatorArm64: only
  `kotlin.*` and multiplatform libraries there (`kotlin.time.Instant`, `encodeToByteArray()`, no `java.*`).

## Database

- MySQL only. The tests run on MySQL 8.4 too (Testcontainers, Docker required), never on an
  in-memory substitute, so what is tested is what runs.
- Exposed DSL for every query, mutation and lock, test fixtures included. No raw SQL, no `exec`.
- HikariCP for the pool, **Flyway for the schema**: nothing calls `SchemaUtils.create` or alters
  a table at runtime (only `MigrationsTest` does, to reproduce a database built before Flyway).
- A schema change is a new migration under `infrastructure/src/main/resources/db/migration`,
  generated from the Exposed table definitions (`./gradlew :infrastructure:generateMigrations`, Docker
  required; it replays the committed history in a throwaway MySQL 8.4 container), then
  read and corrected by hand before it is committed (renames, drops, type changes, defaults).
- **A migration that has been merged is never edited**, renamed or deleted: Flyway checks the
  checksums. Fix it with a new migration.
- Migrations are additive whenever possible (add a nullable column, backfill, then tighten in a
  later migration), so that the previous version of the server keeps working during a rollout.

## Queries: performance is part of correctness

- **Every new or changed query gets `EXPLAIN ANALYZE` evidence in its PR**, run on MySQL 8.4 against
  tables seeded to a realistic size (hundreds of thousands of posts and follow links, not an empty
  schema). Exposed runs it for you: `explain(analyze = true) { <the query> }.toList()` inside a
  transaction, on the exact query the repository builds. Paste the plan before and after. Add or fix an index when the plan shows a table scan,
  a filter applied after the index lookup, a temporary table or a filesort on a hot path.
- Indexes follow the filter order: equality columns first, then the range or sort column.
- No N+1: never call a repository per row of a list (`posts.map { users.get(it.userId) }`); read
  everything in one query with joins or `inList`.
- Every list is bounded (`limit`), and pagination of feeds should move to keyset (`WHERE
  published_at < ?`) rather than `OFFSET`, which gets slower page after page.
- Prefer `exists` / `limit(1)` to `COUNT(*)` when only presence matters. Beware of counters made
  with several joins and `countDistinct`: the joins multiply rows; check the plan and the row
  counts, and prefer subqueries or stored counters when it grows.
- Never select large text columns on list endpoints unless they are shown.
- Never hold a transaction across a network call (email, push, HTTP).

## Code

- Kotlin official style. Small, named functions; no `!!` in production code.
- Coroutines: never swallow `CancellationException` (rethrow it first in any broad `catch`, no
  `runCatching` around suspend calls). Blocking JDBC runs on `Dispatchers.IO`.
- Bound everything that comes from outside: request bodies, string lengths (see the limits in the
  domain models), list sizes, retries, timeouts.
- Never log secrets, tokens, passwords or full request bodies.
- Every endpoint that writes checks who is calling (ownership, `personal` accounts, banned users)
  in the use case, with a test for the refusal.

## Tests

- A bug fix starts with a test that fails without the fix.
- Use cases: unit tests with mocked repositories (`domain/src/test`).
- Repositories: tests against MySQL (`infrastructure/src/test`, Docker required). `MigrationsTest`
  fails when a table changed without its migration.
- Routes: `presentation/src/test`; the whole HTTP surface: `app/src/test`.
- `./gradlew build` must pass before a PR is opened.

## Publishing

- Only `api` and `client` are published. During development: `publishToMavenLocal` with the default
  `-SNAPSHOT` version, and the app reads them from Maven Local.
- Maven Central has monthly limits per organization (releases, files, size). A Central release is
  occasional and decided by Nathan: `-PextopyVersion=x.y.z`, never a snapshot.
- Published targets: jvm, js, iosArm64, iosSimulatorArm64. Each target adds files to every Central
  release: ask before adding one.
