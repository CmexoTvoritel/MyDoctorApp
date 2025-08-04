package com.asc.mydoctorapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_doctors")
data class FavoriteDoctorEntity(
    @PrimaryKey
    val id: String, // Составной ключ: userEmail + favoriteKey для уникальности
    val userEmail: String,
    val doctorEmail: String, // ОРИГИНАЛЬНЫЙ email врача для навигации
    val favoriteKey: String, // Сгенерированный ключ для уникальной идентификации
    val doctorName: String,
    val doctorSurname: String,
    val specialty: String,
    val rating: Float,
    val photoRes: Int?,
    val clinic: String
)
