package com.wyllyw.huertoplan.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sectors",
    foreignKeys = [
        ForeignKey(
            entity = Terrain::class,
            parentColumns = ["id"],
            childColumns = ["terrainId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Sector(
    @PrimaryKey val id: String,
    val name: String,
    val terrainId: String
) {
    init {
        require(id.isNotBlank()) { "Sector ID cannot be blank" }
        require(name.isNotBlank()) { "Sector name cannot be blank" }
        require(terrainId.isNotBlank()) { "Terrain ID cannot be blank" }
    }
}
