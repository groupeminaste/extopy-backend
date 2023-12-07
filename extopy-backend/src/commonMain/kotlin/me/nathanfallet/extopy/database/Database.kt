package me.nathanfallet.extopy.database

import kotlinx.coroutines.Dispatchers
import me.nathanfallet.extopy.database.application.CodesInEmails
import me.nathanfallet.extopy.database.notifications.Notifications
import me.nathanfallet.extopy.database.notifications.TokensInNotifications
import me.nathanfallet.extopy.database.posts.LikesInPosts
import me.nathanfallet.extopy.database.posts.Posts
import me.nathanfallet.extopy.database.users.FollowersInUsers
import me.nathanfallet.extopy.database.users.Users
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class Database(
    protocol: String,
    host: String = "",
    name: String = "",
    user: String = "",
    password: String = "",
) {

    private val database: org.jetbrains.exposed.sql.Database

    init {
        // Connect to database
        database = when (protocol) {
            "mysql" -> org.jetbrains.exposed.sql.Database.connect(
                "jdbc:mysql://$host:3306/$name", "com.mysql.cj.jdbc.Driver",
                user, password
            )

            "h2" -> org.jetbrains.exposed.sql.Database.connect(
                "jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1;", "org.h2.Driver"
            )

            else -> throw Exception("Unsupported database protocol: $protocol")
        }

        // Create tables (if needed)
        transaction(database) {
            //SchemaUtils.create(Authorizes)
            //SchemaUtils.create(Clients)
            SchemaUtils.create(CodesInEmails)
            SchemaUtils.create(Notifications)
            SchemaUtils.create(TokensInNotifications)
            SchemaUtils.create(Posts)
            SchemaUtils.create(LikesInPosts)
            SchemaUtils.create(Users)
            SchemaUtils.create(FollowersInUsers)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

}

/*
    private suspend fun doExpiration() {
        dbQuery {
            Authorizes.deleteWhere {
                Op.build { expiration less Clock.System.now().toString() }
            }
            Notifications.deleteWhere {
                Op.build { expiration less Clock.System.now().toString() }
            }
            NotificationsTokens.deleteWhere {
                Op.build { expiration less Clock.System.now().toString() }
            }
            Posts.select {
                Posts.expiration less Clock.System.now().toString()
            }.forEach {
                Posts.delete(it[Posts.id])
            }
        }
    }
*/
