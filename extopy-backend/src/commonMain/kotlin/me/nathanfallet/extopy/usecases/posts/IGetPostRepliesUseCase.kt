package me.nathanfallet.extopy.usecases.posts

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.usecases.base.ITripleSuspendUseCase
import me.nathanfallet.usecases.pagination.Pagination

interface IGetPostRepliesUseCase : ITripleSuspendUseCase<String, Pagination, UserContext, List<Post>>
