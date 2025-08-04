package com.asc.mydoctorapp.ui.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.profile.viewmodel.ProfileViewModel
import com.asc.mydoctorapp.ui.profile.viewmodel.model.ProfileAction
import com.asc.mydoctorapp.ui.profile.viewmodel.model.ProfileEvent
import com.asc.mydoctorapp.ui.profile.viewmodel.model.ProfileUIState

private val TealColor = Color(0xFF43B3AE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateTo: (String) -> Unit = {},
    logoutNavigate: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = hiltViewModel()
    val state by viewModel.viewStates().collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // This DisposableEffect observes lifecycle events to refresh data when the screen becomes visible
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Refresh user info every time the screen is resumed
                viewModel.refreshUserInfo()
            }
        }
        
        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)
        
        // When the effect leaves the composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is ProfileAction.NavigateToSettings -> {
                    navigateTo(AppRoutes.ProfileSettings.route)
                }
                is ProfileAction.NavigateToFavorites -> {
                    Toast.makeText(
                        context,
                        "Not implemented yet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is ProfileAction.NavigateToReviews -> navigateTo("home/reviewsList/true")
                is ProfileAction.NavigateToMedicalBookView -> {
                    Toast.makeText(
                        context,
                        "Not implemented yet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is ProfileAction.NavigateToMedicalBookCreate -> {
                    Toast.makeText(
                        context,
                        "Not implemented yet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is ProfileAction.NavigateToSupportChat -> {
                    Toast.makeText(
                        context,
                        "Not implemented yet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is ProfileAction.NavigateToAvatarSelection -> {
                    Toast.makeText(
                        context,
                        "Not implemented yet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is ProfileAction.Logout -> {
                    logoutNavigate()
                }
                else -> {}
            }
        }
    }
    
    PullToRefreshBox(
        isRefreshing = state?.isRefreshing ?: false,
        onRefresh = { viewModel.obtainEvent(ProfileEvent.OnRefresh) },
        modifier = Modifier.fillMaxSize()
    ) {
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

                LogoutCard(
                    onClick = { viewModel.obtainEvent(viewEvent = ProfileEvent.OnLogoutClick) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SupportCard(
                    onClick = { viewModel.obtainEvent(ProfileEvent.OnSupportClick) }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
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
        Icon(
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = "Настройки",
            tint = Color.Unspecified,
            modifier = Modifier.size(36.dp)
                .align(alignment = Alignment.TopEnd)
                .clickable { onSettingsClick() }
        )
        // Аватар и имя пользователя
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
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
            
            Spacer(modifier = Modifier.width(width = 8.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = uiState.userName ?: "",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.userLogin ?: "",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.userBirth ?: "",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )
            }
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
                modifier = Modifier.size(28.dp)
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
                painter = painterResource(id = R.drawable.ic_profile_message),
                contentDescription = "Отзывы",
                tint = Color.Unspecified,
                modifier = Modifier.size(28.dp)
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
                painter = painterResource(id = R.drawable.ic_profile_appointment),
                contentDescription = "Медкнижка",
                tint = Color.Unspecified,
                modifier = Modifier.size(28.dp)
            )
        },
        title = "Медкнижка",
        subtitle = if (hasMedicalBook) "Посмотреть" else "Добавить",
        onClick = onClick
    )
}

@Composable
fun LogoutCard(
    onClick: () -> Unit
) {
    ActionCard(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = "Выход",
                tint = Color(0xFF43B3AE),
                modifier = Modifier.size(28.dp)
            )
        },
        title = "Выйти из аккаунта",
        subtitle = "Никакая информация не теряется",
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
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.Black.copy(alpha = 0.7f)
                )
                Text(
                    text = "Напишите нам!",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.Black.copy(alpha = 0.7f)
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
            icon()
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )
            }
        }
    }
}
