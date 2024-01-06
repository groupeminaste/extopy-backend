package me.nathanfallet.extopy.usecases.posts

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.repositories.posts.IPostsRepository
import me.nathanfallet.usecases.models.delete.IDeleteModelSuspendUseCase

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
