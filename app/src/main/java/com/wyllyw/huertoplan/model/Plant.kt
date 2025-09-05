package com.wyllyw.huertoplan.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey val id: String,
    val nombre: String,
    val variedad: String,
    val familia: String,
    val diasGerminacion: Int, // Días hasta germinación
    val diasMaduracion: Int   // Días desde germinación hasta cosecha
) {
    init {
        require(id.isNotBlank()) { "Plant ID cannot be blank" }
        require(nombre.isNotBlank()) { "Plant nombre cannot be blank" }
        require(variedad.isNotBlank()) { "Plant variedad cannot be blank" }
        require(familia.isNotBlank()) { "Plant familia cannot be blank" }
        require(diasGerminacion > 0) { "Días germinación must be positive" }
        require(diasMaduracion > 0) { "Días maduración must be positive" }
    }
}