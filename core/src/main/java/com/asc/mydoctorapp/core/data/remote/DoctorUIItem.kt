package com.asc.mydoctorapp.core.data.remote

data class DoctorUIItem(
    val id: String,             // Оригинальный email врача для навигации
    val name: String,
    val surname: String,
    val specialty: String,
    val rating: Float,
    val photoRes: Int?,         // drawable или null = заглушка
    val isFavorite: Boolean,
    val clinic: String = "",    // Название клиники
    val favoriteKey: String = "" // Уникальный ключ для избранного
)