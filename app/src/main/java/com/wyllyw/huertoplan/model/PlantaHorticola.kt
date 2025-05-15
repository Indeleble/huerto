package com.wyllyw.huertoplan.model

data class PlantaHorticola(
    var id: String,
    val nombreComun: String,
    val variedad: String?,
    val familiaId: Int,
    val nombreCientifico: String,
    val tiempoRecoleccion: Int
) 