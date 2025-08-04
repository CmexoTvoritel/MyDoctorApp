package com.asc.mydoctorapp.ui.records

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.ui.records.components.RecordCard
import com.asc.mydoctorapp.ui.records.viewmodel.RecordsViewModel
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsAction
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsEvent
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsTab
import com.asc.mydoctorapp.ui.records.viewmodel.model.RecordsUIState

private val TealColor = Color(0xFF43B3AE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(
    navigateTo: (String) -> Unit = {},
    viewModel: RecordsViewModel = hiltViewModel()
) {
    val state by viewModel.viewStates().collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    
    // Обработка действий для навигации
    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is RecordsAction.NavigateToSearchDoctor -> navigateTo(action.route)
                is RecordsAction.NavigateToRecordDetails -> {}
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        state?.let { uiState ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = TealColor
                    )
                }
            } else {
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { viewModel.obtainEvent(RecordsEvent.OnRefresh) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TabsRow(
                            selectedTab = uiState.selectedTab,
                            onTabSelected = { tab -> viewModel.obtainEvent(RecordsEvent.OnTabSelected(tab)) }
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp)
                        ) {
                            Column {
                                Box(modifier = Modifier.weight(weight = 1f)) {
                                    RecordsTabContent(
                                        uiState = uiState,
                                        onRecordClick = { recordId ->
                                            viewModel.obtainEvent(RecordsEvent.OnRecordClick(recordId))
                                        },
                                        onFavoriteToggle = { recordId, newValue ->
                                            viewModel.obtainEvent(
                                                RecordsEvent.OnFavoriteToggle(
                                                    recordId,
                                                    newValue
                                                )
                                            )
                                        }
                                    )
                                }
                                if ((uiState.selectedTab == RecordsTab.CURRENT && uiState.current.isNotEmpty()) ||
                                    (uiState.selectedTab == RecordsTab.PAST && uiState.past.isNotEmpty()) ||
                                    (uiState.selectedTab == RecordsTab.CANCELLED && uiState.cancelled.isNotEmpty())) {
                                    BottomButton(
                                        selectedTab = uiState.selectedTab,
                                        onPrimaryButtonClick = { viewModel.obtainEvent(RecordsEvent.OnPrimaryButtonClick) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabsRow(
    selectedTab: RecordsTab,
    onTabSelected: (RecordsTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                height = 2.dp,
                color = TealColor
            )
        },
        divider = {},
        containerColor = Color.White
    ) {
        Tab(
            selected = selectedTab == RecordsTab.CURRENT,
            onClick = { onTabSelected(RecordsTab.CURRENT) },
            text = {
                Text(
                    text = "Текущие",
                    fontWeight = if (selectedTab == RecordsTab.CURRENT) FontWeight.Bold else FontWeight.Normal
                )
            },
            selectedContentColor = TealColor,
            unselectedContentColor = Color.Black.copy(alpha = 0.6f)
        )
        
        Tab(
            selected = selectedTab == RecordsTab.PAST,
            onClick = { onTabSelected(RecordsTab.PAST) },
            text = {
                Text(
                    text = "Прошедшие",
                    fontWeight = if (selectedTab == RecordsTab.PAST) FontWeight.Bold else FontWeight.Normal
                )
            },
            selectedContentColor = TealColor,
            unselectedContentColor = Color.Black.copy(alpha = 0.6f)
        )
        
        Tab(
            selected = selectedTab == RecordsTab.CANCELLED,
            onClick = { onTabSelected(RecordsTab.CANCELLED) },
            text = {
                Text(
                    text = "Отмененные",
                    fontWeight = if (selectedTab == RecordsTab.CANCELLED) FontWeight.Bold else FontWeight.Normal
                )
            },
            selectedContentColor = TealColor,
            unselectedContentColor = Color.Black.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun RecordsTabContent(
    uiState: RecordsUIState,
    onRecordClick: (String) -> Unit,
    onFavoriteToggle: (String, Boolean) -> Unit
) {
    val records = when (uiState.selectedTab) {
        RecordsTab.CURRENT -> uiState.current
        RecordsTab.PAST -> uiState.past
        RecordsTab.CANCELLED -> uiState.cancelled
    }
    val isPastTab = uiState.selectedTab == RecordsTab.PAST
    val isCancelledTab = uiState.selectedTab == RecordsTab.CANCELLED
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        if (records.isEmpty()) {
            // Плейсхолдер пустого списка
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 100.dp), // Добавляем отступы чтобы контент был в центре
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            isPastTab -> "Здесь будут отображаться\nваши прошедшие записи"
                            isCancelledTab -> "Здесь будут отображаться\nваши отмененные записи"
                            else -> "Здесь будут отображаться\nваши будущие записи"
                        },
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center,
                        color = Color.Black.copy(alpha = 0.87f)
                    )
                }
            }
        } else {
            items(records) { record ->
                RecordCard(
                    record = record,
                    isPast = isPastTab,
                    isCancelled = isCancelledTab,
                    onRecordClick = onRecordClick,
                    onFavoriteToggle = onFavoriteToggle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun BottomButton(
    selectedTab: RecordsTab,
    onPrimaryButtonClick: () -> Unit
) {
    val buttonText = when (selectedTab) {
        RecordsTab.CURRENT -> "Найти врача или клинику"
        RecordsTab.PAST -> "Записаться повторно"
        RecordsTab.CANCELLED -> "Записаться повторно"
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Button(
            onClick = onPrimaryButtonClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = TealColor
            )
        ) {
            Text(
                text = buttonText,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                ),
                color = Color.White
            )
        }
    }
}
