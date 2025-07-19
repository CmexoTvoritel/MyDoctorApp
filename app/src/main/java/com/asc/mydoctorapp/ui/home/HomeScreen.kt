package com.asc.mydoctorapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.ui.home.components.DoctorCard
import com.asc.mydoctorapp.ui.home.viewmodel.HomeViewModel
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeAction
import com.asc.mydoctorapp.ui.home.viewmodel.model.HomeEvent

private val TealColor = Color(0xFF43B3AE)

@Composable
fun HomeScreen(
    navigateTo: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.viewStates().collectAsState()
    val scrollState = rememberScrollState()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Image(
            modifier = Modifier.width(width = 150.dp),
            painter = painterResource(id = R.drawable.ic_onboarding_logo),
            contentDescription = null,
            contentScale = ContentScale.FillWidth
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Поиск клиники или специалиста",
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
            ),
            color = Color.Black.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Поиск",
                        tint = Color.Unspecified
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Карточка AI-чата
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .border(
                    width = 1.dp,
                    color = TealColor,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Column(
                modifier = Modifier.weight(1f)
                    .padding(all = 16.dp)
                    .align(alignment = Alignment.CenterVertically)
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = "Проверьте симптомы в чате с искусственным интеллектом",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.Black
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
                        style = TextStyle(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = Color.Black
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.ic_ai_help_main),
                contentDescription = "AI Chat",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .height(height = 140.dp)
                    .align(alignment = Alignment.Bottom)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Заголовок секции специалистов
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Специалисты для Вас",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                ),
                color = Color.Black.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(width = 4.dp))
            Icon(
                modifier = Modifier.size(22.dp)
                    .clickable {
                        viewModel.obtainEvent(HomeEvent.OnSeeAllSpecialistsClick)
                    },
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = "Смотреть всех специалистов",
                tint = TealColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 17.sp,
                    ),
                    color = Color.Black.copy(alpha = 0.7f)
                )
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_question_widget),
                    contentDescription = "FAQ",
                    tint = Color.Unspecified
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
