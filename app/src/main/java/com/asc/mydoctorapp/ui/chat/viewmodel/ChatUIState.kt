package com.asc.mydoctorapp.ui.chat.viewmodel

import java.util.UUID

data class ChatUIState(
    val messages: List<ChatMessage> = emptyList(),  // история
    val inputText: String = "",
    val aiReplyCount: Int = 0                       // сколько ответов ИИ отправлено
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val author: Author,
    val text: String
)

enum class Author { USER, AI }

sealed interface ChatEvent {
    data class OnInputChanged(val text: String) : ChatEvent      // ввод в TextField
    object OnSendClick : ChatEvent                               // тап по «отправить»
    object OnAttachClick : ChatEvent                             // плюсик слева
    object OnBookDoctorClick : ChatEvent                         // «Записаться к врачу»
}

sealed interface ChatAction {
    data class NavigateToBooking(val route: String) : ChatAction
}
