package com.extopy.domain.usecases.users

import com.extopy.domain.models.application.Pagination
import com.extopy.domain.models.posts.Post
import kotlin.uuid.Uuid

/**
 * Lists the posts of an account, newest first.
 */
interface GetUserPostsUseCase {

    suspend operator fun invoke(userId: Uuid, pagination: Pagination, viewedBy: Uuid): List<Post>

}
