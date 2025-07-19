package com.asc.mydoctorapp.ui.chat.viewmodel

import androidx.lifecycle.viewModelScope
import com.diveomedia.little.stories.bedtime.books.kids.core.ui.viewmodel.BaseSharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : BaseSharedViewModel<ChatUIState, ChatAction, ChatEvent>(
    initialState = ChatUIState()
) {

    override fun obtainEvent(viewEvent: ChatEvent) {
        when (viewEvent) {
            is ChatEvent.OnInputChanged -> handleInputChange(viewEvent.text)
            is ChatEvent.OnSendClick -> handleSendClick()
            is ChatEvent.OnAttachClick -> handleAttachClick()
            is ChatEvent.OnBookDoctorClick -> handleBookDoctorClick()
        }
    }
    
    private fun handleInputChange(text: String) {
        updateViewState { state ->
            state.copy(inputText = text)
        }
    }
    
    private fun handleSendClick() {
        val currentText = viewStates().value?.inputText ?: ""
        
        if (viewStates().value?.messages?.isEmpty() == true) {
            // Если чат пустой и нажали "Начать чат" - отправляем пустое сообщение для инициации чата
            addMessage(Author.USER, "")
            simulateAiResponse()
            return
        }
        
        if (currentText.isNotBlank()) {
            // Отправляем сообщение пользователя
            addMessage(Author.USER, currentText)
            
            // Очищаем поле ввода
            updateViewState { state ->
                state.copy(inputText = "")
            }
            
            // Симулируем ответ AI
            simulateAiResponse()
        }
    }
    
    private fun handleAttachClick() {
        // Пока пусто, в будущем здесь будет логика прикрепления файлов
    }
    
    private fun handleBookDoctorClick() {
        sendViewAction(ChatAction.NavigateToBooking("booking"))
    }
    
    private fun addMessage(author: Author, text: String) {
        updateViewState { state ->
            val newMessage = ChatMessage(author = author, text = text)
            state.copy(messages = listOf(newMessage) + state.messages)
        }
    }
    
    private fun simulateAiResponse() {
        viewModelScope.launch {
            // Имитация сетевой задержки
            delay(600)
            
            // Генерируем ответ AI
            val aiReply = generateAiReply()
            addMessage(Author.AI, aiReply)
            
            // Увеличиваем счетчик ответов AI
            updateViewState { state ->
                state.copy(aiReplyCount = state.aiReplyCount + 1)
            }
        }
    }
    
    private fun generateAiReply(): String {
        val replies = listOf(
            "Понимаю ваше беспокойство. Расскажите подробнее о симптомах, которые вы испытываете?",
            "Исходя из описанных симптомов, вам стоит обратиться к терапевту для первичной консультации. Он сможет направить вас к нужному специалисту.",
            "Важно помнить, что самолечение может усугубить состояние. Лучше записаться на консультацию к врачу.",
            "При таких симптомах рекомендую обратиться к неврологу. Хотите узнать больше о том, чего ожидать на приеме?",
            "Это может быть связано с несколькими факторами. Специалист проведет необходимую диагностику и определит причину."
        )
        
        val aiReplyCount = viewStates().value?.aiReplyCount ?: 0
        return if (aiReplyCount < replies.size) {
            replies[aiReplyCount]
        } else {
            replies.random()
        }
    }
}
