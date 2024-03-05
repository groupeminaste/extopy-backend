package me.nathanfallet.extopy.usecases.users

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.users.UserContext
import me.nathanfallet.usecases.base.ITripleSuspendUseCase
import me.nathanfallet.usecases.pagination.Pagination

interface IGetUserPostsUseCase : ITripleSuspendUseCase<String, Pagination, UserContext, List<Post>>
