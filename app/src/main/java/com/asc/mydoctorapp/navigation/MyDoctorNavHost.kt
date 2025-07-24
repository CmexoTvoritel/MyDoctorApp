package com.asc.mydoctorapp.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import androidx.navigation.navArgument
import com.asc.mydoctorapp.R
import com.asc.mydoctorapp.ui.chat.ChatScreen
import com.asc.mydoctorapp.ui.doctordetail.DoctorDetailScreen
import com.asc.mydoctorapp.ui.doctorlist.DoctorListScreen
import com.asc.mydoctorapp.ui.doctorrecord.DoctorRecordScreen
import com.asc.mydoctorapp.ui.finishrecord.FinishRecordScreen
import com.asc.mydoctorapp.ui.home.HomeScreen
import com.asc.mydoctorapp.ui.login.LoginScreen
import com.asc.mydoctorapp.ui.onboarding.OnboardingScreen
import com.asc.mydoctorapp.ui.profile.ProfileScreen
import com.asc.mydoctorapp.ui.records.RecordsScreen
import com.asc.mydoctorapp.ui.registration.RegistrationScreen
import com.asc.mydoctorapp.ui.reviews.ReviewsScreen
import com.asc.mydoctorapp.ui.splash.SplashScreen
import java.time.LocalDate
import java.time.LocalTime

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
    DoctorList("home/doctorList"),
    DoctorDetails("home/doctorDetails/{doctorEmail}"),
    ReviewsList("home/reviewsList/{isMyReviews}"),
    DoctorRecord("home/doctorRecord/{doctorEmail}"),
    FinishRecord("home/DoctorRecord"),
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
            LoginScreen(
                navigateTo = { route ->
                    navController.navigate(route) {
                        // Clear the back stack when navigating to main flow
                        if (route == AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(route = AppRoutes.Registration.route) {
            RegistrationScreen(
                navigateTo = { route ->
                    navController.navigate(route) {
                        // Clear the back stack when navigating to main flow
                        if (route == AppRoutes.Home.route) {
                            popUpTo(AppRoutes.Registration.route) { inclusive = true }
                        }
                    }
                }
            )
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
            HomeScreen(
                navigateTo = { route ->
                    navController.navigate(route)
                }
            )
        }
        composable(route = AppRoutes.DoctorList.route) {
            DoctorListScreen(
                onNavigateToScreen = { route ->
                    navController.navigate(route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = AppRoutes.DoctorDetails.route,
            arguments = listOf(
                navArgument("doctorEmail") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val doctorEmail = backStackEntry.arguments?.getString("doctorEmail") ?: ""
            DoctorDetailScreen(
                doctorEmail = doctorEmail,
                onNavigateToScreen = { route ->
                    navController.navigate(route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = AppRoutes.DoctorRecord.route,
            arguments = listOf(
                navArgument("doctorEmail") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val doctorEmail = backStackEntry.arguments?.getString("doctorEmail") ?: ""
            DoctorRecordScreen(
                doctorEmail = doctorEmail,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToConfirmation = { date, time ->
                    navController.navigate(AppRoutes.FinishRecord.route)
                }
            )
        }
        composable(route = AppRoutes.FinishRecord.route) {
            FinishRecordScreen(
                date = LocalDate.now(),
                time = LocalTime.now(),
                onNavigateToMain = {
                    navController.navigate(AppRoutes.Home.route)
                }
            )
        }
        composable(
            route = AppRoutes.ReviewsList.route,
            arguments = listOf(
                navArgument("isMyReviews") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val isMyReviews = backStackEntry.arguments?.getBoolean("isMyReviews") ?: false
            ReviewsScreen(
                isMyReviews = isMyReviews,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEditReview = { reviewId ->
                    // В будущем здесь может быть навигация к экрану редактирования отзыва
                }
            )
        }
    }
}

private fun NavGraphBuilder.chatNavigationGraph(navController: NavController) {
    navigation(
        startDestination = AppRoutes.Chat.route,
        route = "chat_graph"
    ) {
        composable(route = AppRoutes.Chat.route) {
            ChatScreen()
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
            RecordsScreen(
                navigateTo = { route ->
                    navController.navigate(route)
                }
            )
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
            ProfileScreen { route ->
                navController.navigate(route)
            }
        }
        composable(route = AppRoutes.ProfileSettings.route) {

        }
    }
}