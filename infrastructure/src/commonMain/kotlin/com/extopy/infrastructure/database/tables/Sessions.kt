package com.extopy.infrastructure.database.tables

import org.jetbrains.exposed.v1.core.Table

object Sessions : Table() {

    val id = varchar("id", 255)
    val value = text("value")

    override val primaryKey = PrimaryKey(id)

}
