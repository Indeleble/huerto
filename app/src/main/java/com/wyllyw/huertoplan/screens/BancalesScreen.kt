package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.ui.components.HuertoButton
import com.wyllyw.huertoplan.ui.components.HuertoFloatingActionButton
import com.wyllyw.huertoplan.ui.components.HuertoOutlinedButton
import com.wyllyw.huertoplan.ui.components.HuertoTextField
import com.wyllyw.huertoplan.ui.components.HuertoTopAppBar
import com.wyllyw.huertoplan.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BancalesScreen(navController: NavController, viewModel: UserViewModel) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val sector = viewModel.getSectorToShow()

    Scaffold(
        topBar = {
            HuertoTopAppBar(
                title = "Bancales - ${sector.name}",
                onNavigateBack = { navController.navigateUp() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BancalesBodyContent(navController, viewModel)
        }
    }
}

@Composable
fun BancalesBodyContent(navController: NavController, viewModel: UserViewModel) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val sector = viewModel.getSectorToShow()
    var showCreateDialog by remember { mutableStateOf(false) }
    var bancalToEdit by remember { mutableStateOf<Bancal?>(null) }
    var selectedBancal by remember { mutableStateOf<Bancal?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var initialTouch by remember { mutableStateOf(Offset.Zero) }
    var canvasOffset by remember { mutableStateOf(Offset.Zero) }
    var lastDragPosition by remember { mutableStateOf(Offset.Zero) }
    var isDraggingCanvas by remember { mutableStateOf(false) }

    // Obtener los colores del tema una vez aquí
    val colors = MaterialTheme.colorScheme
    val bancalBackgroundColor = colors.surfaceVariant.copy(alpha = 0.2f)
    val selectedBancalBackgroundColor = colors.primaryContainer.copy(alpha = 0.3f)
    val bancalBorderColor = colors.outline
    val selectedBancalBorderColor = colors.primary
    val textColor = colors.onSurface.toArgb()
    val textSecondaryColor = colors.onSurfaceVariant.toArgb()

    var state by remember { mutableStateOf(true) }

    key(state) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Canvas con scroll
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, rotation ->
                            canvasOffset += pan
                        }
                    }
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val touchPoint = offset - canvasOffset
                                    selectedBancal = sector.bancales.find { bancal ->
                                        val x = bancal.x * 100f
                                        val y = bancal.y * 100f
                                        val w = bancal.width * 100f
                                        val h = bancal.height * 100f
                                        val touchArea = 50f
                                        touchPoint.x in (x - touchArea)..(x + w + touchArea) &&
                                                touchPoint.y in (y - touchArea)..(y + h + touchArea)
                                    }

                                    if (selectedBancal == null) {
                                        isDraggingCanvas = true
                                        lastDragPosition = offset
                                    } else {
                                        initialTouch = touchPoint
                                        dragOffset = Offset.Zero
                                    }
                                },
                                onDragEnd = {
                                    if (selectedBancal != null) {
                                        selectedBancal?.let { bancal ->
                                            bancal.x += dragOffset.x / 100f
                                            bancal.y += dragOffset.y / 100f
                                        }
                                        selectedBancal = null
                                        dragOffset = Offset.Zero
                                    }
                                    isDraggingCanvas = false
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    if (selectedBancal != null) {
                                        dragOffset += dragAmount
                                    } else if (isDraggingCanvas) {
                                        canvasOffset += dragAmount
                                    }
                                }
                            )
                        }
                ) {
                    // Dibujar bancales
                    sector.bancales.forEach { bancal ->
                        val x = if (bancal == selectedBancal) {
                            bancal.x * 100f + dragOffset.x + canvasOffset.x
                        } else {
                            bancal.x * 100f + canvasOffset.x
                        }
                        val y = if (bancal == selectedBancal) {
                            bancal.y * 100f + dragOffset.y + canvasOffset.y
                        } else {
                            bancal.y * 100f + canvasOffset.y
                        }

                        // Dibujar fondo del bancal
                        drawRect(
                            color = if (bancal == selectedBancal)
                                selectedBancalBackgroundColor
                            else
                                bancalBackgroundColor,
                            topLeft = Offset(x, y),
                            size = Size(bancal.width * 100f, bancal.height * 100f)
                        )

                        // Dibujar borde del bancal
                        drawRect(
                            color = if (bancal == selectedBancal)
                                selectedBancalBorderColor
                            else
                                bancalBorderColor,
                            topLeft = Offset(x, y),
                            size = Size(bancal.width * 100f, bancal.height * 100f),
                            style = Stroke(width = 3f)
                        )

                        // Dibujar información del bancal
                        drawContext.canvas.nativeCanvas.apply {
                            // Nombre del bancal
                            drawText(
                                bancal.name,
                                x + 10f,
                                y + 25f,
                                android.graphics.Paint().apply {
                                    color = textColor
                                    textSize = 24f
                                    isFakeBoldText = true
                                }
                            )

                            // Dimensiones del bancal
                            drawText(
                                "${bancal.width}m x ${bancal.height}m",
                                x + 10f,
                                y + 50f,
                                android.graphics.Paint().apply {
                                    color = textSecondaryColor
                                    textSize = 20f
                                }
                            )

                            // Botón de eliminar
                            val deleteButtonY = y + bancal.height * 100f - 30f
                            drawRect(
                                color = colors.error,
                                topLeft = Offset(x + 10f, deleteButtonY),
                                size = Size(70f, 25f)
                            )
                            drawText(
                                "Eliminar",
                                x + 15f,
                                deleteButtonY + 18f,
                                android.graphics.Paint().apply {
                                    color = colors.onError.toArgb()
                                    textSize = 16f
                                }
                            )
                        }
                    }
                }
            }

            // Botón flotante para crear bancal
            HuertoFloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Crear bancal"
                    )
                }
            )
        }


        // Diálogos
        if (showCreateDialog) {
            CreateBancalDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, width, height ->
                    val id = (sector.bancales.maxOfOrNull { it.id } ?: 0) + 1
                    val bancal = Bancal(
                        name,
                        id,
                        0f,
                        0f,
                        width.toFloatOrNull() ?: 1f,
                        height.toFloatOrNull() ?: 1f
                    )
                    viewModel.addBancal(sector, bancal)
                    state = !state
                    showCreateDialog = false
                }
            )
        }

        if (bancalToEdit != null) {
            EditBancalDialog(
                bancal = bancalToEdit!!,
                onDismiss = { bancalToEdit = null },
                onConfirm = { name, width, height ->
                    bancalToEdit!!.name = name
                    bancalToEdit!!.width = width.toFloatOrNull() ?: bancalToEdit!!.width
                    bancalToEdit!!.height = height.toFloatOrNull() ?: bancalToEdit!!.height
                    bancalToEdit = null
                }
            )
        }
    }
}


