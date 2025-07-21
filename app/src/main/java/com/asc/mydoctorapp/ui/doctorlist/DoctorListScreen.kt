package com.asc.mydoctorapp.ui.doctorlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.doctorlist.components.DoctorItemCard
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.DoctorListViewModel
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.model.DoctorAction
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.model.DoctorEvent

@Composable
fun DoctorListScreen(
    onNavigateToScreen: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: DoctorListViewModel = hiltViewModel()
    val state by viewModel.viewStates().collectAsState()

    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is DoctorAction.NavigateToDoctorDetails -> {
                    onNavigateToScreen(AppRoutes.DoctorDetails.route)
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Box(
            modifier = Modifier
                .align(alignment = Alignment.Start)
                .padding(start = 24.dp)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        viewModel.obtainEvent(DoctorEvent.OnBackClick)
                    },
                painter = painterResource(id = R.drawable.ic_back_navigation),
                contentDescription = "Назад",
                tint = Color.Unspecified
            )
        }
        LazyColumn(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
        ) {
            items(state?.doctorList ?: emptyList()) { doctor ->
                DoctorItemCard(
                    doctor = doctor,
                    onCardClick = { viewModel.obtainEvent(DoctorEvent.OnDoctorClick(doctor.id)) },
                    onFavoriteToggle = { 
                        viewModel.obtainEvent(
                            DoctorEvent.OnFavoriteToggle(
                                doctorId = doctor.id,
                                newValue = !doctor.isFavorite
                            )
                        )
                    }
                )
            }
        }
    }
}