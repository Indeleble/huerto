package com.wyllyw.huertoplan.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wyllyw.huertoplan.screens.BancalesScreen
import com.wyllyw.huertoplan.screens.LoginScreen
import com.wyllyw.huertoplan.screens.RegisterScreen
import com.wyllyw.huertoplan.viewmodel.UserViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route, enterTransition =  { slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Start, tween(200)) },) {
        composable(route = AppScreens.LoginScreen.route) { LoginScreen(navController) }
        composable(route = AppScreens.RegisterScreen.route) { RegisterScreen(navController) }
        composable(route = AppScreens.BancalesScreen.route) { BancalesScreen(navController, userViewModel) }
    }

}

