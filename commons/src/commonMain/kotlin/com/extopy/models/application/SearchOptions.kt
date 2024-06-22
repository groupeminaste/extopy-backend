package com.extopy.models.application

import dev.kaccelero.repositories.IPaginationOptions
import kotlinx.serialization.Serializable

@Serializable
data class SearchOptions(
    val search: String,
) : IPaginationOptions
