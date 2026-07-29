package com.extopy.presentation.views

/**
 * What the templates read, kept apart from the domain models so that renaming a field of the domain
 * does not silently blank out a page.
 *
 * Freemarker reads these by name, so the field names are part of the templates.
 */

/**
 * A user, as the `authorize` page shows it.
 */
data class UserView(
    val displayName: String,
    val username: String,
)

/**
 * An OAuth client, as the `authorize` page shows it.
 */
data class ClientView(
    val id: String,
    val name: String,
    val description: String,
)

/**
 * The pair the `authorize` page asks the user about.
 */
data class ClientForUserView(
    val client: ClientView,
    val user: UserView,
)

/**
 * The address the second step of the sign-up form fills in and cannot be changed.
 */
data class RegisterView(
    val email: String,
)
