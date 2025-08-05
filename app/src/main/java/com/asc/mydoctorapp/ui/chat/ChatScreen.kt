package com.asc.mydoctorapp.ui.chat

import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.ui.chat.components.ChatMessageBubble
import com.asc.mydoctorapp.ui.chat.components.WelcomeChatComponent
import com.asc.mydoctorapp.ui.chat.viewmodel.ChatAction
import com.asc.mydoctorapp.ui.chat.viewmodel.ChatEvent
import com.asc.mydoctorapp.ui.chat.viewmodel.ChatViewModel
import com.asc.mydoctorapp.ui.chat.viewmodel.ScreenState

val TealColor = Color(0xFF43B3AE)

@Composable
fun ChatScreen(
    navigateTo: (String) -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.viewStates().collectAsState()

    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is ChatAction.NavigateToBooking -> navigateTo(action.route)
                else -> {}
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(bottom = 0.dp)
    ) {
        when (state?.screenState) {
            ScreenState.WELCOME -> {
                WelcomeChatComponent(
                    remainingSessions = state?.remainingSessions ?: 2,
                    isSessionLimitReached = state?.isSessionLimitReached ?: false,
                    onStartClick = {
                        viewModel.obtainEvent(ChatEvent.OnStartClick)
                    }
                )
            }
            ScreenState.CHAT -> {
                ChatHistoryScreen(
                    messages = state?.messages ?: emptyList(),
                    aiReplyCount = state?.aiReplyCount ?: 0,
                    onBookDoctorClick = { viewModel.obtainEvent(ChatEvent.OnBookDoctorClick) },
                    inputText = state?.inputText ?: "",
                    onInputChanged = { viewModel.obtainEvent(ChatEvent.OnInputChanged(it)) },
                    onSendClick = { viewModel.obtainEvent(ChatEvent.OnSendClick) },
                    onAttachClick = { viewModel.obtainEvent(ChatEvent.OnAttachClick) }
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun ChatHistoryScreen(
    messages: List<com.asc.mydoctorapp.ui.chat.viewmodel.ChatMessage>,
    aiReplyCount: Int,
    onBookDoctorClick: () -> Unit,
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            text = "ИИ консультант",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                color = Color.Black
            )
        )
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(color = Color.Black.copy(alpha = 0.2f))
                .height(height = 1.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    // Используем LazyColumn с reverseLayout = false для правильного отображения
                    // сообщений в хронологическом порядке (новые внизу)
                    val listState = rememberLazyListState()
                    
                    LazyColumn(
                        state = listState,
                        reverseLayout = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(messages.reversed()) { message ->
                            ChatMessageBubble(
                                message = message,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        // Добавляем кнопку записи к врачу после нескольких ответов AI
                        if (aiReplyCount >= 3) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            item {
                                OutlinedButton(
                                    onClick = onBookDoctorClick,
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = TealColor
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        TealColor
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                ) {
                                    Text(
                                        text = "Записаться к врачу",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                    
                    // Автоматическая прокрутка к последнему сообщению при появлении новых сообщений
                    LaunchedEffect(messages.size) {
                        if (messages.isNotEmpty()) {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    }
                }
            }
        }
        ChatInputBar(
            inputText = inputText,
            onInputChanged = onInputChanged,
            onSendClick = onSendClick,
            onAttachClick = onAttachClick
        )
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
