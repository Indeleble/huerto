package com.wyllyw.huertoplan.navigation

sealed class AppScreens(val route: String) {
    object SingUpScreen: AppScreens ("signup_screen")
    object BancalesScreen: AppScreens("bancales_screen")
}