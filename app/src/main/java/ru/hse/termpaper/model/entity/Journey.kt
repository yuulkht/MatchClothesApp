package ru.hse.termpaper.model.entity

import java.io.Serializable

data class Journey (
    var id: String = "",
    var userId: String = "",
    val title: String = ""
) : Serializable