package com.wyllyw.huertoplan.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "bancales",
    foreignKeys = [
        ForeignKey(
            entity = Sector::class,
            parentColumns = ["id"],
            childColumns = ["sectorId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Bancal(
    @PrimaryKey val id: String,
    val name: String,
    val sectorId: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    init {
        require(id.isNotBlank()) { "Bancal ID cannot be blank" }
        require(name.isNotBlank()) { "Bancal name cannot be blank" }
        require(sectorId.isNotBlank()) { "Sector ID cannot be blank" }
        require(width > 0) { "Width must be positive" }
        require(height > 0) { "Height must be positive" }
    }
}