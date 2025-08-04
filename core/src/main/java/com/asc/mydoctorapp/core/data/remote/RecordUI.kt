package com.asc.mydoctorapp.core.data.remote

data class RecordUI(
    val id: String,
    val doctorName: String,
    val doctorEmail: String,       // Оригинальный email врача для навигации
    val specialty: String,
    val time: String,          // «15:00»
    val address: String,       // «Вавилова, 15»
    val clinic: String,        // «Клиника "Здоровье"»
    val photoRes: Int?,        // drawable / URL
    val isFavorite: Boolean,
    val isConfirmed: Boolean
)