package com.wyllyw.huertoplan.navigation

sealed class AppScreens(val route: String) {
    object SingUpScreen: AppScreens ("signup_screen")
    object TerrenosScreen: AppScreens("terrenos_screen")
    object SectoresScreen: AppScreens("sectores_screen")
}