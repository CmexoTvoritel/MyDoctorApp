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
import com.asc.mydoctorapp.ui.changeuser.ChangeUserScreen
import com.asc.mydoctorapp.ui.chat.ChatScreen
import com.asc.mydoctorapp.ui.cliniclist.ClinicListScreen
import com.asc.mydoctorapp.ui.doctordetail.DoctorDetailScreen
import com.asc.mydoctorapp.ui.doctorlist.DoctorListScreen
import com.asc.mydoctorapp.ui.doctorrecord.DoctorRecordScreen
import com.asc.mydoctorapp.ui.favourites.FavouritesDoctorsScreen
import com.asc.mydoctorapp.ui.finishrecord.FinishRecordScreen
import com.asc.mydoctorapp.ui.home.HomeScreen
import com.asc.mydoctorapp.ui.login.LoginScreen
import com.asc.mydoctorapp.ui.onboarding.OnboardingScreen
import com.asc.mydoctorapp.ui.profile.ProfileScreen
import com.asc.mydoctorapp.ui.records.RecordsScreen
import com.asc.mydoctorapp.ui.registration.RegistrationScreen
import com.asc.mydoctorapp.ui.reviews.ReviewsScreen
import com.asc.mydoctorapp.ui.settings.SettingsScreen
import com.asc.mydoctorapp.ui.splash.SplashScreen
import java.net.URLEncoder
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
    ClinicList("/home/clinicsList"),
    DoctorList("home/doctorList/{clinicName}"),
    DoctorDetails("home/doctorDetails/{doctorEmail}/{clinicName}"),
    ReviewsList("home/reviewsList/{isMyReviews}"),
    DoctorRecord("home/doctorRecord/{doctorEmail}/{clinicName}"),
    FinishRecord("home/finishRecord/{appointmentInfo}/{clinicName}/{clinicAddress}"),
    Chat("chat"),
    ChatDetails("chat/details"),
    Records("records"),
    RecordsDetails("records/details"),
    Profile("profile"),
    ProfileSettings("profile/settings"),
    ProfileEdit("profile/edit"),
    ProfileFavorites("profile/favorites")
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
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            }
        }

        composable(route = AppRoutes.OnBoarding.route) {
            OnboardingScreen { route ->
                navController.navigate(route) {
                    popUpTo(AppRoutes.OnBoarding.route) {
                        inclusive = true
                    }
                }
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

fun NavController.navigateToBottomBarItem(bottomBarItem: BottomBarItem) {
    val navOptions = navOptions {
        popUpTo(bottomBarItem.route.route) {
            inclusive = true
        }
        launchSingleTop = true
        restoreState = false
    }
    navigate(bottomBarItem.route.route, navOptions)
}

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
        composable(route = AppRoutes.ClinicList.route) {
            ClinicListScreen(
                onClinicClick = { route ->
                    navController.navigate(route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = AppRoutes.DoctorList.route,
            arguments = listOf(
                navArgument("clinicName") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val clinicName = backStackEntry.arguments?.getString("clinicName") ?: "Clinic1"
            DoctorListScreen(
                clinicName = clinicName,
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
                ,
                navArgument("clinicName") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val doctorEmail = backStackEntry.arguments?.getString("doctorEmail") ?: ""
            val clinicName = backStackEntry.arguments?.getString("clinicName") ?: "Clinic1"
            DoctorDetailScreen(
                clinicName = clinicName,
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
                ,
                navArgument("clinicName") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val doctorEmail = backStackEntry.arguments?.getString("doctorEmail") ?: ""
            val clinicName = backStackEntry.arguments?.getString("clinicName") ?: "Clinic1"
            DoctorRecordScreen(
                clinicName = clinicName,
                doctorEmail = doctorEmail,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToConfirmation = { appointmentInfo, clinicName, clinicAddress ->
                    val encodedAppointmentInfo = URLEncoder.encode(appointmentInfo, "UTF-8")
                    val encodedClinicName = URLEncoder.encode(clinicName, "UTF-8")
                    val encodedClinicAddress = URLEncoder.encode(clinicAddress, "UTF-8")
                    navController.navigate("home/finishRecord/$encodedAppointmentInfo/$encodedClinicName/$encodedClinicAddress")
                }
            )
        }
        composable(
            route = AppRoutes.FinishRecord.route,
            arguments = listOf(
                navArgument("appointmentInfo") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("clinicName") {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("clinicAddress") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val appointmentInfo = backStackEntry.arguments?.getString("appointmentInfo") ?: ""
            val clinicName = backStackEntry.arguments?.getString("clinicName") ?: ""
            val clinicAddress = backStackEntry.arguments?.getString("clinicAddress") ?: ""
            FinishRecordScreen(
                appointmentInfo = appointmentInfo,
                clinicName = clinicName,
                clinicAddress = clinicAddress,
                date = LocalDate.now(),
                time = LocalTime.now(),
                onNavigateToMain = {
                    navController.navigate(AppRoutes.Records.route)
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
            ProfileScreen(
                navigateTo = { route -> navController.navigate(route) },
                logoutNavigate = {
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            )
        }
        composable(route = AppRoutes.ProfileSettings.route) {
            SettingsScreen(
                onNavigateTo = { route ->
                    navController.navigate(route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = AppRoutes.ProfileEdit.route) {
            ChangeUserScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = AppRoutes.ProfileFavorites.route) {
            FavouritesDoctorsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                navigateTo = { route ->
                    navController.navigate(route)
                }
            )
        }
    }
}