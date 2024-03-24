package ru.hse.termpaper.model.entity

import java.io.Serializable

class Cloth (
    var id: String = "",
    var user_id: String = "",
    val title: String = "",
    var photo: String = "",
    val information: String = "",
    // убрать сезон
    val season: Season = Season.SUMMER
) : Serializable {}