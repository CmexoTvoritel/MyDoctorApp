package com.asc.mydoctorapp.ui.chat.viewmodel

import com.asc.mydoctorapp.core.domain.usecase.SendPromptUseCase
import com.asc.mydoctorapp.core.domain.usecase.ChatSessionUseCase
import com.asc.mydoctorapp.core.domain.usecase.UserInfoUseCase
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendPromptUseCase: SendPromptUseCase,
    private val chatSessionUseCase: ChatSessionUseCase,
    private val userInfoUseCase: UserInfoUseCase
) : BaseSharedViewModel<ChatUIState, ChatAction, ChatEvent>(
    initialState = ChatUIState()
) {

    init {
        loadRemainingSessions()
    }

    override fun obtainEvent(viewEvent: ChatEvent) {
        when (viewEvent) {
            is ChatEvent.OnInputChanged -> handleInputChange(viewEvent.text)
            is ChatEvent.OnSendClick -> handleSendClick()
            is ChatEvent.OnAttachClick -> handleAttachClick()
            is ChatEvent.OnBookDoctorClick -> handleBookDoctorClick()
            is ChatEvent.OnStartClick -> handleStartClick()
        }
    }
    
    private fun handleInputChange(text: String) {
        updateViewState { state ->
            state.copy(inputText = text)
        }
    }
    
    private fun handleSendClick() {
        val currentText = viewStates().value?.inputText ?: ""
        
        // Проверяем, что текст не пустой - теперь не отправляем пустые сообщения автоматически
        if (currentText.isNotBlank()) {
            // Отправляем сообщение пользователя
            addMessage(Author.USER, currentText)
            
            // Очищаем поле ввода
            updateViewState { state ->
                state.copy(inputText = "")
            }
            
            // Показываем индикатор загрузки (три точки)
            showLoadingIndicator()
            
            // Отправляем запрос в API
            sendPromptToApi(currentText)
        }
    }
    
    private fun handleAttachClick() {
        // Пока пусто, в будущем здесь будет логика прикрепления файлов
    }
    
    private fun handleBookDoctorClick() {
        sendViewAction(ChatAction.NavigateToBooking("booking"))
    }

    private fun handleStartClick() {
        viewModelScope.launch {
            try {
                val userEmail = userInfoUseCase.getUserEmail()
                val sessionStarted = chatSessionUseCase.startNewSession(userEmail)
                
                if (sessionStarted) {
                    updateViewState { state ->
                        state.copy(screenState = ScreenState.CHAT)
                    }
                    // Обновляем количество оставшихся сессий после начала новой
                    loadRemainingSessions()
                } else {
                    // Превышен лимит сессий - не переключаемся в режим чата
                    updateViewState { state ->
                        state.copy(isSessionLimitReached = true)
                    }
                }
            } catch (e: Exception) {
                // Обработка ошибок
            }
        }
    }
    
    private fun addMessage(author: Author, text: String) {
        updateViewState { state ->
            val newMessage = ChatMessage(author = author, text = text)
            state.copy(messages = listOf(newMessage) + state.messages)
        }
    }
    
    private fun showLoadingIndicator() {
        // Добавляем сообщение с индикатором загрузки
        updateViewState { state ->
            val loadingMessage = ChatMessage(
                author = Author.AI,
                text = ".",
                isLoading = true
            )
            state.copy(messages = listOf(loadingMessage) + state.messages)
        }
        
        // Запускаем анимацию точек
        viewModelScope.launch {
            var dots = 0
            while (viewStates().value?.messages?.firstOrNull()?.isLoading == true) {
                dots = (dots % 3) + 1
                val dotsText = ".".repeat(dots)
                
                updateViewState { state ->
                    val messages = state.messages.toMutableList()
                    if (messages.isNotEmpty() && messages[0].isLoading) {
                        messages[0] = messages[0].copy(text = dotsText)
                    }
                    state.copy(messages = messages)
                }
                delay(300) // Меняем количество точек каждые 300 мс
            }
        }
    }
    
    private fun sendPromptToApi(prompt: String) {
        viewModelScope.launch {
            try {
                // Вызываем реальный API через UseCase
                val response = sendPromptUseCase(prompt)
                removeLoadingIndicator()
                val responseText = response.text
                addMessage(Author.AI, responseText)
                updateViewState { state ->
                    state.copy(aiReplyCount = state.aiReplyCount + 1)
                }
            } catch (e: Exception) {
                // Обработка ошибки
                removeLoadingIndicator()
                addMessage(Author.AI, "Произошла ошибка: ${e.message ?: "неизвестная ошибка"}")
            }
        }
    }

    private fun appendEmptyAiMessage(): Int {
        var idx = 0
        updateViewState { s ->
            val list = listOf(ChatMessage(author = Author.AI, text = "")) + s.messages
            idx = 0
            s.copy(messages = list)
        }
        return idx
    }

    /** Постепенно «набиваем» текст: 20–50 мс между символами. */
    private suspend fun typeWriterEffect(index: Int, full: String) {
        for (i in 1..full.length) {
            val part = full.substring(0, i)

            updateViewState { s ->
                val list = s.messages.toMutableList()
                if (index < list.size && list[index].author == Author.AI) {
                    list[index] = list[index].copy(text = part)
                }
                s.copy(messages = list)
            }

            delay(30)                       // скорость печати
        }
    }
    
    private fun removeLoadingIndicator() {
        updateViewState { state ->
            val messages = state.messages.toMutableList()
            if (messages.isNotEmpty() && messages[0].isLoading) {
                messages.removeAt(0)
            }
            state.copy(messages = messages)
        }
    }
    
    private suspend fun animateTypingResponse(fullText: String) {
        for (i in 1..fullText.length) {
            val partialText = fullText.substring(0, i)
            
            updateViewState { state ->
                val messages = state.messages.toMutableList()
                if (messages.isNotEmpty() && messages[0].author == Author.AI) {
                    messages[0] = messages[0].copy(text = partialText)
                }
                state.copy(messages = messages)
            }
            
            delay(30) // Задержка 30 мс между появлением символов
        }
    }

    private fun loadRemainingSessions() {
        viewModelScope.launch {
            try {
                val userEmail = userInfoUseCase.getUserEmail()
                val remainingSessions = chatSessionUseCase.getRemainingSessionsForToday(userEmail)
                
                updateViewState { state ->
                    state.copy(
                        remainingSessions = remainingSessions,
                        isSessionLimitReached = remainingSessions <= 0
                    )
                }
            } catch (e: Exception) {
                updateViewState { state ->
                    state.copy(
                        remainingSessions = 0,
                        isSessionLimitReached = true
                    )
                }
            }
        }
    }
}
