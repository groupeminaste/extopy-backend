package com.extopy.presentation.di

import com.extopy.presentation.routes.auth.AuthApiRoutesDependencies
import com.extopy.presentation.routes.auth.AuthWebRoutesDependencies
import com.extopy.presentation.routes.health.HealthRoutesDependencies
import com.extopy.presentation.routes.posts.LikesInPostsRoutesDependencies
import com.extopy.presentation.routes.posts.PostsRoutesDependencies
import com.extopy.presentation.routes.timelines.TimelinesRoutesDependencies
import com.extopy.presentation.routes.users.FollowersInUsersRoutesDependencies
import com.extopy.presentation.routes.users.UsersRoutesDependencies
import org.koin.dsl.module

/**
 * Koin module for presentation layer dependencies.
 */
val presentationModule = module {
    // Probes
    single { HealthRoutesDependencies(get()) }

    // API routes
    single { AuthApiRoutesDependencies(get(), get(), get(), get()) }
    single { UsersRoutesDependencies(get(), get(), get(), get()) }
    single { FollowersInUsersRoutesDependencies(get(), get(), get(), get(), get()) }
    single { PostsRoutesDependencies(get(), get(), get(), get(), get(), get(), get()) }
    single { LikesInPostsRoutesDependencies(get(), get(), get(), get(), get()) }
    single { TimelinesRoutesDependencies(get(), get(), get()) }

    // Web pages
    single { AuthWebRoutesDependencies(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
