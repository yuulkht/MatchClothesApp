package ru.hse.termpaper.model.entity

import java.io.Serializable

data class CalendarEvent(
    var id: String = "",
    var userId: String = "",
    val date: String = ""
) : Serializable