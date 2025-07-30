package com.asc.mydoctorapp.ui.cliniclist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.ui.cliniclist.viewmodel.ClinicListViewModel
import com.asc.mydoctorapp.ui.cliniclist.viewmodel.model.ClinicListAction
import com.asc.mydoctorapp.ui.cliniclist.viewmodel.model.ClinicListEvent
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.model.DoctorEvent
import com.asc.mydoctorapp.ui.home.components.ClinicSearchCard

@Composable
fun ClinicListScreen(
    onClinicClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: ClinicListViewModel = hiltViewModel()
) {
    val state by viewModel.viewStates().collectAsState()

    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is ClinicListAction.NavigateToClinic -> onClinicClick(action.route)
                is ClinicListAction.NavigateBack -> onBackClick()
                is ClinicListAction.ShowError -> {
                    // Handle error display (could use SnackBar or Toast)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        // Header with back button and title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        viewModel.handleEvent(ClinicListEvent.OnBackClick)
                    },
                painter = painterResource(id = R.drawable.ic_back_navigation),
                contentDescription = "Назад",
                tint = Color.Unspecified
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "Список клиник",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF43B3AE)
                    )
                }
                
                state.error != null -> {
                    Text(
                        text = state.error!!,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.clinics) { clinic ->
                            ClinicSearchCard(
                                clinicName = clinic.name ?: "",
                                clinicAddress = clinic.address ?: "",
                                clinicEmail = clinic.email ?: "",
                                clinicPhone = clinic.phone ?: "",
                                clinicWorkingTime = clinic.workingDays ?: "",
                                onClinicClick = { clinicName ->
                                    viewModel.handleEvent(ClinicListEvent.OnClinicClick(clinicName))
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}