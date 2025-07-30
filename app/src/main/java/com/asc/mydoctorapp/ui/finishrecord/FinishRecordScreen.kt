package com.asc.mydoctorapp.ui.finishrecord

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.ui.finishrecord.model.FinishRecordAction
import com.asc.mydoctorapp.ui.finishrecord.model.FinishRecordEvent
import com.asc.mydoctorapp.ui.finishrecord.viewmodel.FinishRecordViewModel
import java.net.URLDecoder
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun FinishRecordScreen(
    appointmentInfo: String,
    clinicName: String, 
    clinicAddress: String,
    date: LocalDate,
    time: LocalTime,
    onNavigateToMain: () -> Unit,
) {
    val viewModel: FinishRecordViewModel = hiltViewModel()
    val state by viewModel.viewStates().collectAsState()
    
    LaunchedEffect(appointmentInfo, clinicName, clinicAddress, date, time) {
        viewModel.setAppointmentDetails(
            appointmentInfo = URLDecoder.decode(appointmentInfo, "UTF-8"),
            clinicName = URLDecoder.decode(clinicName, "UTF-8"),
            clinicAddress = URLDecoder.decode(clinicAddress, "UTF-8"),
            date = date,
            time = time
        )
    }
    
    // Наблюдение за действиями
    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is FinishRecordAction.NavigateToMain -> onNavigateToMain()
                else -> {}
            }
        }
    }
    
    // Цвета
    val accentColor = Color(0xFF43B3AE)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Верхний текстовый блок
        Text(
            text = "Ожидайте подтверждения",
            style = TextStyle(
                fontSize = 22.sp
            ),
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "запрос отправлен в клинику:",
            style = TextStyle(
                fontSize = 20.sp
            ),
            fontWeight = FontWeight.Medium,
            color = accentColor,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Список напоминаний
        BulletPoint(text = "Клиника: ${state?.clinicName}", accentColor = accentColor)
        
        Spacer(modifier = Modifier.height(24.dp))

        BulletPoint(text = "Дата: ${state?.getFormattedDateTime()}", accentColor = accentColor)
        
        Spacer(modifier = Modifier.height(24.dp))

        BulletPoint(text = "Адрес: ${state?.address}", accentColor = accentColor)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Иллюстрация
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_success_record),
                contentDescription = "Успешная запись",
                modifier = Modifier.width(250.dp),
                contentScale = ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Кнопка перехода
        Button(
            onClick = { viewModel.obtainEvent(FinishRecordEvent.OnToMainClick) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = accentColor,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "К записям",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.White
                ),
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun BulletPoint(
    text: String,
    accentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(24.dp))
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Текст
        Text(
            text = text,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        )
    }
}