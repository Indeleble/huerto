package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.User
import com.wyllyw.huertoplan.screens.popups.CreateSectorDialog
import com.wyllyw.huertoplan.viewmodel.UserViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BancalesScreen(navController: NavController, viewModel: UserViewModel) {

    Scaffold(
        topBar = {
            BarraSuperior(navController, "Bancales", true)
        },
    ) {
        //BancalesBodyContent(navController = navController, viewModel)
        BancalesDragDrop(viewModel.getSectorToShow().bancales)
    }

}

@Composable
fun BancalesBodyContent(navController: NavController, viewModel: UserViewModel) {

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
            for (bancal in viewModel.getSectorToShow().bancales) {
                Spacer(modifier = Modifier.size(50.dp))
                Button(onClick = {

                    //  viewModel.setSectorToShow(sector)
                    // navController.navigate(AppScreens.BancalesScreen.route)

                }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(375.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            Text(
                                text = "This is a dialog with buttons and an image.",
                                modifier = Modifier.padding(16.dp),
                            )


                        }
                    }
                }
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

@Composable
fun BancalesDragDrop(banc : List<Bancal>) {
    var bancales by remember { mutableStateOf(banc) }
    var nextId by remember { mutableStateOf(0) }
    var selectedBancal by remember { mutableStateOf<Bancal?>(null) }
    val metroToPx = 100f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                bancales = bancales + Bancal(
                    name = nextId++.toString(),
                    id = nextId++,
                    x = 0f,
                    y = 0f,
                    width = 1f,
                    height = 5f
                )
            }) {
                Text("Añadir Bancal")
            }
            selectedBancal?.let { bancal ->
                Row {
                    Button(onClick = {
                        bancales = bancales.filterNot { it.id == bancal.id }
                        selectedBancal = null
                    }) {
                        Text("Eliminar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        bancales = bancales.map {
                            if (it.id == bancal.id) it.copy(
                                width = it.width + 1f, height = it.height + 1f
                            ) else it
                        }
                    }) {
                        Text("+ Tamaño")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
        ) {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .clickable { }
                .pointerInput(Unit) {
                    detectDragGestures(onDrag = { change, dragAmount ->
                        val pos = change.position
                        val draggedId = bancales.indexOfLast {
                            pos.x >= it.x * metroToPx && pos.x <= (it.x + it.width) * metroToPx && pos.y >= it.y * metroToPx && pos.y <= (it.y + it.height) * metroToPx
                        }
                        if (draggedId != -1) {
                            val updated = bancales.toMutableList()
                            updated[draggedId] = updated[draggedId].copy(
                                x = (updated[draggedId].x + dragAmount.x / metroToPx).coerceAtLeast(
                                    0f
                                ),
                                y = (updated[draggedId].y + dragAmount.y / metroToPx).coerceAtLeast(
                                    0f
                                )
                            )
                            bancales = updated
                        }
                    })
                }) {
                bancales.forEach { bancal ->
                    drawRect(
                        color = if (selectedBancal?.id == bancal.id) Color.Red else Color.Green,
                        topLeft = Offset(bancal.x * metroToPx, bancal.y * metroToPx),
                        size = Size(
                            bancal.width * metroToPx, bancal.height * metroToPx
                        ),
                        style = Stroke(width = 4f)
                    )
                }
            }
        }
    }
}