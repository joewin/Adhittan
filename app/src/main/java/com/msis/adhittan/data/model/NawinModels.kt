package com.msis.adhittan.data.model

enum class BirthDay(val value: Int, val label: String) {
    SUNDAY(1, "Sunday"),
    MONDAY(2, "Monday"),
    TUESDAY(3, "Tuesday"),
    WEDNESDAY(4, "Wednesday"),
    THURSDAY(5, "Thursday"),
    FRIDAY(6, "Friday"),
    SATURDAY(7, "Saturday")
}

data class NawinDay(
    val dayOfWeek: Int, // 1-7
    val mantraCount: Int,
    val offering: String,
    val colorHex: String,
    val attributeName: String
)
