package com.wyllyw.huertoplan.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen: AppScreens ("login_screen")
    object SecondScreen: AppScreens("second_screen")
}