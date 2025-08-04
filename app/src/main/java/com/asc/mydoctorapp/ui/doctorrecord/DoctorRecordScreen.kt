package com.asc.mydoctorapp.ui.doctorrecord

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import com.asc.mydoctorapp.ui.doctorrecord.model.DoctorRecordAction
import com.asc.mydoctorapp.ui.doctorrecord.model.DoctorRecordEvent
import com.asc.mydoctorapp.ui.doctorrecord.model.DoctorRecordUIState
import com.asc.mydoctorapp.ui.doctorrecord.viewmodel.DoctorRecordViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DoctorRecordScreen(
    clinicName: String,
    doctorEmail: String,
    onNavigateBack: () -> Unit = {},
    onNavigateToConfirmation: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val viewModel: DoctorRecordViewModel = hiltViewModel()
    val state by viewModel.viewStates().collectAsState()
    val context = LocalContext.current

    LaunchedEffect(doctorEmail, clinicName) {
        viewModel.obtainEvent(viewEvent = DoctorRecordEvent.LoadDoctor(doctorEmail, clinicName))
    }
    
    viewModel.viewActions().collectAsState(initial = null).value?.let { action ->
        when (action) {
            is DoctorRecordAction.NavigateBack -> onNavigateBack()
            is DoctorRecordAction.NavigateToConfirmation ->
                onNavigateToConfirmation(action.appointmentInfo, action.clinicName, action.clinicAddress)
            is DoctorRecordAction.ShowError -> {
                LaunchedEffect(Unit) {
                    Toast.makeText(
                        context,
                        "Произошла ошибка при записи. Попробуйте позже",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.obtainEvent(DoctorRecordEvent.OnErrorShown)
                }
            }
        }
    }
    
    val accentColor = Color(0xFF43B3AE)
    val disabledColor = Color(0xFFBDBDBD)
    val isUIEnabled = state?.isLoading != true && state?.isLoadingRecords != true
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Кнопка назад
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Назад",
            modifier = Modifier
                .padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                .size(24.dp)
                .clickable(enabled = isUIEnabled) { 
                    if (isUIEnabled) {
                        viewModel.obtainEvent(DoctorRecordEvent.OnBackClick) 
                    }
                }
        )
        
        // Прокручиваемый контент
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // Заголовок
            Text(
                text = "Выберите дату и время приема",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Показываем индикатор загрузки если идет загрузка записей
            if (state?.isLoadingRecords == true) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = accentColor,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Загрузка записей...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // Календарь (показываем только когда записи загружены)
                CalendarView(
                    yearMonth = state?.displayedMonth ?: YearMonth.now(),
                    selectedDate = state?.selectedDate,
                    availableDates = state?.availableDates ?: emptySet(),
                    accentColor = accentColor,
                    isEnabled = isUIEnabled,
                    onPrevMonth = { 
                        if (isUIEnabled) {
                            viewModel.obtainEvent(DoctorRecordEvent.OnPrevMonth) 
                        }
                    },
                    onNextMonth = { 
                        if (isUIEnabled) {
                            viewModel.obtainEvent(DoctorRecordEvent.OnNextMonth) 
                        }
                    },
                    onDateSelected = { 
                        if (isUIEnabled) {
                            viewModel.obtainEvent(DoctorRecordEvent.OnDateSelected(it)) 
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            val timeSlots = state?.availableTimeSlots ?: emptyList()
            val selectedTime = state?.selectedTime
            val isDateBlocked = state?.isDateBlocked ?: false
            
            // Выбор времени (только если дата выбрана и записи загружены)
            if (state?.isLoadingRecords != true) {
                state?.selectedDate?.let { selectedDate ->
                    if (isDateBlocked) {
                        // Показываем сообщение о недоступности записи
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Нет свободного времени записи",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp
                                ),
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else if (timeSlots.isNotEmpty()) {
                        Text(
                            text = "Выберите время",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 20.sp
                            ),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            timeSlots.forEach { time ->
                                val isSelected = selectedTime == time
                                TimeSlotItem(
                                    time = time,
                                    isSelected = isSelected,
                                    accentColor = accentColor,
                                    isEnabled = isUIEnabled,
                                    modifier = Modifier.weight(1f, fill = false),
                                    onClick = { 
                                        if (isUIEnabled) {
                                            viewModel.obtainEvent(DoctorRecordEvent.OnTimeSelected(time)) 
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Кнопка записаться
            Button(
                onClick = {
                    if (state?.canContinue == true && state?.selectedDate != null && state?.selectedTime != null && isUIEnabled) {
                        viewModel.obtainEvent(DoctorRecordEvent.OnContinueClick(
                            state?.selectedDate ?: LocalDate.now(), state?.selectedTime ?: LocalTime.now()
                        ))
                    }
                },
                enabled = (state?.canContinue == true) && isUIEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if ((state?.canContinue == true) && isUIEnabled) accentColor else disabledColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .padding(vertical = 8.dp)
            ) {
                if (state?.isLoading == true) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Записаться",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 20.sp
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CalendarView(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    availableDates: Set<LocalDate>,
    accentColor: Color,
    isEnabled: Boolean,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ru"))
    val todayDate = LocalDate.now()
    val todayHighlightColor = Color(0x8043B3AE) // Полупрозрачный акцентный цвет для текущего дня
    val weekendColor = Color(0xFFE20000) // Красный цвет для выходных
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // Заголовок месяца с кнопками навигации
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Кнопка предыдущий месяц
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(1.dp, accentColor, CircleShape)
                    .clickable(enabled = isEnabled) { onPrevMonth() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Предыдущий месяц",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Название месяца
            Text(
                text = yearMonth.format(monthFormatter).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = accentColor
            )
            
            // Кнопка следующий месяц
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(1.dp, accentColor, CircleShape)
                    .clickable(enabled = isEnabled) { onNextMonth() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Следующий месяц",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Дни недели (заголовки)
        Row(modifier = Modifier.fillMaxWidth()) {
            for (dayOfWeek in DayOfWeek.values()) {
                // Начинаем с понедельника
                val index = (dayOfWeek.ordinal + 1) % 7
                val day = DayOfWeek.of(if (index == 0) 7 else index)
                
                val isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY
                
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                ) {
                    Text(
                        text = day.getDisplayName(TextStyle.SHORT, Locale("ru"))
                            .take(2)
                            .uppercase(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 18.sp
                        ),
                        color = if (isWeekend) weekendColor else Color.Black.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        // Календарная сетка
        val firstDayOfMonth = yearMonth.atDay(1)
        val daysToAdd = (firstDayOfMonth.dayOfWeek.value - 1 + 7) % 7
        val startDate = firstDayOfMonth.minusDays(daysToAdd.toLong())
        
        for (weekIndex in 0 until 6) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayIndex in 0 until 7) {
                    val date = startDate.plusDays((weekIndex * 7 + dayIndex).toLong())
                    val isCurrentMonth = date.month == yearMonth.month
                    val isSelected = date == selectedDate
                    val isToday = date == todayDate
                    // Сегодняшний день считаем доступным всегда
                    val isAvailable = date in availableDates || isToday
                    val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
                    
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.65f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when {
                                    isSelected && isAvailable -> accentColor
                                    isToday && !isSelected -> todayHighlightColor
                                    else -> Color.Transparent
                                }
                            )
                            .clickable(
                                enabled = isEnabled && isCurrentMonth && isAvailable,
                                onClick = { onDateSelected(date) }
                            )
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 18.sp
                            ),
                            color = when {
                                isSelected && isAvailable -> Color.White
                                !isCurrentMonth -> Color.Black.copy(alpha = 0.5f)
                                isWeekend && isAvailable -> weekendColor
                                !isAvailable -> Color.Black.copy(alpha = 0.5f)
                                else -> Color.Black
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeSlotItem(
    time: LocalTime,
    isSelected: Boolean,
    accentColor: Color,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, accentColor),
        modifier = modifier
            .clickable(enabled = isEnabled, onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            // Радио-круг
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    //.border(1.dp, accentColor, CircleShape)
                    .background(if (isSelected) accentColor else Color.Transparent)
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.size(8.dp))
            
            // Время
            Text(
                text = time.format(formatter),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 18.sp
                )
            )
        }
    }
}