package com.extopy.repositories.posts

import com.extopy.client.IExtopyClient
import com.extopy.models.posts.LikeInPost
import com.extopy.models.posts.Post
import com.extopy.models.users.User
import dev.kaccelero.models.RecursiveId
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.APIChildModelRemoteRepository
import dev.kaccelero.repositories.IAPIModelRemoteRepository
import dev.kaccelero.repositories.Pagination
import io.ktor.util.reflect.*

class LikesInPostsRemoteRepository(
    client: IExtopyClient,
    parentRepository: IAPIModelRemoteRepository<Post, UUID, *, *>,
) : APIChildModelRemoteRepository<LikeInPost, UUID, Unit, Unit, UUID>(
    typeInfo<LikeInPost>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    typeInfo<List<LikeInPost>>(),
    client,
    parentRepository,
    route = "likes",
    prefix = "/api/v1"
), ILikesInPostsRemoteRepository {

    override suspend fun list(pagination: Pagination, postId: UUID): List<LikeInPost> =
        list(pagination, RecursiveId<User, UUID, Unit>(postId), null)

    override suspend fun create(postId: UUID): LikeInPost? =
        create(Unit, RecursiveId<User, UUID, Unit>(postId), null)

    override suspend fun delete(postId: UUID, userId: UUID): Boolean =
        delete(userId, RecursiveId<User, UUID, Unit>(postId), null)

}
