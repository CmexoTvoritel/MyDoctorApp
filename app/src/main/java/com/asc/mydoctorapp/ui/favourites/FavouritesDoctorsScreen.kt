package com.asc.mydoctorapp.ui.favourites

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.ui.doctorlist.components.DoctorItemCard
import com.asc.mydoctorapp.ui.favourites.viewmodel.FavouritesViewModel
import com.asc.mydoctorapp.ui.favourites.viewmodel.model.FavouritesAction
import com.asc.mydoctorapp.ui.favourites.viewmodel.model.FavouritesEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesDoctorsScreen(
    navigateTo: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: FavouritesViewModel = hiltViewModel()
) {
    val state by viewModel.viewStates().collectAsState()

    // Обработка действий для навигации
    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is FavouritesAction.NavigateBack -> onNavigateBack()
                is FavouritesAction.NavigateToDoctorDetails -> {
                    navigateTo("home/doctorDetails/${action.doctorEmail}/${action.clinicName}")
                }
                is FavouritesAction.ShowError -> {
                    // Можно показать Snackbar или Toast
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 24.dp)
    ) {
        
        /* ---------- Header: ←  "Избранные врачи" ---------- */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 32.dp)
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_navigation),
                contentDescription = "Назад",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onNavigateBack() }
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Избранные врачи",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )
        }

        // Основное содержимое
        when {
            state?.isLoading == true -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state?.doctors?.isEmpty() == true -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "У вас пока нет избранных врачей",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                LazyColumn {
                    items(state?.doctors ?: emptyList()) { doctor ->
                        DoctorItemCard(
                            doctor = doctor,
                            onCardClick = {
                                viewModel.obtainEvent(FavouritesEvent.OnDoctorClick(doctor.id))
                            },
                            onFavoriteToggle = {
                                viewModel.obtainEvent(FavouritesEvent.OnFavoriteToggle(doctor.id, !doctor.isFavorite))
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
