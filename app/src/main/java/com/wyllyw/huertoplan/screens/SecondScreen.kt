package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.wyllyw.huertoplan.navigation.AppNavigation
import com.wyllyw.huertoplan.navigation.AppScreens

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SecondScreen(navController: NavController) {

    Scaffold {
        SecondBodyContent(navController)
    }

}

@Composable
fun SecondBodyContent(navController: NavController) {

    Column {
        Text("Segunda pantalla")
        Button(onClick = { navController.navigate(AppScreens.FirstScreen.route)}) {
            Text("Boton 2")
        }
    }
}