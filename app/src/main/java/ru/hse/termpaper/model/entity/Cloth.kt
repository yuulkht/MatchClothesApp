package ru.hse.termpaper.model.entity

import java.io.Serializable

enum class Season: Serializable {
    WINTER,
    AUTUMN,
    SPRING,
    SUMMER
}

class Cloth (
    val id: String = "",
    val user_id: String = "",
    val title: String = "",
    val photo: String = "",
    val information: String = "",
    val season: Season = Season.SUMMER
) : Serializable {}