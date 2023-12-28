package me.nathanfallet.extopy.usecases.users

import me.nathanfallet.extopy.models.users.FollowerInUser
import me.nathanfallet.usecases.models.list.slice.IListSliceChildModelSuspendUseCase

interface IListFollowingInUserUseCase : IListSliceChildModelSuspendUseCase<FollowerInUser, String>
