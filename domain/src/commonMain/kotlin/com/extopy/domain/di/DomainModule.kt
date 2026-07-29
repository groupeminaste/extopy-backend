package com.extopy.domain.di

import com.extopy.domain.usecases.application.*
import com.extopy.domain.usecases.auth.*
import com.extopy.domain.usecases.posts.*
import com.extopy.domain.usecases.timelines.*
import com.extopy.domain.usecases.users.*
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin module for domain layer dependencies.
 *
 * @param baseUrl Where this deployment is reachable, which the registration email links back to.
 *                It is a parameter rather than something read from a configuration, so that the
 *                domain keeps knowing nothing about how the application is configured.
 */
fun domainModule(baseUrl: String): Module = module {
    // Application
    single<CreateCodeInEmailUseCase> { CreateCodeInEmailUseCaseImpl(get(), get(), get()) }
    single<GetCodeInEmailUseCase> { GetCodeInEmailUseCaseImpl(get()) }
    single<DeleteCodeInEmailUseCase> { DeleteCodeInEmailUseCaseImpl(get()) }
    single<SendRegistrationEmailUseCase> { SendRegistrationEmailUseCaseImpl(get(), get(), baseUrl) }

    // Auth
    single<LoginUseCase> { LoginUseCaseImpl(get(), get()) }
    single<RegisterUseCase> { RegisterUseCaseImpl(get(), get()) }
    single<GetClientUseCase> { GetClientUseCaseImpl(get()) }
    single<CreateAuthCodeUseCase> { CreateAuthCodeUseCaseImpl(get(), get()) }
    single<GetAuthCodeUseCase> { GetAuthCodeUseCaseImpl(get(), get(), get()) }
    single<DeleteAuthCodeUseCase> { DeleteAuthCodeUseCaseImpl(get()) }
    single<GenerateAuthTokenUseCase> { GenerateAuthTokenUseCaseImpl(get()) }
    single<GetClientForUserForRefreshTokenUseCase> {
        GetClientForUserForRefreshTokenUseCaseImpl(get(), get(), get())
    }

    // Users
    single<ListUsersUseCase> { ListUsersUseCaseImpl(get()) }
    single<GetUserUseCase> { GetUserUseCaseImpl(get()) }
    single<CreateUserUseCase> { CreateUserUseCaseImpl(get(), get()) }
    single<UpdateUserUseCase> { UpdateUserUseCaseImpl(get(), get()) }
    single<GetUserPostsUseCase> { GetUserPostsUseCaseImpl(get()) }

    // Followers
    single<ListFollowersInUserUseCase> { ListFollowersInUserUseCaseImpl(get()) }
    single<ListFollowingInUserUseCase> { ListFollowingInUserUseCaseImpl(get()) }
    single<CreateFollowerInUserUseCase> { CreateFollowerInUserUseCaseImpl(get(), get()) }
    single<DeleteFollowerInUserUseCase> { DeleteFollowerInUserUseCaseImpl(get()) }

    // Posts
    single<ListPostsUseCase> { ListPostsUseCaseImpl(get()) }
    single<GetPostUseCase> { GetPostUseCaseImpl(get()) }
    single<CreatePostUseCase> { CreatePostUseCaseImpl(get()) }
    single<UpdatePostUseCase> { UpdatePostUseCaseImpl(get()) }
    single<DeletePostUseCase> { DeletePostUseCaseImpl(get()) }
    single<GetPostRepliesUseCase> { GetPostRepliesUseCaseImpl(get()) }

    // Likes
    single<ListLikesInPostUseCase> { ListLikesInPostUseCaseImpl(get()) }
    single<CreateLikeInPostUseCase> { CreateLikeInPostUseCaseImpl(get()) }
    single<DeleteLikeInPostUseCase> { DeleteLikeInPostUseCaseImpl(get()) }

    // Timelines
    single<GetTimelineUseCase> { GetTimelineUseCaseImpl() }
    single<GetTimelinePostsUseCase> { GetTimelinePostsUseCaseImpl(get()) }
}
