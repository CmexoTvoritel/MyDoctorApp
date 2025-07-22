package com.asc.mydoctorapp.ui.reviews

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.ui.reviews.model.ReviewUi
import com.asc.mydoctorapp.ui.reviews.model.ReviewsAction
import com.asc.mydoctorapp.ui.reviews.model.ReviewsEvent
import com.asc.mydoctorapp.ui.reviews.viewmodel.ReviewsViewModel
import java.time.format.DateTimeFormatter

@Composable
fun ReviewsScreen(
    isMyReviews: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onNavigateToEditReview: (Long) -> Unit = {},
    viewModel: ReviewsViewModel = hiltViewModel()
) {
    // Наблюдение за действиями
    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is ReviewsAction.NavigateBack -> onNavigateBack()
                is ReviewsAction.NavigateToEditReview -> onNavigateToEditReview(action.id)
                is ReviewsAction.ShowDeleteConfirmation -> {}
                else -> {}
            }
        }
    }
    
    val state by viewModel.viewStates().collectAsState()
    val accentColor = Color(0xFF43B3AE)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 24.dp)
    ) {
        // Верхняя панель с кнопкой назад и заголовком
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            IconButton(
                onClick = { viewModel.obtainEvent(ReviewsEvent.OnBackClick) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Заголовок
        Text(
            text = if (state?.isMyReviews == true) "Мои отзывы" else "Оценки и отзывы",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Список отзывов
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state?.reviews ?: emptyList()) { review ->
                ReviewCard(
                    review = review,
                    accentColor = accentColor,
                    isMyReview = state?.isMyReviews ?: false,
                    onEditClick = { viewModel.obtainEvent(ReviewsEvent.OnReviewEdit(review.id)) },
                    onDeleteClick = { viewModel.obtainEvent(ReviewsEvent.OnReviewDelete(review.id)) }
                )
            }
        }
    }
}

@Composable
fun ReviewCard(
    review: ReviewUi,
    accentColor: Color,
    isMyReview: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, accentColor),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Аватар и имя автора
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Аватар
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    ) {
                        review.avatarRes?.let { avatarRes ->
                            Image(
                                painter = painterResource(id = avatarRes),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    
                    // Имя автора
                    Text(
                        text = review.authorName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp
                        ),
                        fontWeight = FontWeight.Normal
                    )
                }
                
                // Дата, время, звёзды и кнопка меню (для моих отзывов)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isMyReview && review.dateTime != null) {
                        Text(
                            text = review.dateTime.format(
                                DateTimeFormatter.ofPattern("d MMMM, HH:mm")
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 15.sp
                            ),
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                        
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    }
                    
                    // Звёзды рейтинга
                    Row {
                        repeat(review.rating) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    // Кнопка меню (только для моих отзывов)
                    if (isMyReview) {
                        Box {
                            IconButton(
                                onClick = { showMenu = !showMenu },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Меню",
                                    tint = Color.Black
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Редактировать") },
                                    onClick = {
                                        onEditClick()
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Удалить") },
                                    onClick = {
                                        onDeleteClick()
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Текст отзыва
            Text(
                text = review.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}