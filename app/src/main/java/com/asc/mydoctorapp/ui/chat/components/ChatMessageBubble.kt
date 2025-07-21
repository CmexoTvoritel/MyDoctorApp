package com.asc.mydoctorapp.ui.chat.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asc.mydoctorapp.ui.chat.viewmodel.Author
import com.asc.mydoctorapp.ui.chat.viewmodel.ChatMessage

private val TealColor = Color(0x3343B3AE)

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val alignment = if (message.author == Author.AI) Alignment.Start else Alignment.End
    val contentAlignment = if (message.author == Author.AI) Alignment.TopStart else Alignment.TopEnd
    
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .align(alignment)
                .fillMaxWidth(),
            contentAlignment = contentAlignment
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
        modifier = Modifier.widthIn(max = 250.dp),
        color = Color(0xFFF2F2F2),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp
            ),
            color = Color.Black,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun UserBubble(text: String) {
    Surface(
        modifier = Modifier.widthIn(max = 250.dp),
        color = TealColor,
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp
            ),
            color = Color.Black,
            modifier = Modifier.padding(12.dp)
        )
    }
}
