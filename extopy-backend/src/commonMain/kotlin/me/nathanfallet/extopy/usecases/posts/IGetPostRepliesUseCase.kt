package me.nathanfallet.extopy.usecases.posts

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.usecases.base.IQuadSuspendUseCase

interface IGetPostRepliesUseCase : IQuadSuspendUseCase<String, Long, Long, UserContext, List<Post>>
