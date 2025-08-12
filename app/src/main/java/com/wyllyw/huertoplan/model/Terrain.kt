package com.wyllyw.huertoplan.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "terrains",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Terrain(
    @PrimaryKey val id: String,
    val name: String,
    val userId: String
) {
    init {
        require(id.isNotBlank()) { "Terrain ID cannot be blank" }
        require(name.isNotBlank()) { "Terrain name cannot be blank" }
        require(userId.isNotBlank()) { "User ID cannot be blank" }
    }
}
