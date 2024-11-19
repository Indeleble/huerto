package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.size(50.dp))
            Text("Usuario: ${user.name}")
            for (terreno in user.terrains!!) {
                Spacer(modifier = Modifier.size(50.dp))
                Button(modifier = Modifier.fillMaxWidth(), onClick = {

                    viewModel.setTerrainToShow(terreno)
                    navController.navigate(AppScreens.SectoresScreen.route)

                }) {

                    Column {
                        Text(terreno.name)
                        Text(terreno.Location)
                    }

                }
                Icon(imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Borrar",
                    modifier = Modifier.clickable {
                        viewModel.deleteTerrain(terreno);force = !force
                    })
            }

            Spacer(modifier = Modifier.size(50.dp))
            Row {
                Button(onClick = {
                    showCreateTerrainsPopup = true
                }) {
                    Text(text = "Crear")
                }
            }
        }
        CreateTerrainDialog(showPopup = showCreateTerrainsPopup,
            onDismissRequest = { showCreateTerrainsPopup = false },
            onConfirmation = { name: String, ubicacion: String ->
                viewModel.createTerrain(name, ubicacion)
                showCreateTerrainsPopup = false;
            })
    }

}




