package ru.hse.termpaper.model.entity

import java.io.Serializable

data class Outfit (
    var id: String = "",
    var userId: String = "",
    val title: String = "",
    var photo: String = "",
    val information: String = "",
) : Serializable