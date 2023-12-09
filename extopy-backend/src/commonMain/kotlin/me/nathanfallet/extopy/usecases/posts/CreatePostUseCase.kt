package me.nathanfallet.extopy.usecases.posts

import io.ktor.http.*
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.ktorx.models.exceptions.ControllerException
import me.nathanfallet.usecases.context.IContext
import me.nathanfallet.usecases.models.create.context.ICreateModelWithContextSuspendUseCase

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
            if (repository.get(repliedToId) == null) throw ControllerException(
                HttpStatusCode.BadRequest,
                "posts_replied_to_not_found"
            )
        }
        input1.repostOfId?.let { repostOfId ->
            if (repository.get(repostOfId) == null) throw ControllerException(
                HttpStatusCode.BadRequest,
                "posts_repost_of_not_found"
            )
        }
        return repository.create(input1, input2)
    }

}
