package com.wyllyw.huertoplan.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wyllyw.huertoplan.screens.SectorScreen
import com.wyllyw.huertoplan.screens.SingUpScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.SingUpScreen.route, enterTransition =  { slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },) {
        composable(route = AppScreens.SingUpScreen.route) { SingUpScreen(navController) }
        composable(route = AppScreens.SectorScreen.route) { SectorScreen(navController, hiltViewModel()) }
    }

}

