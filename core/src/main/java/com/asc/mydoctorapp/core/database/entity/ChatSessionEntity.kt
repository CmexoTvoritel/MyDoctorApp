package com.asc.mydoctorapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey
    val id: String,
    val userEmail: String,
    val sessionDate: String,
    val sessionsUsed: Int = 0,
    val maxSessions: Int = 2
)
