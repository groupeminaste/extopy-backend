package me.nathanfallet.extopy.plugins

import io.ktor.server.application.*

/*
object NotificationsPlugin {

    /*
     * Generic
     */

    val httpClient = HttpClient(Jetty) { install(ContentNegotiation) { json() } }

    suspend fun createNotification(notification: Notification, args: List<String> = listOf()) {
        val now = Clock.System.now()
        val expiration = now.plus(1, DateTimeUnit.YEAR, TimeZone.currentSystemDefault())
        Database.dbQuery {
            Notifications.insert {
                it[Notifications.id] = notification.id
                it[Notifications.userId] = notification.userId!!
                it[Notifications.type] = notification.type!!
                it[Notifications.body] = notification.body!!
                it[Notifications.contentId] = notification.contentId
                it[Notifications.published] = (notification.published ?: now).toString()
                it[Notifications.expiration] = (notification.expiration ?: expiration).toString()
            }
        }
        CoroutineScope(Job()).launch { sendNotification(notification, args) }
    }

    suspend fun sendNotification(notification: Notification, args: List<String> = listOf()) {
        notification.userId
            ?.let { userId ->
                Database.dbQuery {
                    NotificationsTokens.join(
                        Users,
                        JoinType.LEFT,
                        NotificationsTokens.userId,
                        Users.id
                    )
                        .select { NotificationsTokens.userId eq userId }
                        .map { NotificationToken(it, User(it)) }
                }
            }
            ?.forEach { token ->
                when (token.service) {
                    "apns" -> sendNotificationAPNS(notification, args, token)
                }
            }
    }

    /*
     * APNS
     */

    lateinit var apnsKey: ECPrivateKey
    lateinit var apnsKeyId: String
    lateinit var apnsKeyTeam: String
    private var apnsToken: String? = null
    private var apnsTokenIssuedAt: Instant? = null

    private suspend fun sendNotificationAPNS(
        notification: Notification,
        args: List<String>,
        token: NotificationToken
    ) {
        if (Clock.System.now() >
            (apnsTokenIssuedAt ?: Instant.DISTANT_PAST).plus(20, DateTimeUnit.MINUTE)
        ) {
            apnsToken = null
        }
        val apnsToken =
            apnsToken
                ?: {
                    val generated =
                        JWT.create()
                            .withKeyId(apnsKeyId)
                            .withIssuer(apnsKeyTeam)
                            .withIssuedAt(Date())
                            .sign(Algorithm.ECDSA256(null, apnsKey))
                    apnsToken = generated
                    apnsTokenIssuedAt = Clock.System.now()
                    generated
                }()
        val topic =
            when (token.clientId) {
                "extopy" -> "me.nathanfallet.Extopy"
                "solar" -> "me.nathanfallet.solar"
                else -> return
            }
        httpClient.post("https://api.push.apple.com/3/device/${token.token}") {
            header("authorization", "bearer $apnsToken")
            header("apns-topic", topic)
            contentType(ContentType.Application.Json)
            setBody(
                NotificationAPNS(
                    NotificationAPNSAPS(
                        NotificationAPNSAPSAlert(
                            "notifications_title_${token.clientId}",
                            listOf(
                                token.user?.displayname ?: "",
                                token.user?.username ?: ""
                            ),
                            notification.body,
                            args
                        ),
                        notification.type
                    ),
                    notification.id,
                    notification.contentId
                )
            )
        }
    }

    @Serializable
    data class NotificationAPNS(
        val aps: NotificationAPNSAPS,
        val id: String?,
        val contentId: String?
    )

    @Serializable
    data class NotificationAPNSAPS(
        val alert: NotificationAPNSAPSAlert,
        val category: String?,
        val sound: String = "default"
    )

    @Serializable
    data class NotificationAPNSAPSAlert(
        @SerialName("title-loc-key") val titleLocKey: String?,
        @SerialName("title-loc-args") val titleLocArgs: List<String>?,
        @SerialName("loc-key") val locKey: String?,
        @SerialName("loc-args") val locArgs: List<String>?
    )
}
*/

fun Application.configureNotifications() {
    /*
    // APNS Key
    NotificationsPlugin.apnsKeyId = environment.config.property("apns.kid").getString()
    NotificationsPlugin.apnsKeyTeam = environment.config.property("apns.iss").getString()
    NotificationsPlugin.apnsKey =
        {
            val rawKey = File("AuthKey_${NotificationsPlugin.apnsKeyId}.p8").readText()
            val bytes =
                rawKey.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("\n", "")
                    .toByteArray()
            val keySpec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(bytes))
            KeyFactory.getInstance("EC").generatePrivate(keySpec) as ECPrivateKey
        }()
    */
}
