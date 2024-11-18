package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wyllyw.huertoplan.model.User
import com.wyllyw.huertoplan.navigation.AppScreens
import com.wyllyw.huertoplan.viewmodel.UserViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SectoresScreen(navController: NavController, viewModel: UserViewModel) {

    Scaffold(
        topBar = {
            BarraSuperior(navController, "Sectores", true)
        },
    ) {
        SecondBodyContent(navController = navController, viewModel)
    }

}

@Composable
fun SecondBodyContent(navController: NavController, viewModel: UserViewModel) {

    val user: User by viewModel.user.collectAsStateWithLifecycle()
    var force by remember { mutableStateOf(true) }

    key(force) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Spacer(modifier = Modifier.size(100.dp))
            Text("Usuario: ${user.name}")
            for (sector in viewModel.getTerrainToShow().sectors) {
                Spacer(modifier = Modifier.size(50.dp))
                Button(onClick = {

                    viewModel.setSectorToShow(sector)
                    //navController.navigate(AppScreens.BancalesScreen.route)

                    //viewModel.deleteTerrain(terreno)
                    //force = !force
                } ) {
                    Column {
                        Text(sector.name)
                        Text("Bancales: " + sector.bancales.size)
                    }
                }
            }
        }
    }
}