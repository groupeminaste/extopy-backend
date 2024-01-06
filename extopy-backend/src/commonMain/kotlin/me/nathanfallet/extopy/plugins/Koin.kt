package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*
import me.nathanfallet.extopy.controllers.auth.AuthRouter
import me.nathanfallet.extopy.controllers.notifications.NotificationsRouter
import me.nathanfallet.extopy.controllers.posts.*
import me.nathanfallet.extopy.controllers.timelines.TimelinesController
import me.nathanfallet.extopy.controllers.timelines.TimelinesRouter
import me.nathanfallet.extopy.controllers.users.*
import me.nathanfallet.extopy.database.Database
import me.nathanfallet.extopy.database.application.ClientsDatabaseRepository
import me.nathanfallet.extopy.database.application.CodesInEmailsDatabaseRepository
import me.nathanfallet.extopy.database.posts.LikesInPostsDatabaseRepository
import me.nathanfallet.extopy.database.posts.PostsDatabaseRepository
import me.nathanfallet.extopy.database.users.ClientsInUsersDatabaseRepository
import me.nathanfallet.extopy.database.users.FollowersInUsersDatabaseRepository
import me.nathanfallet.extopy.database.users.UsersDatabaseRepository
import me.nathanfallet.extopy.models.application.Client
import me.nathanfallet.extopy.models.auth.LoginPayload
import me.nathanfallet.extopy.models.auth.RegisterCodePayload
import me.nathanfallet.extopy.models.auth.RegisterPayload
import me.nathanfallet.extopy.models.posts.LikeInPost
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.extopy.models.timelines.Timeline
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
import me.nathanfallet.extopy.usecases.application.SendEmailUseCase
import me.nathanfallet.extopy.usecases.auth.*
import me.nathanfallet.extopy.usecases.posts.*
import me.nathanfallet.extopy.usecases.timelines.GetTimelineByIdUseCase
import me.nathanfallet.extopy.usecases.timelines.IGetTimelineByIdUseCase
import me.nathanfallet.extopy.usecases.users.*
import me.nathanfallet.i18n.usecases.localization.TranslateUseCase
import me.nathanfallet.ktorx.controllers.IChildModelController
import me.nathanfallet.ktorx.controllers.IModelController
import me.nathanfallet.ktorx.controllers.auth.AuthWithCodeController
import me.nathanfallet.ktorx.controllers.auth.IAuthWithCodeController
import me.nathanfallet.ktorx.database.IDatabase
import me.nathanfallet.ktorx.database.sessions.SessionsDatabaseRepository
import me.nathanfallet.ktorx.repositories.sessions.ISessionsRepository
import me.nathanfallet.ktorx.usecases.auth.*
import me.nathanfallet.ktorx.usecases.localization.GetLocaleForCallUseCase
import me.nathanfallet.ktorx.usecases.localization.IGetLocaleForCallUseCase
import me.nathanfallet.ktorx.usecases.users.IGetUserForCallUseCase
import me.nathanfallet.ktorx.usecases.users.IRequireUserForCallUseCase
import me.nathanfallet.ktorx.usecases.users.RequireUserForCallUseCase
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
                    get()
                )
            }
            single<IChildModelController<LikeInPost, String, Unit, Unit, Post, String>>(named<LikeInPost>()) {
                LikesInPostsController(
                    get(),
                    get(named<LikeInPost>()),
                    get(named<LikeInPost>()),
                    get(named<LikeInPost>())
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
            single { FollowersInUsersRouter(get(), get()) }
            single { PostsRouter(get()) }
            single { LikesInPostsRouter(get(named<LikeInPost>()), get()) }
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
