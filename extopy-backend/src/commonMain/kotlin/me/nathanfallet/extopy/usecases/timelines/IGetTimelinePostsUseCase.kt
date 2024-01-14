package me.nathanfallet.extopy.usecases.timelines

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.usecases.base.IQuadSuspendUseCase

interface IGetTimelinePostsUseCase : IQuadSuspendUseCase<String, Long, Long, UserContext, List<Post>>
