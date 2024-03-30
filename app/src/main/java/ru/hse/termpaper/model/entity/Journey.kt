package ru.hse.termpaper.model.entity

import java.io.Serializable

data class Journey (
    var id: String = "",
    var user_id: String = "",
    val title: String = ""
) : Serializable