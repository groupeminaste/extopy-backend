package com.extopy.controllers.posts

import com.extopy.models.posts.LikeInPost
import com.extopy.models.posts.Post
import dev.kaccelero.annotations.*
import dev.kaccelero.controllers.IChildModelController
import io.ktor.server.application.*

interface ILikesInPostsController : IChildModelController<LikeInPost, String, Unit, Unit, Post, String> {

    @APIMapping
    @ListModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "posts_not_found")
    suspend fun list(
        call: ApplicationCall,
        @ParentModel parent: Post,
        @QueryParameter limit: Long?,
        @QueryParameter offset: Long?,
    ): List<LikeInPost>

    @APIMapping
    @CreateModelPath
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(404, "posts_not_found")
    @DocumentedError(500, "error_internal")
    suspend fun create(call: ApplicationCall, @ParentModel parent: Post, @Payload payload: Unit): LikeInPost

    @APIMapping
    @DeleteModelPath
    @DocumentedType(LikeInPost::class)
    @DocumentedError(401, "auth_invalid_credentials")
    @DocumentedError(403, "likes_in_posts_delete_not_allowed")
    @DocumentedError(404, "posts_not_found")
    @DocumentedError(500, "error_internal")
    suspend fun delete(call: ApplicationCall, @ParentModel parent: Post, @Id id: String)

}
