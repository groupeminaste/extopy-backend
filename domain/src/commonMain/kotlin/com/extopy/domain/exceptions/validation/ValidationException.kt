package com.extopy.domain.exceptions.validation

/**
 * Exception thrown when a field of a payload does not hold up.
 *
 * The key is carried rather than derived from the type, because the message bundle names these per
 * form and per field (`auth_username_regex`, `users_displayName_maxLength`, ...) and clients already
 * read those exact keys.
 *
 * @param key The key of the message describing what is wrong.
 */
class ValidationException(
    val key: String,
) : Exception(key)
