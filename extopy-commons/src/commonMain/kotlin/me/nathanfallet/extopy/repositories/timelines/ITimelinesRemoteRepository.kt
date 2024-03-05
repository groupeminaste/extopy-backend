package me.nathanfallet.extopy.repositories.timelines

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.usecases.pagination.Pagination

interface ITimelinesRemoteRepository {

    suspend fun get(id: String): Timeline?
    suspend fun getPosts(id: String, pagination: Pagination): List<Post>

}
