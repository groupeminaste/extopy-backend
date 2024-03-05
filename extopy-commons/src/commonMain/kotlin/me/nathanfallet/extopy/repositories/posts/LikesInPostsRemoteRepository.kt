package me.nathanfallet.extopy.repositories.posts

import io.ktor.util.reflect.*
import me.nathanfallet.extopy.client.IExtopyClient
import me.nathanfallet.extopy.models.posts.LikeInPost
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.User
import me.nathanfallet.ktorx.repositories.api.APIChildModelRemoteRepository
import me.nathanfallet.ktorx.repositories.api.IAPIModelRemoteRepository
import me.nathanfallet.usecases.models.id.RecursiveId
import me.nathanfallet.usecases.pagination.Pagination

class LikesInPostsRemoteRepository(
    client: IExtopyClient,
    parentRepository: IAPIModelRemoteRepository<Post, String, *, *>,
) : APIChildModelRemoteRepository<LikeInPost, String, Unit, Unit, String>(
    typeInfo<LikeInPost>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    typeInfo<List<LikeInPost>>(),
    client,
    parentRepository,
    route = "likes",
    prefix = "/api/v1"
), ILikesInPostsRemoteRepository {

    override suspend fun list(pagination: Pagination, postId: String): List<LikeInPost> =
        list(pagination, RecursiveId<User, String, Unit>(postId), null)

    override suspend fun create(postId: String): LikeInPost? =
        create(Unit, RecursiveId<User, String, Unit>(postId), null)

    override suspend fun delete(postId: String, userId: String): Boolean =
        delete(userId, RecursiveId<User, String, Unit>(postId), null)

}
