package com.asc.mydoctorapp.ui.doctordetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.doctordetail.components.ReviewCard
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.DoctorDetailViewModel
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.DoctorDetailAction
import com.asc.mydoctorapp.ui.doctordetail.viewmodel.model.DoctorDetailEvent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DoctorDetailScreen(
    onNavigateToScreen: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: DoctorDetailViewModel = hiltViewModel()
    val state by viewModel.viewStates().collectAsState()

    viewModel.viewActions().collectAsState(initial = null).value?.let { action ->
        when (action) {
            is DoctorDetailAction.NavigateBack -> onNavigateBack()
            is DoctorDetailAction.NavigateToBooking -> onNavigateToScreen(AppRoutes.DoctorRecord.route)
            is DoctorDetailAction.NavigateToSupport -> {}
            is DoctorDetailAction.NavigateToReviewDetail -> onNavigateToScreen(AppRoutes.ReviewsList.route)
        }
    }
    
    val accentColor = Color(0xFF43B3AE)

    val titleLargeStyle = TextStyle(
        fontSize = 21.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp
    )
    
    val bodyMediumStyle = TextStyle(
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
    
    val headingStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp
    )
    
    val buttonTextStyle = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        color = Color.White
    )
    
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Фиксированная стрелка назад
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Назад",
            modifier = Modifier
                .padding(start = 24.dp, top = 16.dp, bottom = 16.dp)
                .size(24.dp)
                .clickable { viewModel.obtainEvent(DoctorDetailEvent.OnBackClick) }
                .zIndex(1f)
        )
        
        // Основной контент с прокруткой
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // Шапка с информацией о враче
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Фото врача
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier.size(96.dp)
                ) {
                    state?.doctor?.photoRes?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Информация о враче
                Column {
                    // Имя врача
                    Text(
                        text = state?.doctor?.name ?: "",
                        style = titleLargeStyle
                    )
                    
                    // Рейтинг
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "5,0",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = accentColor
                            )
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = state?.doctor?.specialty ?: "",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state?.doctor?.qualification ?: "",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF268681)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Теги - FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state?.tags?.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, accentColor),
                        color = Color.White
                    ) {
                        Text(
                            text = tag,
                            style = TextStyle(
                                fontSize = 17.sp,
                                color = accentColor
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Отзывы
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                items(state?.reviews ?: emptyList()) { review ->
                    ReviewCard(
                        review = review
                    )
                }
                
                // Кнопка "смотреть все отзывы" - центрированная по вертикали
                item {
                    Box(
                        modifier = Modifier.height(height = 120.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .align(alignment = Alignment.Center)
                                .clip(CircleShape)
                                .background(accentColor)
                                .clickable { viewModel.obtainEvent(DoctorDetailEvent.OnReviewClick("all")) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = "Все отзывы",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Образование
            Text(
                text = "Сведения об образовании",
                style = headingStyle
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Список образований
            state?.education?.forEach { educationItem ->
                Text(
                    text = educationItem,
                    style = bodyMediumStyle
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Место работы
            Text(
                text = "Врач принимает:",
                style = headingStyle
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Информация о клинике (с буллетами)
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = state?.clinicInfo?.name ?: "",
                    style = bodyMediumStyle
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = state?.clinicInfo?.address ?: "",
                    style = bodyMediumStyle
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = state?.clinicInfo?.schedule ?: "",
                    style = bodyMediumStyle
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Карточка поддержки
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, accentColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clickable { viewModel.obtainEvent(DoctorDetailEvent.OnSupportClick) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Остались вопросы?",
                            style = TextStyle(
                                fontSize = 17.sp,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                        )
                        Text(
                            text = "Напишите нам!",
                            style = TextStyle(
                                fontSize = 17.sp,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    }

                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.ic_question_widget),
                        contentDescription = "FAQ",
                        tint = Color.Unspecified
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Кнопка записи
            Button(
                onClick = { viewModel.obtainEvent(DoctorDetailEvent.OnBookClick) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Записаться на приём",
                    style = buttonTextStyle
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}