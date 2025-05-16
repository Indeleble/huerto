package com.wyllyw.huertoplan.model

data class User(
    var id: String = "",
    var name: String = "",
    var terrainsIds: List<String> = listOf()
)
