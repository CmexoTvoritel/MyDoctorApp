package com.asc.mydoctorapp.ui.doctorlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.ui.doctorlist.components.DoctorItemCard
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.DoctorListViewModel
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.model.DoctorAction
import com.asc.mydoctorapp.ui.doctorlist.viewmodel.model.DoctorEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorListScreen(
    clinicName: String,
    onNavigateToScreen: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: DoctorListViewModel = hiltViewModel()
) {
    val state by viewModel.viewStates().collectAsState()

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(DoctorEvent.InitLoad(clinicName))
    }

    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is DoctorAction.NavigateToDoctorDetails -> {
                    val route = AppRoutes.DoctorDetails.route.replace("{doctorEmail}", action.doctorEmail).replace("{clinicName}", clinicName)
                    onNavigateToScreen(route)
                }
                DoctorAction.NavigateBack -> {
                    onNavigateBack()
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
        // Fixed back button
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
                        viewModel.obtainEvent(DoctorEvent.OnBackClick)
                    },
                painter = painterResource(id = R.drawable.ic_back_navigation),
                contentDescription = "Назад",
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content with pull-to-refresh
        PullToRefreshBox(
            isRefreshing = state?.isRefreshing ?: false,
            onRefresh = { viewModel.obtainEvent(DoctorEvent.OnRefresh) },
            modifier = Modifier.fillMaxSize()
        ) {
            if (state?.isLoading == true) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF43B3AE))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    // Clinic info card
                    state?.clinic?.let { clinic ->
                        item {
                            ClinicInfoCard(
                                clinicName = clinic.name ?: "",
                                clinicAddress = clinic.address ?: "",
                                clinicEmail = clinic.email ?: "",
                                clinicPhone = clinic.phone ?: "",
                                clinicWorkingDays = clinic.workingDays ?: ""
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Список врачей клиники:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    
                    // Doctors list
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
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ClinicInfoCard(
    clinicName: String,
    clinicAddress: String,
    clinicEmail: String,
    clinicPhone: String,
    clinicWorkingDays: String
) {
    Column {
        // Main clinic info row with image and name/address
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clinic image placeholder
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE0E0E0),
                modifier = Modifier.size(120.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_clinic),
                    contentDescription = "Клиника",
                    contentScale = ContentScale.FillHeight
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = clinicName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = clinicAddress,
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Contact info
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (clinicEmail.isNotEmpty()) {
                ContactInfoRow(
                    label = "Почта:",
                    value = clinicEmail
                )
            }
            
            if (clinicPhone.isNotEmpty()) {
                ContactInfoRow(
                    label = "Номер:",
                    value = clinicPhone
                )
            }
            
            if (clinicWorkingDays.isNotEmpty()) {
                ContactInfoRow(
                    label = "Рабочие дни:",
                    value = clinicWorkingDays
                )
            }
        }
    }
}

@Composable
private fun ContactInfoRow(
    label: String,
    value: String
) {
    val annotatedText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        ) {
            append(label)
        }
        append(" ")
        withStyle(
            style = SpanStyle(
                fontSize = 16.sp,
                color = Color.Gray
            )
        ) {
            append(value)
        }
    }
    
    Text(text = annotatedText)
}