package com.wyllyw.huertoplan.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wyllyw.huertoplan.screens.BancalesScreen
import com.wyllyw.huertoplan.screens.SingUpScreen
import com.wyllyw.huertoplan.presentation.viewmodel.UserViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Crear UserViewModel a nivel de NavHost para compartir entre screens
    val userViewModel: UserViewModel = hiltViewModel()
    
    Log.d("AppNavigation", "UserViewModel created at NavHost level: ${userViewModel.hashCode()}")
    
    NavHost(navController = navController, startDestination = AppScreens.SingUpScreen.route, enterTransition =  { slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },) {
        composable(route = AppScreens.SingUpScreen.route) { 
            Log.d("AppNavigation", "Navigating to SingUpScreen with UserViewModel: ${userViewModel.hashCode()}")
            SingUpScreen(navController, userViewModel) 
        }
        composable(route = AppScreens.BancalesScreen.route) { 
            Log.d("AppNavigation", "Navigating to BancalesScreen with UserViewModel: ${userViewModel.hashCode()}")
            BancalesScreen(navController, userViewModel) 
        }
    }

}

