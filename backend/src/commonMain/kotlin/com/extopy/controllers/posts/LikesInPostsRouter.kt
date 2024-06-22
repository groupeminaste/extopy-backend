package com.extopy.controllers.posts

import com.extopy.models.posts.LikeInPost
import com.extopy.models.posts.Post
import dev.kaccelero.models.UUID
import dev.kaccelero.routers.APIChildModelRouter
import io.ktor.util.reflect.*

class LikesInPostsRouter(
    controller: ILikesInPostsController,
    postsRouter: PostsRouter,
) : APIChildModelRouter<LikeInPost, UUID, Unit, Unit, Post, UUID>(
    typeInfo<LikeInPost>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    controller,
    ILikesInPostsController::class,
    postsRouter,
    route = "likes",
    id = "userId",
    prefix = "/api/v1"
)
