package com.extopy.presentation.mappers.users

import com.extopy.api.requests.users.UpdateUserRequest
import com.extopy.api.responses.users.FollowerInUserResponse
import com.extopy.api.responses.users.UserResponse
import com.extopy.domain.models.users.FollowerInUser
import com.extopy.domain.models.users.UpdateUserPayload
import com.extopy.domain.models.users.User
import com.extopy.presentation.views.UserView

/**
 * Maps a [User] to a [UserResponse].
 *
 * The password hash has no field to land in, so it cannot leave through here even if a query read
 * it by mistake.
 */
fun User.toUserResponse() = UserResponse(
    id = id,
    displayName = displayName,
    username = username,
    email = email,
    biography = biography,
    avatar = avatar,
    birthdate = birthdate,
    joinDate = joinDate,
    lastActive = lastActive,
    personal = personal,
    verified = verified,
    banned = banned,
    postsCount = postsCount,
    followersCount = followersCount,
    followingCount = followingCount,
    followersIn = followersIn,
    followingIn = followingIn,
)

/**
 * Maps a [FollowerInUser] to a [FollowerInUserResponse].
 */
fun FollowerInUser.toFollowerInUserResponse() = FollowerInUserResponse(
    userId = userId,
    targetId = targetId,
    accepted = accepted,
    user = user?.toUserResponse(),
    target = target?.toUserResponse(),
)

/**
 * Maps an [UpdateUserRequest] to the payload the domain works with.
 */
fun UpdateUserRequest.toUpdateUserPayload() = UpdateUserPayload(
    username = username,
    displayName = displayName,
    password = password,
    biography = biography,
    avatar = avatar,
    personal = personal,
)

/**
 * Maps a [User] to a [UserView].
 */
fun User.toUserView() = UserView(
    displayName = displayName,
    username = username,
)
