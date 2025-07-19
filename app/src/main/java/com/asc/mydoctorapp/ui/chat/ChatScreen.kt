package com.asc.mydoctorapp.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.ui.chat.components.ChatMessageBubble
import com.asc.mydoctorapp.ui.chat.viewmodel.ChatAction
import com.asc.mydoctorapp.ui.chat.viewmodel.ChatEvent
import com.asc.mydoctorapp.ui.chat.viewmodel.ChatViewModel

private val TealColor = Color(0xFF43B3AE)
private val RedColor = Color(0xFFFF5252)

@Composable
fun ChatScreen(
    navigateTo: (String) -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.viewStates().collectAsState()
    
    // Обработка действий для навигации
    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is ChatAction.NavigateToBooking -> navigateTo(action.route)
                else -> {}
            }
        }
    }
    
    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            ChatInputBar(
                inputText = state?.inputText ?: "",
                onInputChanged = { viewModel.obtainEvent(ChatEvent.OnInputChanged(it)) },
                onSendClick = { viewModel.obtainEvent(ChatEvent.OnSendClick) },
                onAttachClick = { viewModel.obtainEvent(ChatEvent.OnAttachClick) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state?.messages?.isEmpty() == true) {
                // Экран приветствия (когда чат пуст)
                WelcomeScreen(
                    onStartClick = { viewModel.obtainEvent(ChatEvent.OnSendClick) }
                )
            } else {
                // Активный чат (есть сообщения)
                ChatHistoryScreen(
                    messages = state?.messages ?: emptyList(),
                    aiReplyCount = state?.aiReplyCount ?: 0,
                    onBookDoctorClick = { viewModel.obtainEvent(ChatEvent.OnBookDoctorClick) }
                )
            }
        }
    }
}

@Composable
private fun WelcomeScreen(
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Заголовок "Здравствуйте!"
        Text(
            text = "Здравствуйте!",
            color = TealColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Основной параграф
        Text(
            text = "Я ваш ИИ-помощник.\n" +
                   "Опишите ваши симптомы.\n" +
                   "Я проанализирую информацию и предложу возможные причины и специалистов, к которым стоит обратиться.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black.copy(alpha = 0.87f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Блок "Важно:"
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(
                    color = RedColor,
                    fontWeight = FontWeight.Medium
                )) {
                    append("Важно:\n")
                }
                withStyle(style = SpanStyle(
                    color = Color.Black.copy(alpha = 0.87f)
                )) {
                    append("Я — вспомогательный инструмент.\n" +
                           "Мои предположения не заменяют консультацию врача.")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Фраза "Моя цель..."
        Text(
            text = "Моя цель — помочь понять, к какому специалисту записаться.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black.copy(alpha = 0.87f)
        )
        
        // Филлер
        Spacer(modifier = Modifier.weight(1f))
        
        // Кнопка "Начать чат"
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealColor)
        ) {
            Text(
                text = "Начать чат",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ChatHistoryScreen(
    messages: List<com.asc.mydoctorapp.ui.chat.viewmodel.ChatMessage>,
    aiReplyCount: Int,
    onBookDoctorClick: () -> Unit
) {
    val listState = rememberLazyListState()
    
    LazyColumn(
        state = listState,
        reverseLayout = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        items(messages) { message ->
            ChatMessageBubble(
                message = message,
                aiReplyCount = aiReplyCount,
                onBookDoctorClick = onBookDoctorClick,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка "+" для прикрепления файлов
            IconButton(
                onClick = onAttachClick,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
            ) {
                Surface(
                    color = TealColor,
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Прикрепить",
                        tint = Color.White,
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
            
            // Поле ввода текста
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChanged,
                placeholder = { 
                    Text(
                        text = "Напишите сообщение",
                        color = Color.Black.copy(alpha = 0.5f)
                    ) 
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealColor,
                    unfocusedBorderColor = TealColor
                ),
                maxLines = 1
            )
            
            // Кнопка отправки сообщения
            IconButton(
                onClick = onSendClick,
                enabled = inputText.isNotBlank(),
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Отправить",
                    tint = if (inputText.isNotBlank()) TealColor else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
