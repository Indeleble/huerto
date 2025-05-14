package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.User
import com.wyllyw.huertoplan.model.Sector
import com.wyllyw.huertoplan.ui.components.HuertoButton
import com.wyllyw.huertoplan.ui.components.HuertoFloatingActionButton
import com.wyllyw.huertoplan.ui.components.HuertoOutlinedButton
import com.wyllyw.huertoplan.ui.components.HuertoTextField
import com.wyllyw.huertoplan.ui.components.HuertoTopAppBar
import com.wyllyw.huertoplan.viewmodel.UserViewModel
import kotlin.math.abs

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BancalesScreen(navController: NavController, viewModel: UserViewModel) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    var selectedTerreno by remember { mutableStateOf(user.terrains?.firstOrNull()) }
    var selectedSector by remember { mutableStateOf(selectedTerreno?.sectors?.firstOrNull()) }
    var expandedTerreno by remember { mutableStateOf(false) }
    var expandedSector by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showCreateTerrenoDialog by remember { mutableStateOf(false) }
    var showCreateSectorDialog by remember { mutableStateOf(false) }
    var state by remember { mutableStateOf(true) }

    // Actualizar el sector seleccionado cuando cambia el terreno
    LaunchedEffect(selectedTerreno) {
        selectedSector = selectedTerreno?.sectors?.firstOrNull()
        state = !state // Forzar actualización del canvas
    }

    // Actualizar el estado cuando cambia el sector
    LaunchedEffect(selectedSector) {
        state = !state // Forzar actualización del canvas
    }

    Scaffold(
        topBar = {
            HuertoTopAppBar(
                title = "Bancales",
                onNavigateBack = { navController.navigateUp() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Selector de Terreno
            ExposedDropdownMenuBox(
                expanded = expandedTerreno,
                onExpandedChange = { expandedTerreno = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedTerreno?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Terreno") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTerreno)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedTerreno,
                    onDismissRequest = { expandedTerreno = false }
                ) {
                    user.terrains?.forEach { terreno ->
                        DropdownMenuItem(
                            text = { Text(terreno.name) },
                            onClick = {
                                selectedTerreno = terreno
                                expandedTerreno = false
                            }
                        )
                    }
                    Divider()
                    DropdownMenuItem(
                        text = { Text("+ Crear nuevo terreno") },
                        onClick = {
                            showCreateTerrenoDialog = true
                            expandedTerreno = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Selector de Sector
            ExposedDropdownMenuBox(
                expanded = expandedSector,
                onExpandedChange = { expandedSector = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedSector?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sector") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSector)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedSector,
                    onDismissRequest = { expandedSector = false }
                ) {
                    selectedTerreno?.sectors?.forEach { sector ->
                        DropdownMenuItem(
                            text = { Text(sector.name) },
                            onClick = {
                                selectedSector = sector
                                expandedSector = false
                            }
                        )
                    }
                    Divider()
                    DropdownMenuItem(
                        text = { Text("+ Crear nuevo sector") },
                        onClick = {
                            showCreateSectorDialog = true
                            expandedSector = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de Editar
            Button(
                onClick = { showEditDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido de Bancales
            if (selectedSector != null) {
                BancalesBodyContent(
                    navController = navController,
                    viewModel = viewModel,
                    sector = selectedSector!!,
                    state = state
                )
            }
        }
    }

    // Diálogo de Edición
    if (showEditDialog && selectedTerreno != null && selectedSector != null) {
        var editedTerrenoName by remember { mutableStateOf(selectedTerreno!!.name) }
        var editedTerrenoLocation by remember { mutableStateOf(selectedTerreno!!.Location) }
        var editedSectorName by remember { mutableStateOf(selectedSector!!.name) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Terreno y Sector") },
            text = {
                Column {
                    Text("Terreno", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    HuertoTextField(
                        value = editedTerrenoName,
                        onValueChange = { editedTerrenoName = it },
                        label = "Nombre del terreno",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HuertoTextField(
                        value = editedTerrenoLocation,
                        onValueChange = { editedTerrenoLocation = it },
                        label = "Ubicación",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Sector", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    HuertoTextField(
                        value = editedSectorName,
                        onValueChange = { editedSectorName = it },
                        label = "Nombre del sector",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateTerrain(selectedTerreno!!, editedTerrenoName, editedTerrenoLocation)
                        viewModel.updateSector(selectedTerreno!!, selectedSector!!, editedSectorName)
                        showEditDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de Crear Terreno
    if (showCreateTerrenoDialog) {
        var newTerrenoName by remember { mutableStateOf("") }
        var newTerrenoLocation by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateTerrenoDialog = false },
            title = { Text("Crear Nuevo Terreno") },
            text = {
                Column {
                    HuertoTextField(
                        value = newTerrenoName,
                        onValueChange = { newTerrenoName = it },
                        label = "Nombre del terreno",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HuertoTextField(
                        value = newTerrenoLocation,
                        onValueChange = { newTerrenoLocation = it },
                        label = "Ubicación",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTerrenoName.isNotBlank() && newTerrenoLocation.isNotBlank()) {
                            viewModel.createTerrain(newTerrenoName, newTerrenoLocation)
                            showCreateTerrenoDialog = false
                        }
                    }
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTerrenoDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de Crear Sector
    if (showCreateSectorDialog && selectedTerreno != null) {
        var newSectorName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateSectorDialog = false },
            title = { Text("Crear Nuevo Sector") },
            text = {
                Column {
                    HuertoTextField(
                        value = newSectorName,
                        onValueChange = { newSectorName = it },
                        label = "Nombre del sector",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newSectorName.isNotBlank()) {
                            viewModel.createSector(newSectorName, selectedTerreno!!)
                            showCreateSectorDialog = false
                        }
                    }
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateSectorDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun BancalItem(bancal: Bancal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = bancal.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${bancal.width}m x ${bancal.height}m",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BancalesBodyContent(navController: NavController, viewModel: UserViewModel, sector: Sector, state: Boolean) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var bancalToEdit by remember { mutableStateOf<Bancal?>(null) }
    var selectedBancal by remember { mutableStateOf<Bancal?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var initialTouch by remember { mutableStateOf(Offset.Zero) }
    var canvasOffset by remember { mutableStateOf(Offset.Zero) }
    var lastDragPosition by remember { mutableStateOf(Offset.Zero) }
    var isDraggingCanvas by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableStateOf(0L) }
    var lastClickPosition by remember { mutableStateOf(Offset.Zero) }
    var state2 by remember { mutableStateOf(state) }

    // Resetear el estado cuando cambia el sector
    LaunchedEffect(sector) {
        selectedBancal = null
        dragOffset = Offset.Zero
        canvasOffset = Offset.Zero
        isDraggingCanvas = false
        bancalToEdit = null
        lastClickTime = 0L // Resetear el tiempo del último clic
        lastClickPosition = Offset.Zero // Resetear la posición del último clic
    }

    // Obtener los colores del tema una vez aquí
    val colors = MaterialTheme.colorScheme
    val bancalBackgroundColor = colors.surfaceVariant.copy(alpha = 0.2f)
    val selectedBancalBackgroundColor = colors.primaryContainer.copy(alpha = 0.3f)
    val bancalBorderColor = colors.outline
    val selectedBancalBorderColor = colors.primary
    val textColor = colors.onSurface.toArgb()
    val textSecondaryColor = colors.onSurfaceVariant.toArgb()

    key(state2) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Canvas con scroll
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, rotation ->
                            if (selectedBancal == null) {
                                canvasOffset += pan
                            }
                        }
                    }
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(sector) { // Añadir sector como key para resetear el pointerInput
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val currentTime = System.currentTimeMillis()
                                    val position = event.changes.first().position
                                    val touchPoint = position - canvasOffset

                                    if (event.type == PointerEventType.Press) {
                                        if (currentTime - lastClickTime < 300) {
                                            // Buscar bancal en la posición del clic
                                            val clickedBancal = sector.bancales.find { bancal ->
                                                val x = bancal.x * 100f + canvasOffset.x
                                                val y = bancal.y * 100f + canvasOffset.y
                                                val w = bancal.width * 100f
                                                val h = bancal.height * 100f
                                                val touchArea = 50f
                                                position.x in (x - touchArea)..(x + w + touchArea) &&
                                                position.y in (y - touchArea)..(y + h + touchArea)
                                            }
                                            
                                            if (clickedBancal != null) {
                                                bancalToEdit = clickedBancal
                                                continue
                                            }
                                        }
                                        lastClickTime = currentTime
                                        lastClickPosition = position
                                    }
                                }
                            }
                        }
                        .pointerInput(sector) { // Añadir sector como key para resetear el pointerInput
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val touchPoint = offset - canvasOffset
                                    selectedBancal = sector.bancales.find { bancal ->
                                        val x = bancal.x * 100f + canvasOffset.x
                                        val y = bancal.y * 100f + canvasOffset.y
                                        val w = bancal.width * 100f
                                        val h = bancal.height * 100f
                                        val touchArea = 50f
                                        offset.x in (x - touchArea)..(x + w + touchArea) &&
                                        offset.y in (y - touchArea)..(y + h + touchArea)
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
                    state2 = !state2
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
                    state2 = !state2
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
                    isError = isError && name.isBlank(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                HuertoTextField(
                    value = width,
                    onValueChange = { width = it },
                    label = "Ancho (metros)",
                    isError = isError && (width.isBlank() || width.toFloatOrNull() == null),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                HuertoTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = "Alto (metros)",
                    isError = isError && (height.isBlank() || height.toFloatOrNull() == null),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
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
                    isError = isError && name.isBlank(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                HuertoTextField(
                    value = width,
                    onValueChange = { width = it },
                    label = "Ancho (metros)",
                    isError = isError && (width.isBlank() || width.toFloatOrNull() == null),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                HuertoTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = "Alto (metros)",
                    isError = isError && (height.isBlank() || height.toFloatOrNull() == null),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTerrenosDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit,
    viewModel: UserViewModel
) {
    var showAddTerrenoDialog by remember { mutableStateOf(false) }
    var showAddSectorDialog by remember { mutableStateOf(false) }
    var showEditTerrenoDialog by remember { mutableStateOf<Terrain?>(null) }
    var showEditSectorDialog by remember { mutableStateOf<Pair<Terrain, Sector>?>(null) }
    var selectedTerreno by remember { mutableStateOf<Terrain?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Terrenos y Sectores") },
        text = {
            Column {
                // Lista de Terrenos
                user.terrains?.forEach { terreno ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = terreno.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Botones de editar y eliminar terreno
                        HuertoOutlinedButton(
                            onClick = { showEditTerrenoDialog = terreno },
                            text = "Editar",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        
                        HuertoOutlinedButton(
                            onClick = { viewModel.deleteTerrain(terreno) },
                            text = "Eliminar"
                        )
                    }
                    
                    // Lista de Sectores del Terreno
                    terreno.sectors.forEach { sector ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "  - ${sector.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            
                            // Botones de editar y eliminar sector
                            HuertoOutlinedButton(
                                onClick = { showEditSectorDialog = Pair(terreno, sector) },
                                text = "Editar",
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            
                            HuertoOutlinedButton(
                                onClick = { viewModel.deleteSector(terreno, sector) },
                                text = "Eliminar"
                            )
                        }
                    }
                }

                // Botones de Añadir
                HuertoButton(
                    onClick = { showAddTerrenoDialog = true },
                    text = "Añadir Terreno",
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                HuertoButton(
                    onClick = { showAddSectorDialog = true },
                    text = "Añadir Sector",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            HuertoButton(
                onClick = { onDismiss() },
                text = "Cerrar"
            )
        },
        dismissButton = {
            HuertoOutlinedButton(
                onClick = onDismiss,
                text = "Cancelar"
            )
        }
    )

    // Diálogo para añadir Terreno
    if (showAddTerrenoDialog) {
        var name by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddTerrenoDialog = false },
            title = { Text("Nuevo Terreno") },
            text = {
                Column {
                    HuertoTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre del terreno",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    HuertoTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = "Localización",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        )
                    )
                }
            },
            confirmButton = {
                HuertoButton(
                    onClick = {
                        if (name.isNotBlank() && location.isNotBlank()) {
                            viewModel.createTerrain(name, location)
                            showAddTerrenoDialog = false
                        }
                    },
                    text = "Crear"
                )
            },
            dismissButton = {
                HuertoOutlinedButton(
                    onClick = { showAddTerrenoDialog = false },
                    text = "Cancelar"
                )
            }
        )
    }

    // Diálogo para editar Terreno
    if (showEditTerrenoDialog != null) {
        var name by remember { mutableStateOf(showEditTerrenoDialog!!.name) }
        var location by remember { mutableStateOf(showEditTerrenoDialog!!.Location) }
        AlertDialog(
            onDismissRequest = { showEditTerrenoDialog = null },
            title = { Text("Editar Terreno") },
            text = {
                Column {
                    HuertoTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre del terreno",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    HuertoTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = "Localización",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        )
                    )
                }
            },
            confirmButton = {
                HuertoButton(
                    onClick = {
                        if (name.isNotBlank() && location.isNotBlank()) {
                            viewModel.updateTerrain(showEditTerrenoDialog!!, name, location)
                            showEditTerrenoDialog = null
                        }
                    },
                    text = "Guardar"
                )
            },
            dismissButton = {
                HuertoOutlinedButton(
                    onClick = { showEditTerrenoDialog = null },
                    text = "Cancelar"
                )
            }
        )
    }

    // Diálogo para añadir Sector
    if (showAddSectorDialog) {
        var name by remember { mutableStateOf("") }
        var expandedTerreno by remember { mutableStateOf(false) }
        var selectedTerrenoForSector by remember { mutableStateOf(user.terrains?.firstOrNull()) }

        AlertDialog(
            onDismissRequest = { showAddSectorDialog = false },
            title = { Text("Nuevo Sector") },
            text = {
                Column {
                    HuertoTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre del sector",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Selector de Terreno
                    ExposedDropdownMenuBox(
                        expanded = expandedTerreno,
                        onExpandedChange = { expandedTerreno = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = selectedTerrenoForSector?.name ?: "Seleccionar terreno",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTerreno) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedTerreno,
                            onDismissRequest = { expandedTerreno = false }
                        ) {
                            user.terrains?.forEach { terreno ->
                                DropdownMenuItem(
                                    text = { Text(terreno.name) },
                                    onClick = {
                                        selectedTerrenoForSector = terreno
                                        expandedTerreno = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                HuertoButton(
                    onClick = {
                        if (name.isNotBlank() && selectedTerrenoForSector != null) {
                            viewModel.createSector(name, selectedTerrenoForSector!!)
                            showAddSectorDialog = false
                        }
                    },
                    text = "Crear"
                )
            },
            dismissButton = {
                HuertoOutlinedButton(
                    onClick = { showAddSectorDialog = false },
                    text = "Cancelar"
                )
            }
        )
    }

    // Diálogo para editar Sector
    if (showEditSectorDialog != null) {
        var name by remember { mutableStateOf(showEditSectorDialog!!.second.name) }
        AlertDialog(
            onDismissRequest = { showEditSectorDialog = null },
            title = { Text("Editar Sector") },
            text = {
                Column {
                    HuertoTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre del sector",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        )
                    )
                }
            },
            confirmButton = {
                HuertoButton(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.updateSector(showEditSectorDialog!!.first, showEditSectorDialog!!.second, name)
                            showEditSectorDialog = null
                        }
                    },
                    text = "Guardar"
                )
            },
            dismissButton = {
                HuertoOutlinedButton(
                    onClick = { showEditSectorDialog = null },
                    text = "Cancelar"
                )
            }
        )
    }
}

