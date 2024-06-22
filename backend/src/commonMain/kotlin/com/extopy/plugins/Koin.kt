package com.extopy.plugins

import com.extopy.controllers.auth.AuthController
import com.extopy.controllers.auth.AuthRouter
import com.extopy.controllers.auth.IAuthController
import com.extopy.controllers.notifications.NotificationsRouter
import com.extopy.controllers.posts.*
import com.extopy.controllers.timelines.ITimelinesController
import com.extopy.controllers.timelines.TimelinesController
import com.extopy.controllers.timelines.TimelinesRouter
import com.extopy.controllers.users.*
import com.extopy.controllers.web.IWebController
import com.extopy.controllers.web.WebController
import com.extopy.controllers.web.WebRouter
import com.extopy.database.Database
import com.extopy.database.application.ClientsDatabaseRepository
import com.extopy.database.application.CodesInEmailsDatabaseRepository
import com.extopy.database.posts.LikesInPostsDatabaseRepository
import com.extopy.database.posts.PostsDatabaseRepository
import com.extopy.database.users.ClientsInUsersDatabaseRepository
import com.extopy.database.users.FollowersInUsersDatabaseRepository
import com.extopy.database.users.UsersDatabaseRepository
import com.extopy.models.application.Client
import com.extopy.models.posts.LikeInPost
import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import com.extopy.models.users.CreateUserPayload
import com.extopy.models.users.FollowerInUser
import com.extopy.models.users.UpdateUserPayload
import com.extopy.models.users.User
import com.extopy.repositories.application.ICodesInEmailsRepository
import com.extopy.repositories.posts.IPostsRepository
import com.extopy.repositories.users.IClientsInUsersRepository
import com.extopy.repositories.users.IFollowersInUsersRepository
import com.extopy.repositories.users.IUsersRepository
import com.extopy.services.emails.EmailsService
import com.extopy.services.emails.IEmailsService
import com.extopy.services.jwt.IJWTService
import com.extopy.services.jwt.JWTService
import com.extopy.usecases.application.*
import com.extopy.usecases.auth.*
import com.extopy.usecases.posts.*
import com.extopy.usecases.timelines.GetTimelineByIdUseCase
import com.extopy.usecases.timelines.GetTimelinePostsUseCase
import com.extopy.usecases.timelines.IGetTimelineByIdUseCase
import com.extopy.usecases.timelines.IGetTimelinePostsUseCase
import com.extopy.usecases.users.*
import dev.kaccelero.commons.auth.*
import dev.kaccelero.commons.emails.ISendEmailUseCase
import dev.kaccelero.commons.localization.GetLocaleForCallUseCase
import dev.kaccelero.commons.localization.IGetLocaleForCallUseCase
import dev.kaccelero.commons.localization.ITranslateUseCase
import dev.kaccelero.commons.localization.TranslateFromPropertiesUseCase
import dev.kaccelero.commons.repositories.*
import dev.kaccelero.commons.sessions.ISessionsRepository
import dev.kaccelero.commons.sessions.SessionsDatabaseRepository
import dev.kaccelero.commons.users.IGetUserForCallUseCase
import dev.kaccelero.commons.users.IRequireUserForCallUseCase
import dev.kaccelero.commons.users.RequireUserForCallUseCase
import dev.kaccelero.database.IDatabase
import dev.kaccelero.repositories.IChildModelSuspendRepository
import dev.kaccelero.repositories.IModelSuspendRepository
import io.ktor.server.application.*
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
            single<ITranslateUseCase> { TranslateFromPropertiesUseCase() }
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
