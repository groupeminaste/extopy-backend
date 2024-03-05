package me.nathanfallet.extopy.usecases.timelines

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.usecases.base.ITripleSuspendUseCase
import me.nathanfallet.usecases.pagination.Pagination

interface IGetTimelinePostsUseCase : ITripleSuspendUseCase<String, Pagination, UserContext, List<Post>>
