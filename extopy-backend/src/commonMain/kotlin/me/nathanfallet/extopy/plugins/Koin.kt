package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import me.nathanfallet.extopy.controllers.auth.AuthController
import me.nathanfallet.extopy.controllers.auth.AuthRouter
import me.nathanfallet.extopy.controllers.auth.IAuthController
import me.nathanfallet.extopy.controllers.notifications.NotificationsRouter
import me.nathanfallet.extopy.controllers.posts.*
import me.nathanfallet.extopy.controllers.timelines.ITimelinesController
import me.nathanfallet.extopy.controllers.timelines.TimelinesController
import me.nathanfallet.extopy.controllers.timelines.TimelinesRouter
import me.nathanfallet.extopy.controllers.users.*
import me.nathanfallet.extopy.controllers.web.IWebController
import me.nathanfallet.extopy.controllers.web.WebController
import me.nathanfallet.extopy.controllers.web.WebRouter
import me.nathanfallet.extopy.database.Database
import me.nathanfallet.extopy.database.application.ClientsDatabaseRepository
import me.nathanfallet.extopy.database.application.CodesInEmailsDatabaseRepository
import me.nathanfallet.extopy.database.posts.LikesInPostsDatabaseRepository
import me.nathanfallet.extopy.database.posts.PostsDatabaseRepository
import me.nathanfallet.extopy.database.users.ClientsInUsersDatabaseRepository
import me.nathanfallet.extopy.database.users.FollowersInUsersDatabaseRepository
import me.nathanfallet.extopy.database.users.UsersDatabaseRepository
import me.nathanfallet.extopy.models.application.Client
import me.nathanfallet.extopy.models.posts.LikeInPost
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.extopy.models.users.CreateUserPayload
import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.extopy.models.users.UpdateUserPayload
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.extopy.repositories.application.ICodesInEmailsRepository
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.extopy.repositories.users.IClientsInUsersRepository
import me.nathanfallet.extopy.repositories.users.IFollowersInUsersRepository
import me.nathanfallet.extopy.repositories.users.IUsersRepository
import me.nathanfallet.extopy.services.emails.EmailsService
import me.nathanfallet.extopy.services.emails.IEmailsService
import me.nathanfallet.extopy.services.jwt.IJWTService
import me.nathanfallet.extopy.services.jwt.JWTService
import me.nathanfallet.extopy.usecases.application.*
import me.nathanfallet.extopy.usecases.auth.*
import me.nathanfallet.extopy.usecases.posts.*
import me.nathanfallet.extopy.usecases.timelines.GetTimelineByIdUseCase
import me.nathanfallet.extopy.usecases.timelines.GetTimelinePostsUseCase
import me.nathanfallet.extopy.usecases.timelines.IGetTimelineByIdUseCase
import me.nathanfallet.extopy.usecases.timelines.IGetTimelinePostsUseCase
import me.nathanfallet.extopy.usecases.users.*
import me.nathanfallet.i18n.usecases.localization.TranslateUseCase
import me.nathanfallet.ktorx.database.sessions.SessionsDatabaseRepository
import me.nathanfallet.ktorx.repositories.sessions.ISessionsRepository
import me.nathanfallet.ktorx.usecases.localization.GetLocaleForCallUseCase
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.ktorx.usecases.users.IGetUserForCallUseCase
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.ktorx.usecases.users.RequireUserForCallUseCase
import me.nathanfallet.surexposed.database.IDatabase
import me.nathanfallet.usecases.emails.ISendEmailUseCase
import me.nathanfallet.usecases.localization.ITranslateUseCase
import me.nathanfallet.usecases.models.create.ICreateModelSuspendUseCase
import me.nathanfallet.usecases.models.create.context.CreateChildModelWithContextFromRepositorySuspendUseCase
import me.nathanfallet.usecases.models.create.context.ICreateChildModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.create.context.ICreateModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.delete.DeleteChildModelFromRepositorySuspendUseCase
import me.nathanfallet.usecases.models.delete.IDeleteChildModelSuspendUseCase
import me.nathanfallet.usecases.models.delete.IDeleteModelSuspendUseCase
import me.nathanfallet.usecases.models.get.GetModelFromRepositorySuspendUseCase
import me.nathanfallet.usecases.models.get.IGetModelSuspendUseCase
import me.nathanfallet.usecases.models.get.context.GetModelWithContextFromRepositorySuspendUseCase
import me.nathanfallet.usecases.models.get.context.IGetModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.list.slice.IListSliceChildModelSuspendUseCase
import me.nathanfallet.usecases.models.list.slice.ListSliceChildModelFromRepositorySuspendUseCase
import me.nathanfallet.usecases.models.list.slice.context.IListSliceModelWithContextSuspendUseCase
import me.nathanfallet.usecases.models.list.slice.context.ListSliceModelWithContextFromRepositorySuspendUseCase
import me.nathanfallet.usecases.models.repositories.IChildModelSuspendRepository
import me.nathanfallet.usecases.models.repositories.IModelSuspendRepository
import me.nathanfallet.usecases.models.update.IUpdateModelSuspendUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        val databaseModule = module {
            single<IDatabase> {
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
            single<ICodesInEmailsRepository> { CodesInEmailsDatabaseRepository(get()) }
            single<IModelSuspendRepository<Client, String, Unit, Unit>>(named<Client>()) {
                ClientsDatabaseRepository(get())
            }
            single<ISessionsRepository> { SessionsDatabaseRepository(get()) }

            // Users
            single<IUsersRepository> { UsersDatabaseRepository(get()) }
            single<IClientsInUsersRepository> { ClientsInUsersDatabaseRepository(get()) }
            single<IFollowersInUsersRepository> { FollowersInUsersDatabaseRepository(get()) }

            // Posts
            single<IPostsRepository> { PostsDatabaseRepository(get()) }
            single<IChildModelSuspendRepository<LikeInPost, String, Unit, Unit, String>>(named<LikeInPost>()) {
                LikesInPostsDatabaseRepository(get())
            }
        }
        val useCaseModule = module {
            // Application
            single<ISendEmailUseCase> { SendEmailUseCase(get()) }
            single<IExpireUseCase> { ExpireUseCase() }
            single<ITranslateUseCase> { TranslateUseCase() }
            single<IGetLocaleForCallUseCase> { GetLocaleForCallUseCase() }
            single<IGetCodeInEmailUseCase> { GetCodeInEmailUseCase(get()) }
            single<ICreateCodeInEmailUseCase> { CreateCodeInEmailUseCase(get(), get()) }
            single<IDeleteCodeInEmailUseCase> { DeleteCodeInEmailUseCase(get()) }
            single<IGetModelSuspendUseCase<Client, String>>(named<Client>()) {
                GetModelFromRepositorySuspendUseCase(get(named<Client>()))
            }
            single<IGetClientForUserForRefreshTokenUseCase> {
                GetClientForUserForRefreshTokenUseCase(get(), get(named<User>()), get(named<Client>()))
            }

            // Auth
            single<IHashPasswordUseCase> { HashPasswordUseCase() }
            single<IVerifyPasswordUseCase> { VerifyPasswordUseCase() }
            single<IGetJWTPrincipalForCallUseCase> { GetJWTPrincipalForCallUseCase() }
            single<IGetSessionForCallUseCase> { GetSessionForCallUseCase() }
            single<ISetSessionForCallUseCase> { SetSessionForCallUseCase() }
            single<IClearSessionForCallUseCase> { ClearSessionForCallUseCase() }
            single<ILoginUseCase> { LoginUseCase(get(), get()) }
            single<IRegisterUseCase> { RegisterUseCase(get(), get(named<User>())) }
            single<ICreateAuthCodeUseCase> { CreateAuthCodeUseCase(get()) }
            single<IGetAuthCodeUseCase> { GetAuthCodeUseCase(get(), get(named<Client>()), get(named<User>())) }
            single<IDeleteAuthCodeUseCase> { DeleteAuthCodeUseCase(get()) }
            single<IGenerateAuthTokenUseCase> { GenerateAuthTokenUseCase(get()) }

            // Users
            single<IRequireUserForCallUseCase> { RequireUserForCallUseCase(get()) }
            single<IGetUserForCallUseCase> { GetUserForCallUseCase(get(), get(), get(named<User>())) }
            single<IListSliceModelWithContextSuspendUseCase<User>>(named<User>()) {
                ListSliceModelWithContextFromRepositorySuspendUseCase(get<IUsersRepository>())
            }
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
            single<ICreateChildModelWithContextSuspendUseCase<FollowerInUser, Unit, String>>(named<FollowerInUser>()) {
                CreateFollowerInUserUseCase(get(), get())
            }
            single<IDeleteChildModelSuspendUseCase<FollowerInUser, String, String>>(named<FollowerInUser>()) {
                DeleteChildModelFromRepositorySuspendUseCase(get<IFollowersInUsersRepository>())
            }
            single<IListSliceChildModelSuspendUseCase<FollowerInUser, String>>(named<FollowerInUser>()) {
                ListSliceChildModelFromRepositorySuspendUseCase(get<IFollowersInUsersRepository>())
            }
            single<IListFollowingInUserUseCase> { ListFollowingInUserUseCase(get()) }

            // Posts
            single<IListSliceModelWithContextSuspendUseCase<Post>>(named<Post>()) {
                ListSliceModelWithContextFromRepositorySuspendUseCase(get<IPostsRepository>())
            }
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
            single<IListSliceChildModelSuspendUseCase<LikeInPost, String>>(named<LikeInPost>()) {
                ListSliceChildModelFromRepositorySuspendUseCase(
                    get<IChildModelSuspendRepository<LikeInPost, String, Unit, Unit, String>>(named<LikeInPost>())
                )
            }
            single<ICreateChildModelWithContextSuspendUseCase<LikeInPost, Unit, String>>(named<LikeInPost>()) {
                CreateChildModelWithContextFromRepositorySuspendUseCase(get(named<LikeInPost>()))
            }
            single<IDeleteChildModelSuspendUseCase<LikeInPost, String, String>>(named<LikeInPost>()) {
                DeleteChildModelFromRepositorySuspendUseCase(get(named<LikeInPost>()))
            }

            // Timelines
            single<IGetTimelineByIdUseCase> { GetTimelineByIdUseCase() }
            single<IGetTimelinePostsUseCase> { GetTimelinePostsUseCase(get()) }
        }
        val controllerModule = module {
            // Static web pages
            single<IWebController> { WebController() }

            // Auth
            single<IAuthController> {
                AuthController(
                    get(),
                    get(),
                    get(),
                    get(),
                    get(),
                    get(named<Client>()),
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
                    get(named<User>()),
                    get()
                )
            }
            single<IFollowersInUsersController> {
                FollowersInUsersController(
                    get(),
                    get(named<FollowerInUser>()),
                    get(),
                    get(named<FollowerInUser>()),
                    get(named<FollowerInUser>())
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
                    get(named<Post>()),
                    get()
                )
            }
            single<ILikesInPostsController> {
                LikesInPostsController(
                    get(),
                    get(named<LikeInPost>()),
                    get(named<LikeInPost>()),
                    get(named<LikeInPost>())
                )
            }

            // Timelines
            single<ITimelinesController> {
                TimelinesController(
                    get(),
                    get(),
                    get()
                )
            }
        }
        val routerModule = module {
            single { WebRouter(get()) }
            single { AuthRouter(get(), get()) }
            single { UsersRouter(get()) }
            single { FollowersInUsersRouter(get(), get()) }
            single { PostsRouter(get()) }
            single { LikesInPostsRouter(get(), get()) }
            single { TimelinesRouter(get()) }
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
