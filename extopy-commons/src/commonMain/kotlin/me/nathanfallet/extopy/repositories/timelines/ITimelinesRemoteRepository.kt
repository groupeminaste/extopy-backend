package me.nathanfallet.extopy.repositories.timelines

import me.nathanfallet.extopy.models.timelines.Timeline

interface ITimelinesRemoteRepository {

    suspend fun get(id: String): Timeline?

}
