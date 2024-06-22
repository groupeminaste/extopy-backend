package com.extopy.repositories.timelines

import com.extopy.models.posts.Post
import com.extopy.models.timelines.Timeline
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination

interface ITimelinesRemoteRepository {

    suspend fun get(id: UUID): Timeline?
    suspend fun getPosts(id: UUID, pagination: Pagination): List<Post>

}
