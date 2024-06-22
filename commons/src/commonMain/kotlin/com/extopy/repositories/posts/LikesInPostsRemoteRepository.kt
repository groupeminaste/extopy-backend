package com.extopy.repositories.posts

import com.extopy.client.IExtopyClient
import com.extopy.models.posts.LikeInPost
import com.extopy.models.posts.Post
import com.extopy.models.users.User
import dev.kaccelero.models.RecursiveId
import dev.kaccelero.repositories.APIChildModelRemoteRepository
import dev.kaccelero.repositories.IAPIModelRemoteRepository
import dev.kaccelero.repositories.Pagination
import io.ktor.util.reflect.*

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
