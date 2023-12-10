package me.nathanfallet.extopy.usecases.posts

import io.ktor.http.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.usecases.models.update.IUpdateModelSuspendUseCase

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
