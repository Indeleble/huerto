package com.wyllyw.huertoplan.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wyllyw.huertoplan.screens.LoginScreen
import com.wyllyw.huertoplan.screens.SectorScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route, enterTransition =  { slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },) {
        composable(route = AppScreens.LoginScreen.route) { LoginScreen(navController) }
        composable(route = AppScreens.SectorScreen.route) { SectorScreen(navController) }
    }

}

