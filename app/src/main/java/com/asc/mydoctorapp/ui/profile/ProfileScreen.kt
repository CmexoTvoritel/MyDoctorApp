package com.asc.mydoctorapp.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.ui.profile.viewmodel.ProfileViewModel
import com.asc.mydoctorapp.ui.profile.viewmodel.model.ProfileAction
import com.asc.mydoctorapp.ui.profile.viewmodel.model.ProfileEvent
import com.asc.mydoctorapp.ui.profile.viewmodel.model.ProfileUIState

// Основной цвет акцента для элементов
private val TealColor = Color(0xFF43B3AE)

@Composable
fun ProfileScreen(
    navigateTo: (String) -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.viewStates().collectAsState()
    
    // Обработка действий для навигации
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is ProfileAction.NavigateToSettings -> navigateTo("settings")
                is ProfileAction.NavigateToFavorites -> navigateTo("favorites")
                is ProfileAction.NavigateToReviews -> navigateTo("reviews")
                is ProfileAction.NavigateToMedicalBookView -> navigateTo("medical_book_view")
                is ProfileAction.NavigateToMedicalBookCreate -> navigateTo("medical_book_create")
                is ProfileAction.NavigateToSupportChat -> navigateTo("support_chat")
                is ProfileAction.NavigateToAvatarSelection -> navigateTo("avatar_selection")
                else -> {}
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        state?.let { uiState ->
            ProfileHeader(
                uiState = uiState,
                onSettingsClick = { viewModel.obtainEvent(ProfileEvent.OnSettingsClick) },
                onAvatarClick = { viewModel.obtainEvent(ProfileEvent.OnAvatarClick) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Карточки действий
            FavoritesCard(
                favoritesCount = uiState.favoritesCount,
                onClick = { viewModel.obtainEvent(ProfileEvent.OnFavoritesClick) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ReviewsCard(
                reviewsCount = uiState.reviewsCount,
                onClick = { viewModel.obtainEvent(ProfileEvent.OnReviewsClick) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MedicalBookCard(
                hasMedicalBook = uiState.hasMedicalBook,
                onClick = { viewModel.obtainEvent(ProfileEvent.OnMedicalBookClick) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Блок "Остались вопросы? Напишите нам!"
            SupportCard(
                onClick = { viewModel.obtainEvent(ProfileEvent.OnSupportClick) }
            )
            
            // Отступ снизу
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileHeader(
    uiState: ProfileUIState,
    onSettingsClick: () -> Unit,
    onAvatarClick: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)
    ) {
        // Кнопка настроек
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp)
                .clip(CircleShape)
                .clickable { onSettingsClick() }
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Настройки",
                tint = TealColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Аватар и имя пользователя
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Аватар пользователя
            Surface(
                shape = CircleShape,
                color = Color(0xFFE0E0E0),
                modifier = Modifier
                    .size(104.dp)
                    .clickable { onAvatarClick() }
            ) {
                if (uiState.avatarRes != null) {
                    Image(
                        painter = painterResource(id = uiState.avatarRes),
                        contentDescription = "Аватар",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Имя пользователя
            Text(
                text = uiState.userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.87f)
            )
        }
    }
}

@Composable
fun FavoritesCard(
    favoritesCount: Int,
    onClick: () -> Unit
) {
    ActionCard(
        icon = {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Избранное",
                tint = TealColor,
                modifier = Modifier.size(24.dp)
            )
        },
        title = "Избранное",
        subtitle = if (favoritesCount == 0) "Нет добавленных врачей и клиник" else "$favoritesCount врача",
        onClick = onClick
    )
}

@Composable
fun ReviewsCard(
    reviewsCount: Int,
    onClick: () -> Unit
) {
    ActionCard(
        icon = {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Отзывы",
                tint = TealColor,
                modifier = Modifier.size(24.dp)
            )
        },
        title = "Мои отзывы",
        subtitle = if (reviewsCount == 0) "Пока вы не написали отзывы" else "$reviewsCount отзыва",
        onClick = onClick
    )
}

@Composable
fun MedicalBookCard(
    hasMedicalBook: Boolean,
    onClick: () -> Unit
) {
    ActionCard(
        icon = {
            Icon(
                imageVector = Icons.Outlined.List,
                contentDescription = "Медкнижка",
                tint = TealColor,
                modifier = Modifier.size(24.dp)
            )
        },
        title = "Медкнижка",
        subtitle = if (hasMedicalBook) "Посмотреть" else "Добавить",
        onClick = onClick
    )
}

@Composable
fun SupportCard(
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .border(
                width = 1.dp,
                color = TealColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Остались вопросы?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.87f)
                )
                Text(
                    text = "Напишите нам!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.87f)
                )
            }
            
            Icon(
                imageVector = Icons.Outlined.Phone,
                contentDescription = "Поддержка",
                tint = TealColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun ActionCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .border(
                width = 1.dp,
                color = TealColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Иконка
            Box(modifier = Modifier.size(24.dp)) {
                icon()
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Текстовый контент
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.87f)
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.6f)
                )
            }
        }
    }
}
