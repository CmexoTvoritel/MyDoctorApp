package com.asc.mydoctorapp.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.ui.home.components.DoctorCard
import com.asc.mydoctorapp.ui.home.viewmodel.HomeViewModel
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeAction
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeEvent

// Основные цвета
private val TealColor = Color(0xFF43B3AE)

@Composable
fun HomeScreen(
    navigateTo: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.viewStates().collectAsState()
    val scrollState = rememberScrollState()
    
    // Отслеживаем действия для навигации
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is HomeAction.NavigateToChat -> navigateTo(action.route)
                is HomeAction.NavigateToDoctorProfile -> navigateTo("doctor/${action.doctorId}")
                is HomeAction.NavigateToSpecialistsList -> navigateTo(action.route)
                is HomeAction.NavigateToFaq -> navigateTo(action.route)
                else -> {}
            }
        }
    }
    
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Логотип "Мой Доктор"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(
                            color = TealColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )) {
                            append("Мой")
                        }
                        append(" ")
                        withStyle(style = SpanStyle(
                            color = Color.Black,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )) {
                            append("Доктор")
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Заголовок для поиска
            Text(
                text = "Поиск клиники или специалиста",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.85f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Поле поиска
            OutlinedTextField(
                value = state?.query ?: "",
                onValueChange = { viewModel.obtainEvent(HomeEvent.OnQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = { Text(" ") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealColor,
                    unfocusedBorderColor = Color.Gray
                ),
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.obtainEvent(HomeEvent.OnSearchSubmit) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Поиск",
                            tint = Color.Black.copy(alpha = 0.75f)
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Карточка AI-чата
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .border(
                        width = 1.dp,
                        color = TealColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Проверьте симптомы\nв чате с искусственным\nинтеллектом",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.87f)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = { viewModel.obtainEvent(HomeEvent.OnAiChatStartClick) },
                        modifier = Modifier
                            .width(90.dp)
                            .height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = TealColor
                        )
                    ) {
                        Text(
                            text = "Начать",
                            color = TealColor
                        )
                    }
                }
                
                // Иллюстрация девушки с телефоном
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.ai_chat_girl),
                    contentDescription = "AI Chat Girl",
                    modifier = Modifier
                        .size(width = 120.dp, height = 120.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Заголовок секции специалистов
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Специалисты для Вас",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                
                IconButton(
                    onClick = { viewModel.obtainEvent(HomeEvent.OnSeeAllSpecialistsClick) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = "Смотреть всех специалистов",
                        tint = TealColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Горизонтальный список докторов
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state?.doctors ?: emptyList()) { doctor ->
                    DoctorCard(
                        doctor = doctor,
                        isFavorite = state?.favorites?.contains(doctor.id) ?: false,
                        onDoctorClick = { doctorId ->
                            viewModel.obtainEvent(HomeEvent.OnDoctorCardClick(doctorId))
                        },
                        onFavoriteToggle = { doctorId, isFavorite ->
                            viewModel.obtainEvent(HomeEvent.OnDoctorFavoriteToggle(doctorId, isFavorite))
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Кнопка FAQ внизу
            OutlinedButton(
                onClick = { viewModel.obtainEvent(HomeEvent.OnFaqClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = TealColor
                ),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Остались вопросы?",
                        color = Color.Black.copy(alpha = 0.87f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "FAQ",
                        tint = TealColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
