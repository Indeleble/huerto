package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chihsuanwu.freescroll.freeScroll
import com.chihsuanwu.freescroll.rememberFreeScrollState
import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.User
import com.wyllyw.huertoplan.screens.popups.CreateSectorDialog
import com.wyllyw.huertoplan.presentation.viewmodel.UserViewModel
import kotlin.math.roundToInt


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BancalesScreen(navController: NavController, viewModel: UserViewModel = hiltViewModel()) {

    Scaffold(
        topBar = {
            BarraSuperior(navController, "Bancales", true)
        },
    ) {
        //BancalesBodyContent(navController = navController, viewModel)
        BancalesBodyContent(viewModel)
    }

}

@Composable
fun BancalDraggable(
    bancal: Bancal,
    onBancalClick: (Bancal) -> Unit,
    onBancalMoved: (Bancal, Float, Float) -> Unit
) {
    var offsetX by remember { mutableStateOf(bancal.x) }
    var offsetY by remember { mutableStateOf(bancal.y) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(width = (bancal.width * 70).dp, height = (bancal.height * 70).dp)
            .background(
                color = if (isDragging) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInput(bancal.id) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                    },
                    onDragCancel = {
                        isDragging = false
                    }
                ) { change, dragAmount ->
                    if (kotlin.math.abs(dragAmount.x) > 1f || kotlin.math.abs(dragAmount.y) > 1f) {
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        onBancalMoved(bancal, offsetX, offsetY)
                    }
                }
            }
            .clickable {
                if (!isDragging) {
                    onBancalClick(bancal)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = bancal.name,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun BancalesBodyContent(viewModel: UserViewModel) {

    // Por ahora mostraremos una lista vacía ya que el UserViewModel no maneja bancales
    // TODO: Implementar BancalViewModel separado para manejar los bancales
    val bancales: List<Bancal> = emptyList() // viewModel.bancales.collectAsStateWithLifecycle()
    var showCreateSectorPopup by rememberSaveable { mutableStateOf(false) }

    val freeScrollState = rememberFreeScrollState()
    
    val user by viewModel.user.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        
        // Mostrar información del usuario logueado
        user?.let { currentUser ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Bienvenido, ${currentUser.name}!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .freeScroll(state = freeScrollState)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 5000.dp, height = 5000.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .freeScroll(state = freeScrollState)
            ) {
                // Mostrar mensaje si no hay bancales
                if (bancales.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay bancales aún.\nUsa el botón 'Crear' para empezar.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                bancales.forEach { bancal ->
                    BancalDraggable(
                        bancal = bancal,
                        onBancalClick = { clickedBancal ->
                            println("Bancal ${clickedBancal.name} clicked")
                        },
                        onBancalMoved = { movedBancal, newX, newY ->
                            // TODO: Implementar actualización de posición
                            println("Bancal ${movedBancal.name} moved to ($newX, $newY)")
                        }
                    )
                }
            }
        }

        // Botón de crear con colores del theme
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = { showCreateSectorPopup = true },
                modifier = Modifier.align(Alignment.Center),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(text = "Crear Bancal")
            }
        }
    }
/*
    CreateSectorDialog(
        terrain = viewModel.getCurrentTerrain(),
        showPopup = showCreateSectorPopup,
        onDismissRequest = { showCreateSectorPopup = false },
        onConfirmation = { name: String, terreno: Terrain ->
            viewModel.createSector(name)
            showCreateSectorPopup = false
        }
    )
    */

}