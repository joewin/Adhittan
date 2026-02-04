package com.msis.adhittan.data.model

data class Session(
    val id: String,
    val chantId: String,
    val startDate: Long,
    val durationDays: Int,
    val targetCountPerDay: Int,
    val currentDay: Int,
    val dailyProgress: Map<Int, Int>, // Day -> Count
    val isActive: Boolean
)

data class MenuItem(
    val title: String,
    val filename: String
)

data class Chant(
    val id: String,
    val title: String, // Display title
    val content: String? = null // Loaded lazily
)
