package com.asc.mydoctorapp.ui.chat.viewmodel

import java.util.UUID

enum class ScreenState {
    WELCOME,
    CHAT
}

data class ChatUIState(
    val screenState: ScreenState = ScreenState.WELCOME,
    val messages: List<ChatMessage> = emptyList(),  // история
    val inputText: String = "",
    val aiReplyCount: Int = 0,                      // сколько ответов ИИ отправлено
    val remainingSessions: Int = 2,                 // оставшиеся сессии на сегодня
    val isSessionLimitReached: Boolean = false      // превышен ли лимит сессий
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val author: Author,
    val text: String,
    val isLoading: Boolean = false
)

enum class Author { USER, AI }

sealed interface ChatEvent {
    data object OnStartClick : ChatEvent
    data class OnInputChanged(val text: String) : ChatEvent      // ввод в TextField
    object OnSendClick : ChatEvent                               // тап по «отправить»
    object OnAttachClick : ChatEvent                             // плюсик слева
    object OnBookDoctorClick : ChatEvent                         // «Записаться к врачу»
}

sealed interface ChatAction {
    data class NavigateToBooking(val route: String) : ChatAction
}
