package com.extopy.usecases.posts

import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.repositories.IUpdateModelSuspendUseCase
import dev.kaccelero.models.UUID
import io.ktor.http.*

class UpdatePostUseCase(
    private val repository: IPostsRepository,
) : IUpdateModelSuspendUseCase<Post, UUID, PostPayload> {

    override suspend fun invoke(input1: UUID, input2: PostPayload): Post? {
        if (input2.body.isBlank()) throw ControllerException(
            HttpStatusCode.BadRequest,
            "posts_body_empty"
        )
        return if (repository.update(input1, input2)) repository.get(input1)
        else null
    }

}
