package me.nathanfallet.extopy.usecases.users

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.usecases.base.IQuadSuspendUseCase

interface IGetUserPostsUseCase : IQuadSuspendUseCase<String, Long, Long, UserContext, List<Post>>
