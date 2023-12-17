package me.nathanfallet.extopy.repositories.timelines

import io.ktor.util.reflect.*
import me.nathanfallet.extopy.client.IExtopyClient
import me.nathanfallet.extopy.models.timelines.Timeline
import me.nathanfallet.ktorx.repositories.api.APIModelRemoteRepository

class TimelinesRemoteRepository(
    client: IExtopyClient,
) : APIModelRemoteRepository<Timeline, String, Unit, Unit>(
    typeInfo<Timeline>(),
    typeInfo<Unit>(),
    typeInfo<Unit>(),
    typeInfo<List<Timeline>>(),
    client,
    prefix = "/api/v1"
), ITimelinesRemoteRepository {

    override suspend fun get(id: String): Timeline? {
        return get(id, null)
    }

}
