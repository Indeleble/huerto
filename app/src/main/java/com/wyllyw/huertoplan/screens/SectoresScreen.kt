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
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.User
import com.wyllyw.huertoplan.navigation.AppScreens
import com.wyllyw.huertoplan.screens.popups.CreateSectorDialog
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
    var showCreateSectorPopup by rememberSaveable { mutableStateOf(false) }


    key(force) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.size(100.dp))
            Text("Usuario: ${user.name}")
            for (sector in viewModel.getTerrainToShow().sectors) {
                Spacer(modifier = Modifier.size(50.dp))
                Button(onClick = {

                    viewModel.setSectorToShow(sector)
                    navController.navigate(AppScreens.BancalesScreen.route)

                }) {
                    Column {
                        Text(sector.name)
                        Text("Bancales: " + sector.bancales.size)
                    }
                    Icon(imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Borrar",
                        modifier = Modifier.clickable {
                            viewModel.deleteSector(viewModel.getTerrainToShow(), sector);force =
                            !force
                        })
                }
            }

            Spacer(modifier = Modifier.size(50.dp))
            Row {
                Button(onClick = {
                    showCreateSectorPopup = true
                }) {
                    Text(text = "Crear")
                }
            }

        }

        CreateSectorDialog(terrain = viewModel.getTerrainToShow(),
            showPopup = showCreateSectorPopup,
            onDismissRequest = { showCreateSectorPopup = false },
            onConfirmation = { name: String, terreno: Terrain ->
                viewModel.createSector(name, terreno);
                showCreateSectorPopup = false;
            })
    }
}