package com.extopy.usecases.posts

import com.extopy.models.posts.Post
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.commons.repositories.IDeleteModelSuspendUseCase
import dev.kaccelero.models.UUID

class DeletePostUseCase(
    private val repository: IPostsRepository,
) : IDeleteModelSuspendUseCase<Post, UUID> {

    override suspend fun invoke(input: UUID): Boolean = repository.delete(input).also { success ->
        if (!success) return@also
        /*
        // TODO: Delete related data
        LikesInPosts.deleteWhere {
           postId eq id
        }
        Posts.deleteWhere {
            Posts.id eq id
        }
        Posts.selectAll().where {
            repliedToId eq id or (repostOfId eq id)
        }.forEach {
            delete(it[Posts.id])
        }
         */
    }

}
