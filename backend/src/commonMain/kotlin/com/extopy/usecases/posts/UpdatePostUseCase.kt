package com.extopy.usecases.posts

import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.repositories.IUpdateModelSuspendUseCase
import io.ktor.http.*

class UpdatePostUseCase(
    private val repository: IPostsRepository,
) : IUpdateModelSuspendUseCase<Post, String, PostPayload> {

    override suspend fun invoke(input1: String, input2: PostPayload): Post? {
        if (input2.body.isBlank()) throw ControllerException(
            HttpStatusCode.BadRequest,
            "posts_body_empty"
        )
        return if (repository.update(input1, input2)) repository.get(input1)
        else null
    }

}
