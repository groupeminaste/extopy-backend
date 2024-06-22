package com.extopy.usecases.users

import com.extopy.models.users.FollowerInUser
import dev.kaccelero.commons.repositories.IListSliceChildModelSuspendUseCase

interface IListFollowingInUserUseCase : IListSliceChildModelSuspendUseCase<FollowerInUser, String>
