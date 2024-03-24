package ru.hse.termpaper.model.entity

import java.io.Serializable

enum class Season : Serializable {
    WINTER {
        override fun toString() = "Зима"
    },
    AUTUMN {
        override fun toString() = "Осень"
    },
    SPRING {
        override fun toString() = "Весна"
    },
    SUMMER {
        override fun toString() = "Лето"
    }
}
