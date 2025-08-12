package com.wyllyw.huertoplan.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val username: String, // Username único para login
    val passwordHash: String // Contraseña hasheada
) {
    init {
        require(id.isNotBlank()) { "User ID cannot be blank" }
        require(name.isNotBlank()) { "User name cannot be blank" }
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(passwordHash.isNotBlank()) { "Password hash cannot be blank" }
    }
}
