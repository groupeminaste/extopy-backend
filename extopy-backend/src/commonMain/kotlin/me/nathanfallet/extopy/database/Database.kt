package me.nathanfallet.extopy.database

import kotlinx.coroutines.Dispatchers
import me.nathanfallet.extopy.database.application.Clients
import me.nathanfallet.extopy.database.application.CodesInEmails
import me.nathanfallet.extopy.database.notifications.Notifications
import me.nathanfallet.extopy.database.notifications.TokensInNotifications
import me.nathanfallet.extopy.database.posts.LikesInPosts
import me.nathanfallet.extopy.database.posts.Posts
import me.nathanfallet.extopy.database.users.ClientsInUsers
import me.nathanfallet.extopy.database.users.FollowersInUsers
import me.nathanfallet.extopy.database.users.Users
import me.nathanfallet.ktorx.database.IDatabase
import me.nathanfallet.ktorx.database.sessions.Sessions
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class Database(
    protocol: String,
    host: String = "",
    name: String = "",
    user: String = "",
    password: String = "",
) : IDatabase {

    // Connect to database
    private val database: org.jetbrains.exposed.sql.Database = when (protocol) {
        "mysql" -> org.jetbrains.exposed.sql.Database.connect(
            "jdbc:mysql://$host:3306/$name", "com.mysql.cj.jdbc.Driver",
            user, password
        )

        "h2" -> org.jetbrains.exposed.sql.Database.connect(
            "jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1;", "org.h2.Driver"
        )

        else -> throw Exception("Unsupported database protocol: $protocol")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Sessions)
            SchemaUtils.create(Clients)
            SchemaUtils.create(CodesInEmails)
            SchemaUtils.create(Users)
            SchemaUtils.create(ClientsInUsers)
            SchemaUtils.create(FollowersInUsers)
            SchemaUtils.create(Notifications)
            SchemaUtils.create(TokensInNotifications)
            SchemaUtils.create(Posts)
            SchemaUtils.create(LikesInPosts)
        }
    }

    override suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

}

/*
    private suspend fun doExpiration() {
        dbQuery {
            Authorizes.deleteWhere {
                expiration less Clock.System.now().toString()
            }
            Notifications.deleteWhere {
                expiration less Clock.System.now().toString()
            }
            NotificationsTokens.deleteWhere {
                expiration less Clock.System.now().toString()
            }
            Posts.selectAll().where {
                Posts.expiration less Clock.System.now().toString()
            }.forEach {
                Posts.delete(it[Posts.id])
            }
        }
    }
*/
