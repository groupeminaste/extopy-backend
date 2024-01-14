package me.nathanfallet.extopy.repositories.timelines

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.timelines.Timeline

interface ITimelinesRemoteRepository {

    suspend fun get(id: String): Timeline?
    suspend fun getPosts(id: String, limit: Long, offset: Long): List<Post>

}
