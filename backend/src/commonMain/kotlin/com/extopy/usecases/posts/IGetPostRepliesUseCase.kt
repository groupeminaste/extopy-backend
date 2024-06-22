package com.extopy.usecases.posts

import com.extopy.models.posts.Post
import com.extopy.models.users.UserContext
import dev.kaccelero.models.UUID
import dev.kaccelero.repositories.Pagination
import dev.kaccelero.usecases.ITripleSuspendUseCase

interface IGetPostRepliesUseCase : ITripleSuspendUseCase<UUID, Pagination, UserContext, List<Post>>
