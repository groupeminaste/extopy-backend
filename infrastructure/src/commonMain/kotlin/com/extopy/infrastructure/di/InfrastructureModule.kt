package com.extopy.infrastructure.di

import com.extopy.domain.repositories.*
import com.extopy.domain.services.*
import com.extopy.infrastructure.bcrypt.BCryptPasswordEncoderService
import com.extopy.infrastructure.codes.RandomCodeGeneratorService
import com.extopy.infrastructure.database.*
import com.extopy.infrastructure.database.repositories.*
import com.extopy.infrastructure.emails.LoggingEmailsService
import com.extopy.infrastructure.emails.SmtpEmailsService
import com.extopy.infrastructure.health.DatabaseHealthService
import com.extopy.infrastructure.i18n.PropertiesTranslateService
import com.extopy.infrastructure.jwt.JwtTokenService
import com.extopy.infrastructure.sessions.DatabaseSessionStorage
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module for infrastructure-related dependencies.
 */
val Application.infrastructureModule: Module
    get() = module {
        // Database
        single {
            DatabaseConfig(
                protocol = environment.config.property("database.protocol").getString(),
                host = environment.config.property("database.host").getString(),
                name = environment.config.property("database.name").getString(),
                user = environment.config.property("database.user").getString(),
                password = environment.config.property("database.password").getString(),
            )
        }
        single<DatabaseFactory> {
            val config = get<DatabaseConfig>()
            when (config.protocol) {
                "mysql" -> MySQLDatabaseFactory(config)
                "h2" -> H2DatabaseFactory(config)
                else -> throw IllegalArgumentException("Unsupported database protocol: ${config.protocol}")
            }
        }
        single<TransactionManager> { TransactionManagerImpl(get()) }

        single<HealthService> { DatabaseHealthService(get()) }

        // Services
        single<PasswordEncoderService> { BCryptPasswordEncoderService() }
        single<TranslateService> { PropertiesTranslateService() }
        single<CodeGeneratorService> { RandomCodeGeneratorService() }
        single<EmailsService> {
            // A deployment without an SMTP host — a local run, or the tests — logs instead of
            // sending, rather than failing every request that has an email to send.
            val host = environment.config.property("email.host").getString()
            if (host.isEmpty()) LoggingEmailsService()
            else SmtpEmailsService(
                host,
                environment.config.property("email.username").getString(),
                environment.config.property("email.password").getString(),
            )
        }
        // The concrete type is bound too: the Ktor `jwt` provider needs its verifier.
        single {
            JwtTokenService(
                environment.config.property("jwt.secret").getString(),
                environment.config.property("jwt.issuer").getString(),
            )
        }
        single<TokenService> { get<JwtTokenService>() }

        // Repositories
        single<SessionsRepository> { SessionsDatabaseRepository(get()) }
        single<SessionStorage> { DatabaseSessionStorage(get()) }
        single<UsersRepository> { UsersDatabaseRepository(get()) }
        single<FollowersInUsersRepository> { FollowersInUsersDatabaseRepository(get()) }
        single<PostsRepository> { PostsDatabaseRepository(get()) }
        single<LikesInPostsRepository> { LikesInPostsDatabaseRepository(get()) }
        single<ClientsRepository> { ClientsDatabaseRepository(get()) }
        single<ClientsInUsersRepository> { ClientsInUsersDatabaseRepository(get()) }
        single<CodesInEmailsRepository> { CodesInEmailsDatabaseRepository(get()) }
    }
