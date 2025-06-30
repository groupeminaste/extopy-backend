package com.extopy.models.application

import dev.kaccelero.repositories.IPaginationOptions
import digital.guimauve.zodable.Zodable
import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@Zodable
@JsExport
@Serializable
data class SearchOptions(
    val search: String,
) : IPaginationOptions
