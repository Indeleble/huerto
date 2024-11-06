package com.wyllyw.huertoplan.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wyllyw.huertoplan.screens.LoginScreen
import com.wyllyw.huertoplan.screens.SecondScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route) {
        composable(route = AppScreens.LoginScreen.route) { LoginScreen(navController) }
        composable(route = AppScreens.SecondScreen.route) { SecondScreen(navController) }
    }
}

