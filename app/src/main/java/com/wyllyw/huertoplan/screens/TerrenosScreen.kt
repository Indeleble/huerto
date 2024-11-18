package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wyllyw.huertoplan.model.User
import com.wyllyw.huertoplan.navigation.AppScreens
import com.wyllyw.huertoplan.screens.popups.CreateTerrainDialog
import com.wyllyw.huertoplan.viewmodel.UserViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TerrenosScreen(navController: NavController, viewModel: UserViewModel) {

    Scaffold(
        topBar = {
            BarraSuperior(navController, "Terrenos", false)
        },
    ) {
        TerrenosBodyContent(navController = navController, viewModel)
    }

}

@Composable
fun TerrenosBodyContent(navController: NavController, viewModel: UserViewModel) {

    val user: User by viewModel.user.collectAsStateWithLifecycle()
    var force by remember { mutableStateOf(true) }


    var showCreateTerrainsPopup by rememberSaveable { mutableStateOf(false) }

    key(force) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Spacer(modifier = Modifier.size(50.dp))
            Text("Usuario: ${user.name}")
            for (terreno in user.terrains!!) {
                Spacer(modifier = Modifier.size(50.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = {

                    viewModel.setTerrainToShow(terreno)
                    navController.navigate(AppScreens.SectoresScreen.route)

                    //viewModel.deleteTerrain(terreno)
                    //force = !force
                }) {
                    Column {
                        Text(terreno.name)
                        Text(terreno.Location)
                    }
                }
            }

            Spacer(modifier = Modifier.size(50.dp))
            Row {
                Button(onClick = {
                    showCreateTerrainsPopup = true;
                //viewModel.createTerrain(tName, tUb)
                     }) {
                    Text(text = "Crear")

                }


            }
        }
        CreateTerrainDialog(showPopup = showCreateTerrainsPopup, onDismissRequest = { showCreateTerrainsPopup = false }, viewModel, onConfirmation = { name: String, ubicacion: String -> viewModel.createTerrain(name, ubicacion); showCreateTerrainsPopup=false; })

    }

}




