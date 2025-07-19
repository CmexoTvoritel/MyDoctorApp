package com.asc.mydoctorapp.ui.chat.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.asc.mydoctorapp.ui.chat.viewmodel.Author
import com.asc.mydoctorapp.ui.chat.viewmodel.ChatMessage

private val TealColor = Color(0xFF43B3AE)
private val DarkTealColor = Color(0xFF0F7D7A)

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val alignment = if (message.author == Author.AI) Alignment.Start else Alignment.End
    val maxWidth = 0.75f
    
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .align(alignment)
                .fillMaxWidth(maxWidth),
            contentAlignment = Alignment.TopCenter
        ) {
            when (message.author) {
                Author.AI -> AiBubble(message.text)
                Author.USER -> UserBubble(message.text)
            }
        }
    }
}

@Composable
private fun AiBubble(text: String) {
    Surface(
        color = Color(0xFFF2F2F2),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black.copy(alpha = 0.87f),
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun UserBubble(text: String) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, TealColor)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black.copy(alpha = 0.87f),
            modifier = Modifier.padding(12.dp)
        )
    }
}
