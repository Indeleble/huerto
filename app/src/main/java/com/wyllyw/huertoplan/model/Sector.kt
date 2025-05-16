package com.wyllyw.huertoplan.model

data class Sector(
    var id: String = "",
    var name: String = "",
    var terrainId: String = "",
    var bancalesIds: List<String> = listOf()
)
