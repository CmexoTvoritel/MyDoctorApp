package com.asc.mydoctorapp.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeChatComponent(
    onStartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(height = 32.dp))
            Text(
                text = "Здравствуйте!",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color(0xFF43B3AE)
                )
            )
            Spacer(modifier = Modifier.height(height = 16.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.Black)) {
                        append("Я ваш ИИ-помощник. Опишите ваши симптомы. Я проанализирую информацию и предложу возможные причины и специалистов, к которым стоит обратиться.\n\n")
                    }
                    withStyle(SpanStyle(color = Color(0xFFEA4D4D))) {
                        append("Важно:\n")
                    }
                    withStyle(SpanStyle(color = Color.Black)) {
                        append("Я — вспомогательный инструмент. Мои предположения не заменяют консультацию врача.\n\nМоя цель — помочь понять, к какому специалисту записаться.")
                    }
                },
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Spacer(modifier = Modifier.weight(weight = 1f))

            Button(
                onClick = onStartClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = com.asc.mydoctorapp.ui.chat.TealColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Начать чат",
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}