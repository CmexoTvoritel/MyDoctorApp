package com.asc.mydoctorapp.core.domain.repository

import com.asc.mydoctorapp.core.domain.model.ChatMessage

interface ChatRepository {
    suspend fun sendPrompt(prompt: String): ChatMessage
}
