package com.wyllyw.huertoplan.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String
) {
    init {
        require(id.isNotBlank()) { "User ID cannot be blank" }
        require(name.isNotBlank()) { "User name cannot be blank" }
    }
}
