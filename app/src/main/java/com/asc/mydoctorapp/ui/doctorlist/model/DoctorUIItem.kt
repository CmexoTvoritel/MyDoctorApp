package com.asc.mydoctorapp.ui.doctorlist.model

data class DoctorUIItem(
    val id: String,
    val name: String,
    val surname: String,
    val specialty: String,
    val rating: Float,
    val photoRes: Int?,         // drawable или null = заглушка
    val isFavorite: Boolean
)