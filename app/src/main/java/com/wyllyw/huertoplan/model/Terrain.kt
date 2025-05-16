package com.wyllyw.huertoplan.model

data class Terrain(
    var id: String = "",
    var name: String = "",
    var Location: String = "",
    var userId: String = "",
    var sectorsIds: List<String> = listOf()
)
