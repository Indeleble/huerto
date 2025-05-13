package com.wyllyw.huertoplan.model

data class PlantaHorticola(
    val id: Int,
    val nombreComun: String,
    val variedad: String?,
    val familiaId: Int,
    val nombreCientifico: String,
    val tiempoRecoleccion: Int
) 