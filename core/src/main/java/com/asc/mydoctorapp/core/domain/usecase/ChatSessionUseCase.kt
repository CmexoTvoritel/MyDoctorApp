package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.database.dao.ChatSessionDao
import com.asc.mydoctorapp.core.database.entity.ChatSessionEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ChatSessionUseCase @Inject constructor(
    private val chatSessionDao: ChatSessionDao
) {
    
    companion object {
        const val MAX_SESSIONS_PER_DAY = 2
    }
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    suspend fun getRemainingSessionsForToday(userEmail: String): Int {
        val today = LocalDate.now().format(dateFormatter)
        val session = chatSessionDao.getSessionsByUserAndDate(userEmail, today)
        
        return if (session != null) {
            (session.maxSessions - session.sessionsUsed).coerceAtLeast(0)
        } else {
            MAX_SESSIONS_PER_DAY
        }
    }
    
    suspend fun startNewSession(userEmail: String): Boolean {
        val today = LocalDate.now().format(dateFormatter)
        val sessionId = "${userEmail}_$today"
        val existingSession = chatSessionDao.getSessionsByUserAndDate(userEmail, today)
        
        return if (existingSession != null) {
            // Проверяем, есть ли доступные сессии
            if (existingSession.sessionsUsed < existingSession.maxSessions) {
                val updatedSession = existingSession.copy(
                    sessionsUsed = existingSession.sessionsUsed + 1
                )
                chatSessionDao.updateSession(updatedSession)
                true
            } else {
                false // Превышен лимит сессий
            }
        } else {
            // Создаем новую запись для текущего дня
            val newSession = ChatSessionEntity(
                id = sessionId,
                userEmail = userEmail,
                sessionDate = today,
                sessionsUsed = 1,
                maxSessions = MAX_SESSIONS_PER_DAY
            )
            chatSessionDao.insertSession(newSession)
            true
        }
    }
    
    suspend fun updateUserEmail(oldEmail: String, newEmail: String) {
        chatSessionDao.updateUserEmail(oldEmail, newEmail)
    }
}
