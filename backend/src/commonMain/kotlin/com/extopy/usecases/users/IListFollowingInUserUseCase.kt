package com.extopy.usecases.users

import com.extopy.models.users.FollowerInUser
import dev.kaccelero.commons.repositories.IListSliceChildModelSuspendUseCase
import dev.kaccelero.models.UUID

interface IListFollowingInUserUseCase : IListSliceChildModelSuspendUseCase<FollowerInUser, UUID>
