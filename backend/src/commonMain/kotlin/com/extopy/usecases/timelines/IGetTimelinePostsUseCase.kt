package com.extopy.usecases.timelines

import com.extopy.models.posts.Post
import com.extopy.models.users.UserContext
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination
import dev.kaccelero.usecases.ITripleSuspendUseCase

interface IGetTimelinePostsUseCase : ITripleSuspendUseCase<UUID, Pagination, UserContext, List<Post>>
