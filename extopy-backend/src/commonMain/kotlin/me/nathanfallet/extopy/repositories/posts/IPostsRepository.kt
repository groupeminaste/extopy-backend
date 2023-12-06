package me.nathanfallet.extopy.repositories.posts

import me.nathanfallet.extopy.models.posts.Post
import me.nathanfallet.extopy.models.posts.PostPayload
import me.nathanfallet.usecases.models.repositories.IModelSuspendRepository

interface IPostsRepository : IModelSuspendRepository<Post, String, PostPayload, PostPayload>
