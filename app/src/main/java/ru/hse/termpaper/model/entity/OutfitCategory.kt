package ru.hse.termpaper.model.entity

import java.io.Serializable

data class OutfitCategory(
    var id: String = "",
    var userId: String = "",
    val title: String = ""
) : Serializable