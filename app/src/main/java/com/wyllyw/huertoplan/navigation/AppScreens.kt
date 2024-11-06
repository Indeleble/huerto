package com.wyllyw.huertoplan.navigation

sealed class AppScreens(val route: String) {
    object LoginScreen: AppScreens ("login_screen")
    object SectorScreen: AppScreens("sector_screen")
}