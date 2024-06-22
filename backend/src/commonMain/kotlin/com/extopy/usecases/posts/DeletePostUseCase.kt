package com.extopy.usecases.posts

import com.extopy.models.posts.Post
import com.extopy.repositories.posts.IPostsRepository
import dev.kaccelero.commons.repositories.IDeleteModelSuspendUseCase

class DeletePostUseCase(
    private val repository: IPostsRepository,
) : IDeleteModelSuspendUseCase<Post, String> {

    override suspend fun invoke(input: String): Boolean {
        return repository.delete(input)
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
