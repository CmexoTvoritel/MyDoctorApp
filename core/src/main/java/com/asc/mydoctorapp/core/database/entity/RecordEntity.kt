package com.asc.mydoctorapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey
    val id: String,
    val userEmail: String, // Добавлено для привязки записей к пользователю
    val doctorName: String,
    val specialty: String,
    val time: String,
    val address: String,
    val clinic: String,
    val photoRes: Int?,
    val isFavorite: Boolean,
    val isConfirmed: Boolean,
    val isCancelled: Boolean = false
)