@Composable
fun CreateBancalDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, width: String, height: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear nuevo bancal") },
        text = {
            Column {
                HuertoTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre del bancal",
                    isError = isError && name.isBlank()
                )
                Spacer(modifier = Modifier.height(8.dp))
                HuertoTextField(
                    value = width,
                    onValueChange = { width = it },
                    label = "Ancho (metros)",
                    isError = isError && (width.isBlank() || width.toFloatOrNull() == null)
                )
                Spacer(modifier = Modifier.height(8.dp))
                HuertoTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = "Alto (metros)",
                    isError = isError && (height.isBlank() || height.toFloatOrNull() == null)
                )
            }
        },
        confirmButton = {
            HuertoButton(
                onClick = {
                    if (name.isNotBlank() && width.toFloatOrNull() != null && height.toFloatOrNull() != null) {
                        onConfirm(name, width, height)
                    } else {
                        isError = true
                    }
                },
                text = "Crear"
            )
        },
        dismissButton = {
            HuertoOutlinedButton(
                onClick = onDismiss,
                text = "Cancelar"
            )
        }
    )
}

@Composable
fun EditBancalDialog(
    bancal: Bancal,
    onDismiss: () -> Unit,
    onConfirm: (name: String, width: String, height: String) -> Unit
) {
    var name by remember { mutableStateOf(bancal.name) }
    var width by remember { mutableStateOf(bancal.width.toString()) }
    var height by remember { mutableStateOf(bancal.height.toString()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar bancal") },
        text = {
            Column {
                HuertoTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre del bancal",
                    isError = isError && name.isBlank()
                )
                Spacer(modifier = Modifier.height(8.dp))
                HuertoTextField(
                    value = width,
                    onValueChange = { width = it },
                    label = "Ancho (metros)",
                    isError = isError && (width.isBlank() || width.toFloatOrNull() == null)
                )
                Spacer(modifier = Modifier.height(8.dp))
                HuertoTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = "Alto (metros)",
                    isError = isError && (height.isBlank() || height.toFloatOrNull() == null)
                )
            }
        },
        confirmButton = {
            HuertoButton(
                onClick = {
                    if (name.isNotBlank() && width.toFloatOrNull() != null && height.toFloatOrNull() != null) {
                        onConfirm(name, width, height)
                    } else {
                        isError = true
                    }
                },
                text = "Guardar"
            )
        },
        dismissButton = {
            HuertoOutlinedButton(
                onClick = onDismiss,
                text = "Cancelar"
            )
        }
    )
}

