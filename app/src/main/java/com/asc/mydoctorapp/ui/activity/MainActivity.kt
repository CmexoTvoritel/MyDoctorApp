package com.asc.mydoctorapp.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.asc.mydoctorapp.navigation.AppRoutes
import com.asc.mydoctorapp.navigation.BottomBarItem
import com.asc.mydoctorapp.navigation.MyDoctorNavHost
import com.asc.mydoctorapp.navigation.navigateToBottomBarItem
import com.asc.mydoctorapp.ui.bottombar.CustomBottomNavigationBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Определяем, нужно ли показывать боттомбар
            val shouldShowBottomBar = currentDestination?.route?.let { route ->
                !route.startsWith(AppRoutes.Splash.route) &&
                !route.startsWith(AppRoutes.OnBoarding.route) &&
                !route.startsWith(AppRoutes.Login.route) &&
                !route.startsWith(AppRoutes.Registration.route)
            } ?: false
            
            // Определяем текущий выбранный элемент боттомбара
            val bottomBarItems = BottomBarItem.values().toList()
            val currentSelectedBottomBarItem = when {
                currentDestination == null -> BottomBarItem.Home
                else -> {
                    bottomBarItems.find { item ->
                        currentDestination.hierarchy.any {
                            it.route?.startsWith(item.route.route) ?: false 
                        }
                    } ?: BottomBarItem.Home
                }
            }
            MyDoctorTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (shouldShowBottomBar) {
                            CustomBottomNavigationBar(
                                bottomBarItems = bottomBarItems,
                                selectedBottomBarItem = currentSelectedBottomBarItem,
                                onBottomBarItemClick = { item ->
                                    navController.navigateToBottomBarItem(item)
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    MyDoctorNavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun MyDoctorTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true

            window.navigationBarColor = Color.White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        content = content
    )
}