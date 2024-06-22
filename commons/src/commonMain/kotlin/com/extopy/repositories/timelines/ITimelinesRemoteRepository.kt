package com.extopy.repositories.timelines

import com.extopy.models.posts.Post
import com.extopy.models.timelines.Timeline
import dev.kaccelero.repositories.Pagination

interface ITimelinesRemoteRepository {

    suspend fun get(id: String): Timeline?
    suspend fun getPosts(id: String, pagination: Pagination): List<Post>

}
