package ru.hse.termpaper.model.entity

import java.io.Serializable

data class Cloth (
    var id: String = "",
    var user_id: String = "",
    val title: String = "",
    var photo: String = "",
    val information: String = "",
) : Serializable {
}