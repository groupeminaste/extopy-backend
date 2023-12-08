package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import me.nathanfallet.extopy.controllers.auth.AuthRouter
import me.nathanfallet.extopy.controllers.notifications.NotificationsRouter
import me.nathanfallet.extopy.controllers.posts.PostsRouter
import me.nathanfallet.extopy.controllers.users.UsersController
import me.nathanfallet.extopy.controllers.users.UsersRouter
import me.nathanfallet.extopy.database.Database
import me.nathanfallet.extopy.database.application.DatabaseCodesInEmailsRepository
import me.nathanfallet.extopy.database.users.DatabaseUsersRepository
import me.nathanfallet.extopy.models.auth.LoginPayload
import me.nathanfallet.extopy.models.auth.RegisterCodePayload
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.repositories.application.ICodesInEmailsRepository
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import me.nathanfallet.extopy.services.emails.EmailsService
import me.nathanfallet.extopy.services.emails.IEmailsService
import me.nathanfallet.extopy.usecases.application.SendEmailUseCase
import me.nathanfallet.extopy.usecases.auth.*
import me.nathanfallet.extopy.usecases.users.CreateUserUseCase
import me.nathanfallet.extopy.usecases.users.GetUserForCallUseCase
import me.nathanfallet.extopy.usecases.users.UpdateUserUseCase
import me.nathanfallet.i18n.usecases.localization.TranslateUseCase
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.controllers.auth.AuthWithCodeController
import me.nathanfallet.ktorx.controllers.auth.IAuthWithCodeController
import me.nathanfallet.ktorx.usecases.auth.*
import me.nathanfallet.ktorx.usecases.localization.GetLocaleForCallUseCase
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.ktorx.usecases.users.IGetUserForCallUseCase
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.ktorx.usecases.users.RequireUserForCallUseCase
import me.nathanfallet.usecases.emails.ISendEmailUseCase
import me.nathanfallet.usecases.localization.ITranslateUseCase
import me.nathanfallet.usecases.models.create.ICreateModelSuspendUseCase
import me.nathanfallet.usecases.models.get.context.GetModelWithContextFromRepositorySuspendUseCase
import me.nathanfallet.usecases.models.get.context.IGetModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.update.IUpdateModelSuspendUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        val databaseModule = module {
            single {
                Database(
                    environment.config.property("database.protocol").getString(),
                    environment.config.property("database.host").getString(),
                    environment.config.property("database.name").getString(),
                    environment.config.property("database.user").getString(),
                    environment.config.property("database.password").getString()
                )
            }
        }
        val serviceModule = module {
            single<IEmailsService> {
                EmailsService(
                    environment.config.property("email.host").getString(),
                    environment.config.property("email.username").getString(),
                    environment.config.property("email.password").getString()
                )
            }
        }
        val repositoryModule = module {
            single<ICodesInEmailsRepository> { DatabaseCodesInEmailsRepository(get()) }
            single<IUsersRepository> { DatabaseUsersRepository(get()) }
        }
        val useCaseModule = module {
            // Application
            single<ISendEmailUseCase> { SendEmailUseCase(get()) }
            single<ITranslateUseCase> { TranslateUseCase() }
            single<IGetLocaleForCallUseCase> { GetLocaleForCallUseCase() }

            // Auth
            single<IHashPasswordUseCase> { HashPasswordUseCase() }
            single<IVerifyPasswordUseCase> { VerifyPasswordUseCase() }
            single<IGetJWTPrincipalForCallUseCase> { GetJWTPrincipalForCallUseCase() }
            single<ICreateSessionForUserUseCase> { CreateSessionForUserUseCase() }
            single<IGetSessionForCallUseCase> { GetSessionForCallUseCase() }
            single<ISetSessionForCallUseCase> { SetSessionForCallUseCase() }
            single<ILoginUseCase<LoginPayload>> { LoginUseCase(get(), get()) }
            single<IRegisterUseCase<RegisterCodePayload>> { RegisterUseCase(get(), get(named<User>())) }
            single<IGetCodeRegisterUseCase<RegisterPayload>> { GetCodeRegisterUseCase(get()) }
            single<ICreateCodeRegisterUseCase<RegisterPayload>> {
                CreateCodeRegisterUseCase(
                    get(),
                    get(),
                    get(),
                    get(),
                    get()
                )
            }
            single<IDeleteCodeRegisterUseCase> { DeleteCodeRegisterUseCase(get()) }

            // Users
            single<IRequireUserForCallUseCase> { RequireUserForCallUseCase(get()) }
            single<IGetUserForCallUseCase> { GetUserForCallUseCase(get(), get(), get(named<User>())) }
            single<IGetModelWithContextSuspendUseCase<User, String>>(named<User>()) {
                GetModelWithContextFromRepositorySuspendUseCase(get<IUsersRepository>())
            }
            single<ICreateModelSuspendUseCase<User, CreateUserPayload>>(named<User>()) {
                CreateUserUseCase(get(), get())
            }
            single<IUpdateModelSuspendUseCase<User, String, UpdateUserPayload>>(named<User>()) {
                UpdateUserUseCase(get(), get())
            }
        }
        val controllerModule = module {
            // Auth
            single<IAuthWithCodeController<LoginPayload, RegisterPayload, RegisterCodePayload>> {
                AuthWithCodeController(
                    get(),
                    get(),
                    get(),
                    get(),
                    get(),
                    get(),
                    get()
                )
            }

            // Users
            single<IModelController<User, String, CreateUserPayload, UpdateUserPayload>>(named<User>()) {
                UsersController(
                    get(),
                    get(named<User>()),
                    get(named<User>())
                )
            }
        }
        val routerModule = module {
            single { AuthRouter(get(), get()) }
            single { UsersRouter(get(named<User>())) }
            single { NotificationsRouter() }
            single { PostsRouter() }
        }

        modules(
            databaseModule,
            serviceModule,
            repositoryModule,
            useCaseModule,
            controllerModule,
            routerModule
        )
    }
}
