package com.extopy.usecases.users

import com.extopy.models.posts.Post
import com.extopy.models.users.UserContext
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination
import dev.kaccelero.usecases.ITripleSuspendUseCase

interface IGetUserPostsUseCase : ITripleSuspendUseCase<UUID, Pagination, UserContext, List<Post>>
