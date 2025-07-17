package com.asc.mydoctorapp.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.ui.login.LoginScreen
import com.asc.mydoctorapp.ui.onboarding.OnboardingScreen
import com.asc.mydoctorapp.ui.registration.RegistrationScreen
import com.asc.mydoctorapp.ui.splash.SplashScreen

enum class BottomBarItem(
    @StringRes val menuName: Int,
    @DrawableRes val icon: Int,
    val route: AppRoutes
) {
    Home(
        menuName = R.string.bottom_bar_home_item,
        icon = R.drawable.ic_bottom_bar_home,
        route = AppRoutes.Home
    ),
    Chat(
        menuName = R.string.bottom_bar_chat_item,
        icon = R.drawable.ic_bottom_bar_chat,
        route = AppRoutes.Chat
    ),
    Records(
        menuName = R.string.bottom_bar_records_item,
        icon = R.drawable.ic_bottom_bar_records,
        route = AppRoutes.Records
    ),
    Profile(
        menuName = R.string.bottom_bar_profile_item,
        icon = R.drawable.ic_bottom_bar_profile,
        route = AppRoutes.Profile
    ),
}

enum class AppRoutes(val route: String) {
    Splash("splash"),
    OnBoarding("onboarding"),
    Login("login"),
    Registration("registration"),
    Home("home"),
    HomeDetails("home/details"),
    Chat("chat"),
    ChatDetails("chat/details"),
    Records("records"),
    RecordsDetails("records/details"),
    Profile("profile"),
    ProfileSettings("profile/settings"),
}

@Composable
fun MyDoctorNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppRoutes.Splash.route,
    ) {
        // Splash Screen
        composable(route = AppRoutes.Splash.route) {
            SplashScreen { route ->
                navController.navigate(route)
            }
        }

        composable(route = AppRoutes.OnBoarding.route) {
            OnboardingScreen() { route ->
                navController.navigate(route)
            }
        }

        composable(route = AppRoutes.Login.route) {
            LoginScreen()
        }

        composable(route = AppRoutes.Registration.route) {
            RegistrationScreen()
        }
        
        // Home Navigation Graph
        homeNavigationGraph(navController)
        
        // Chat Navigation Graph
        chatNavigationGraph(navController)
        
        // Records Navigation Graph
        recordsNavigationGraph(navController)
        
        // Profile Navigation Graph
        profileNavigationGraph(navController)
    }
}

// Расширение для NavController для навигации к элементам боттомбара
fun NavController.navigateToBottomBarItem(bottomBarItem: BottomBarItem) {
    val navOptions = navOptions {
        // При навигации между элементами боттомбара, 
        // поднимаем стартовую точку графа на верх стека
        // для обеспечения правильного поведения кнопки Back
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        // Сохраняем состояние графа при переключении между табами
        launchSingleTop = true
        // Восстанавливаем состояние, если возвращаемся на предыдущий элемент
        restoreState = true
    }
    navigate(bottomBarItem.route.route, navOptions)
}

// Graph для раздела Home
private fun NavGraphBuilder.homeNavigationGraph(navController: NavController) {
    navigation(
        startDestination = AppRoutes.Home.route,
        route = "home_graph"
    ) {
        composable(route = AppRoutes.Home.route) {
            // Содержимое главной страницы
        }
        composable(route = AppRoutes.HomeDetails.route) {
            // Содержимое страницы с деталями
        }
    }
}

// Graph для раздела Chat
private fun NavGraphBuilder.chatNavigationGraph(navController: NavController) {
    navigation(
        startDestination = AppRoutes.Chat.route,
        route = "chat_graph"
    ) {
        composable(route = AppRoutes.Chat.route) {
            // Содержимое страницы чатов
        }
        composable(route = AppRoutes.ChatDetails.route) {
            // Содержимое страницы с деталями чата
        }
    }
}

// Graph для раздела Records
private fun NavGraphBuilder.recordsNavigationGraph(navController: NavController) {
    navigation(
        startDestination = AppRoutes.Records.route,
        route = "records_graph"
    ) {
        composable(route = AppRoutes.Records.route) {
            // Содержимое страницы записей
        }
        composable(route = AppRoutes.RecordsDetails.route) {
            // Содержимое страницы с деталями записи
        }
    }
}

private fun NavGraphBuilder.profileNavigationGraph(navController: NavController) {
    navigation(
        startDestination = AppRoutes.Profile.route,
        route = "profile_graph"
    ) {
        composable(route = AppRoutes.Profile.route) {

        }
        composable(route = AppRoutes.ProfileSettings.route) {

        }
    }
}