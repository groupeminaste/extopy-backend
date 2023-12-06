package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import me.nathanfallet.extopy.controllers.auth.AuthRouter
import me.nathanfallet.extopy.controllers.notifications.NotificationsRouter
import me.nathanfallet.extopy.controllers.posts.PostsRouter
import me.nathanfallet.extopy.controllers.users.UsersController
import me.nathanfallet.extopy.controllers.users.UsersRouter
import me.nathanfallet.extopy.database.Database
import me.nathanfallet.extopy.database.users.DatabaseUsersRepository
import me.nathanfallet.extopy.models.auth.LoginPayload
import me.nathanfallet.extopy.models.auth.RegisterCodePayload
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import me.nathanfallet.extopy.usecases.auth.CreateCodeRegisterUseCase
import me.nathanfallet.extopy.usecases.auth.GetCodeRegisterUseCase
import me.nathanfallet.extopy.usecases.auth.RegisterUseCase
import me.nathanfallet.extopy.usecases.users.GetUserForCallUseCase
import me.nathanfallet.i18n.usecases.localization.TranslateUseCase
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.controllers.auth.AuthWithCodeController
import me.nathanfallet.ktorx.controllers.auth.IAuthWithCodeController
import me.nathanfallet.ktorx.usecases.auth.ICreateCodeRegisterUseCase
import me.nathanfallet.ktorx.usecases.auth.IGetCodeRegisterUseCase
import me.nathanfallet.ktorx.usecases.auth.IRegisterUseCase
import me.nathanfallet.ktorx.usecases.localization.GetLocaleForCallUseCase
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.ktorx.usecases.users.IGetUserForCallUseCase
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.ktorx.usecases.users.RequireUserForCallUseCase
import me.nathanfallet.usecases.localization.ITranslateUseCase
import me.nathanfallet.usecases.models.get.context.GetModelWithContextFromRepositorySuspendUseCase
import me.nathanfallet.usecases.models.get.context.IGetModelWithContextSuspendUseCase
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
        val repositoryModule = module {
            single<IUsersRepository> { DatabaseUsersRepository(get()) }
        }
        val useCaseModule = module {
            // Application
            single<ITranslateUseCase> { TranslateUseCase() }
            single<IGetLocaleForCallUseCase> { GetLocaleForCallUseCase() }

            // Auth
            single<IRegisterUseCase<RegisterCodePayload>> { RegisterUseCase(get(), get()) }
            single<IGetCodeRegisterUseCase<RegisterPayload>> { GetCodeRegisterUseCase() }
            single<ICreateCodeRegisterUseCase<RegisterPayload>> {
                CreateCodeRegisterUseCase(
                    get(),
                    get(),
                    get()
                )
            }

            // Users
            single<IRequireUserForCallUseCase> { RequireUserForCallUseCase(get()) }
            single<IGetUserForCallUseCase> { GetUserForCallUseCase() }
            single<IGetModelWithContextSuspendUseCase<User, String>>(named<User>()) {
                GetModelWithContextFromRepositorySuspendUseCase(get<IUsersRepository>())
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
            repositoryModule,
            useCaseModule,
            controllerModule,
            routerModule
        )
    }
}
