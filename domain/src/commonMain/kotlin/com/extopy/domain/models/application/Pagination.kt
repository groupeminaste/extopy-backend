package com.extopy.domain.models.application

/**
 * A window over a list, plus the search the caller narrowed it down with.
 *
 * @param limit How many items to return.
 * @param offset How many items to skip.
 * @param search The terms to match, if the caller sent any.
 */
data class Pagination(
    val limit: Long = DEFAULT_LIMIT,
    val offset: Long = 0,
    val search: String? = null,
) {

    companion object {

        /**
         * The page size the API falls back to when a caller does not ask for one.
         */
        const val DEFAULT_LIMIT = 25L

    }

}
