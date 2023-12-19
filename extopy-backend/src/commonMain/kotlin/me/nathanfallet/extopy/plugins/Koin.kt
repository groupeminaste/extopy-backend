package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import me.nathanfallet.extopy.controllers.auth.AuthRouter
import me.nathanfallet.extopy.controllers.notifications.NotificationsRouter
import me.nathanfallet.extopy.controllers.posts.IPostsController
import me.nathanfallet.extopy.controllers.posts.PostsController
import me.nathanfallet.extopy.controllers.posts.PostsRouter
import me.nathanfallet.extopy.controllers.timelines.TimelinesController
import me.nathanfallet.extopy.controllers.timelines.TimelinesRouter
import me.nathanfallet.extopy.controllers.users.IUsersController
import me.nathanfallet.extopy.controllers.users.UsersController
import me.nathanfallet.extopy.controllers.users.UsersRouter
import me.nathanfallet.extopy.database.Database
import me.nathanfallet.extopy.database.application.DatabaseClientsRepository
import me.nathanfallet.extopy.database.application.DatabaseCodesInEmailsRepository
import me.nathanfallet.extopy.database.posts.DatabasePostsRepository
import me.nathanfallet.extopy.database.users.DatabaseClientsInUsersRepository
import me.nathanfallet.extopy.database.users.DatabaseUsersRepository
import me.nathanfallet.extopy.models.application.Client
import me.nathanfallet.extopy.models.auth.LoginPayload
import me.nathanfallet.extopy.models.auth.RegisterCodePayload
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.repositories.application.ICodesInEmailsRepository
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.extopy.repositories.users.IClientsInUsersRepository
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import me.nathanfallet.extopy.services.emails.EmailsService
import me.nathanfallet.extopy.services.emails.IEmailsService
import me.nathanfallet.extopy.services.jwt.IJWTService
import me.nathanfallet.extopy.services.jwt.JWTService
import me.nathanfallet.extopy.usecases.application.SendEmailUseCase
import me.nathanfallet.extopy.usecases.auth.*
import me.nathanfallet.extopy.usecases.posts.*
import me.nathanfallet.extopy.usecases.timelines.GetTimelineByIdUseCase
import me.nathanfallet.extopy.usecases.timelines.IGetTimelineByIdUseCase
import me.nathanfallet.extopy.usecases.users.*
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
import me.nathanfallet.usecases.models.create.context.ICreateModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.delete.IDeleteModelSuspendUseCase
import me.nathanfallet.usecases.models.get.GetModelFromRepositorySuspendUseCase
import me.nathanfallet.usecases.models.get.IGetModelSuspendUseCase
import me.nathanfallet.usecases.models.get.context.GetModelWithContextFromRepositorySuspendUseCase
import me.nathanfallet.usecases.models.get.context.IGetModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.repositories.IModelSuspendRepository
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
            single<IJWTService> {
                JWTService(
                    environment.config.property("jwt.secret").getString(),
                    environment.config.property("jwt.issuer").getString()
                )
            }
        }
        val repositoryModule = module {
            // Application
            single<ICodesInEmailsRepository> { DatabaseCodesInEmailsRepository(get()) }
            single<IModelSuspendRepository<Client, String, Unit, Unit>>(named<Client>()) {
                DatabaseClientsRepository(get())
            }

            // Users
            single<IUsersRepository> { DatabaseUsersRepository(get()) }
            single<IClientsInUsersRepository> { DatabaseClientsInUsersRepository(get()) }

            // Posts
            single<IPostsRepository> { DatabasePostsRepository(get()) }
        }
        val useCaseModule = module {
            // Application
            single<ISendEmailUseCase> { SendEmailUseCase(get()) }
            single<ITranslateUseCase> { TranslateUseCase() }
            single<IGetLocaleForCallUseCase> { GetLocaleForCallUseCase() }
            single<IGetModelSuspendUseCase<Client, String>>(named<Client>()) {
                GetModelFromRepositorySuspendUseCase(get(named<Client>()))
            }

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
            single<IGetClientUseCase> { GetClientFromModelUseCase<Client>(get(named<Client>())) }
            single<ICreateAuthCodeUseCase> { CreateAuthCodeUseCase(get()) }
            single<IGetAuthCodeUseCase> { GetAuthCodeUseCase(get(), get(), get(named<User>())) }
            single<IDeleteAuthCodeUseCase> { DeleteAuthCodeUseCase(get()) }
            single<IGenerateAuthTokenUseCase> {
                GenerateAuthTokenUseCase(get())
            }

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
            single<IGetUserPostsUseCase> { GetUserPostsUseCase(get()) }

            // Posts
            single<IGetModelWithContextSuspendUseCase<Post, String>>(named<Post>()) {
                GetModelWithContextFromRepositorySuspendUseCase(get<IPostsRepository>())
            }
            single<ICreateModelWithContextSuspendUseCase<Post, PostPayload>>(named<Post>()) {
                CreatePostUseCase(get())
            }
            single<IUpdateModelSuspendUseCase<Post, String, PostPayload>>(named<Post>()) {
                UpdatePostUseCase(get())
            }
            single<IDeleteModelSuspendUseCase<Post, String>>(named<Post>()) {
                DeletePostUseCase(get())
            }
            single<IGetPostRepliesUseCase> { GetPostRepliesUseCase(get()) }

            // Timelines
            single<IGetTimelineByIdUseCase> { GetTimelineByIdUseCase(get()) }
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
            single<IUsersController> {
                UsersController(
                    get(),
                    get(named<User>()),
                    get(named<User>()),
                    get()
                )
            }

            // Posts
            single<IPostsController> {
                PostsController(
                    get(),
                    get(named<Post>()),
                    get(named<Post>()),
                    get(named<Post>()),
                    get(named<Post>()),
                    get()
                )
            }

            // Timelines
            single<IModelController<Timeline, String, Unit, Unit>>(named<Timeline>()) {
                TimelinesController(
                    get(),
                    get()
                )
            }
        }
        val routerModule = module {
            single { AuthRouter(get(), get()) }
            single { UsersRouter(get()) }
            single { PostsRouter(get()) }
            single { TimelinesRouter(get(named<Timeline>())) }
            single { NotificationsRouter() }
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
