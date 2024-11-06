package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.wyllyw.huertoplan.navigation.AppScreens

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SectorScreen(navController: NavController) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = { BarraSuperior(navController) },
    ) {
        SecondBodyContent(navController = navController)
    }

}

@Composable
fun SecondBodyContent(navController: NavController) {

    Column {
        Text("Segunda pantalla")
        Button(onClick = { navController.navigate(AppScreens.LoginScreen.route) }) {
            Text("Boton 2")
        }
    }
}