package com.wyllyw.huertoplan.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wyllyw.huertoplan.screens.BancalesScreen
import com.wyllyw.huertoplan.screens.SectoresScreen
import com.wyllyw.huertoplan.screens.SingUpScreen
import com.wyllyw.huertoplan.screens.TerrenosScreen
import com.wyllyw.huertoplan.viewmodel.UserViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = AppScreens.SingUpScreen.route, enterTransition =  { slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },) {
        composable(route = AppScreens.SingUpScreen.route) { SingUpScreen(navController, userViewModel) }
        composable(route = AppScreens.TerrenosScreen.route) { TerrenosScreen(navController, userViewModel) }
        composable(route = AppScreens.SectoresScreen.route) { SectoresScreen(navController, userViewModel) }
        composable(route = AppScreens.BancalesScreen.route) { BancalesScreen(navController, userViewModel) }
    }

}

