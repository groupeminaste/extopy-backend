package me.nathanfallet.extopy.repositories.posts

import io.ktor.util.reflect.*
import me.nathanfallet.extopy.client.IExtopyClient
import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.ktorx.repositories.api.APIModelRemoteRepository

class PostsRemoteRepository(
    client: IExtopyClient,
) : APIModelRemoteRepository<Post, String, PostPayload, PostPayload>(
    typeInfo<Post>(),
    typeInfo<PostPayload>(),
    typeInfo<PostPayload>(),
    typeInfo<List<Post>>(),
    client,
    prefix = "/api/v1"
), IPostsRemoteRepository {

    override suspend fun get(id: String): Post? {
        return get(id, null)
    }

    override suspend fun create(payload: PostPayload): Post? {
        return create(payload, null)
    }

    override suspend fun update(id: String, payload: PostPayload): Post? {
        return update(id, payload, null)
    }

    override suspend fun delete(id: String): Boolean {
        return delete(id, null)
    }

}
