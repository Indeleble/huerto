package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.wyllyw.huertoplan.navigation.AppScreens

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FirstScreen(navController: NavController) {

    Scaffold {
        BodyContent(navController)
    }

}

@Composable
fun BodyContent(navController: NavController) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Primera pantalla")
        Button(onClick = {navController.navigate(AppScreens.SecondScreen.route)}) {
            Text("Boton")
        }
    }
}