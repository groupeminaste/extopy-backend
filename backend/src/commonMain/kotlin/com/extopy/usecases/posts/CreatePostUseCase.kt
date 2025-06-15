package com.extopy.usecases.posts

import com.extopy.models.posts.Post
import com.extopy.models.posts.PostPayload
import com.extopy.database.posts.IPostsRepository
import dev.kaccelero.commons.exceptions.ControllerException
import dev.kaccelero.commons.repositories.ICreateModelWithContextSuspendUseCase
import dev.kaccelero.models.IContext
import io.ktor.http.*

class CreatePostUseCase(
    private val repository: IPostsRepository,
) : ICreateModelWithContextSuspendUseCase<Post, PostPayload> {

    override suspend fun invoke(input1: PostPayload, input2: IContext): Post? {
        if (input1.body.isBlank() && input1.repostOfId == null) throw ControllerException(
            HttpStatusCode.BadRequest,
            "posts_body_empty"
        )
        if (input1.repostOfId != null && input1.repliedToId != null) throw ControllerException(
            HttpStatusCode.BadRequest,
            "posts_can_only_one_in_reply_or_repost"
        )
        input1.repliedToId?.let { repliedToId ->
            if (repository.get(repliedToId, input2) == null) throw ControllerException(
                HttpStatusCode.BadRequest,
                "posts_replied_to_not_found"
            )
        }
        input1.repostOfId?.let { repostOfId ->
            if (repository.get(repostOfId, input2) == null) throw ControllerException(
                HttpStatusCode.BadRequest,
                "posts_repost_of_not_found"
            )
        }
        return repository.create(input1, input2)
    }

}
