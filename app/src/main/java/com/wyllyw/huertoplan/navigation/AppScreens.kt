package com.wyllyw.huertoplan.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen: AppScreens ("login_screen")
    object RegisterScreen: AppScreens ("register_screen")
    object BancalesScreen: AppScreens("bancales_screen")
}