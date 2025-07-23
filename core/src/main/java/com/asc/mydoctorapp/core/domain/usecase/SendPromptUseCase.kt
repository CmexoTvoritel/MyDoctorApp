package com.asc.mydoctorapp.core.domain.usecase

import com.asc.mydoctorapp.core.domain.model.ChatMessage
import com.asc.mydoctorapp.core.domain.model.UserToken
import com.asc.mydoctorapp.core.domain.repository.ChatRepository
import javax.inject.Inject

class SendPromptUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(prompt: String): ChatMessage {
        return chatRepository.sendPrompt(prompt)
    }
}
