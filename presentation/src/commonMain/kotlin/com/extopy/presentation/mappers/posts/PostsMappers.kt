package com.extopy.presentation.mappers.posts

import com.extopy.api.requests.posts.PostRequest
import com.extopy.api.responses.posts.LikeInPostResponse
import com.extopy.api.responses.posts.PostResponse
import com.extopy.domain.models.posts.LikeInPost
import com.extopy.domain.models.posts.Post
import com.extopy.domain.models.posts.PostPayload
import com.extopy.presentation.mappers.users.toUserResponse

/**
 * Maps a [Post] to a [PostResponse].
 */
fun Post.toPostResponse(): PostResponse = PostResponse(
    id = id,
    userId = userId,
    user = user?.toUserResponse(),
    repliedToId = repliedToId,
    repostOfId = repostOfId,
    body = body,
    publishedAt = publishedAt,
    editedAt = editedAt,
    expiresAt = expiresAt,
    visibility = visibility,
    likesCount = likesCount,
    repliesCount = repliesCount,
    repostsCount = repostsCount,
    likesIn = likesIn,
)

/**
 * Maps a [LikeInPost] to a [LikeInPostResponse].
 */
fun LikeInPost.toLikeInPostResponse() = LikeInPostResponse(
    postId = postId,
    userId = userId,
    post = post?.toPostResponse(),
    user = user?.toUserResponse(),
)

/**
 * Maps a [PostRequest] to the payload the domain works with.
 */
fun PostRequest.toPostPayload() = PostPayload(
    body = body,
    repliedToId = repliedToId,
    repostOfId = repostOfId,
)
