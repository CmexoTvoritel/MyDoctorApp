package com.asc.mydoctorapp.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.asc.mydoctorapp.ui.splash.viewmodel.SplashViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.asc.mydoctorapp.ui.splash.viewmodel.model.SplashAction

@Composable
fun SplashScreen(onNavigateToStartScreen: (String) -> Unit) {
    val viewModel: SplashViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.viewActions().collect { action ->
            when (action) {
                is SplashAction.Navigate -> { onNavigateToStartScreen(action.route) }
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(color = Color.White)
    ) {
        //TODO:Doctor implement logo in splash screen
    }
}