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
import java.util.UUID

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BancalesScreen(navController: NavController, viewModel: UserViewModel) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val terrains by viewModel.terrains.collectAsStateWithLifecycle()
    val sectorsMap by viewModel.sectors.collectAsStateWithLifecycle()
    val bancalesMap by viewModel.bancales.collectAsStateWithLifecycle()
    val selectedTerrainId by viewModel.selectedTerrainId.collectAsStateWithLifecycle()
    val selectedSectorId by viewModel.selectedSectorId.collectAsStateWithLifecycle()
    
    var expandedTerreno by remember { mutableStateOf(false) }
    var expandedSector by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showCreateTerrenoDialog by remember { mutableStateOf(false) }
    var showCreateSectorDialog by remember { mutableStateOf(false) }
    var forceUpdate by remember { mutableStateOf(0) }
    
    // Obtener el terreno y sector seleccionados
    val selectedTerrain = selectedTerrainId?.let { terrainId ->
        terrains.find { it.id == terrainId }
    }
    
    val selectedSector = if (selectedTerrainId != null && selectedSectorId != null) {
        sectorsMap[selectedTerrainId]?.find { it.id == selectedSectorId }
    } else {
        null
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
            // Debug info para desarrollo
            Text(
                text = "Terrenos: ${terrains.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            
            // Selector de Terreno
            ExposedDropdownMenuBox(
                expanded = expandedTerreno,
                onExpandedChange = { expandedTerreno = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = selectedTerrain?.name ?: "Sin terrenos",
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
                    terrains.forEach { terreno ->
                        DropdownMenuItem(
                            text = { Text(terreno.name) },
                            onClick = {
                                viewModel.selectTerrain(terreno.id)
                                expandedTerreno = false
                                Log.d("BancalesScreen", "Terreno seleccionado: ${terreno.name}")
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
                    value = selectedSector?.name ?: "Sin sectores",
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
                    val terrainSectors = selectedTerrainId?.let { 
                        sectorsMap[it] ?: emptyList() 
                    } ?: emptyList()
                    
                    terrainSectors.forEach { sector ->
                        DropdownMenuItem(
                            text = { Text(sector.name) },
                            onClick = {
                                viewModel.selectSector(sector.id)
                                expandedSector = false
                                Log.d("BancalesScreen", "Sector seleccionado: ${sector.name}")
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
                onClick = { 
                    showEditDialog = true 
                    Log.d("BancalesScreen", "Mostrar diálogo de edición")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedTerrain != null && selectedSector != null
            ) {
                Text("Editar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido de Bancales
            if (selectedSector != null) {
                BancalesBodyContent(
                    navController = navController,
                    viewModel = viewModel,
                    sector = selectedSector,
                    state = forceUpdate
                )
            } else {
                // Mensaje cuando no hay sector seleccionado
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Selecciona o crea un terreno y un sector para comenzar",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    // Diálogo de Edición
    if (showEditDialog && selectedTerrain != null && selectedSector != null) {
        var editedTerrenoName by remember { mutableStateOf(selectedTerrain.name) }
        var editedTerrenoLocation by remember { mutableStateOf(selectedTerrain.Location) }
        var editedSectorName by remember { mutableStateOf(selectedSector.name) }

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
                        Log.d("BancalesScreen", "Guardando cambios de edición")
                        viewModel.updateTerrain(selectedTerrain, editedTerrenoName, editedTerrenoLocation)
                        viewModel.updateSector(selectedTerrain, selectedSector, editedSectorName)
                        showEditDialog = false
                        forceUpdate++ // Forzar actualización de la UI
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
                            Log.d("BancalesScreen", "Creando terreno: $newTerrenoName")
                            viewModel.createTerrain(newTerrenoName, newTerrenoLocation)
                            showCreateTerrenoDialog = false
                            forceUpdate++ // Forzar actualización de la UI
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
    if (showCreateSectorDialog && selectedTerrain != null) {
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
                            Log.d("BancalesScreen", "Creando sector: $newSectorName en terreno: ${selectedTerrain.name}")
                            viewModel.createSector(newSectorName, selectedTerrain)
                            showCreateSectorDialog = false
                            forceUpdate++ // Forzar actualización de la UI
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
fun BancalesBodyContent(navController: NavController, viewModel: UserViewModel, sector: Sector, state: Int) {
    // Obtener la lista de bancales para este sector
    val bancalesList by viewModel.bancales.collectAsStateWithLifecycle()
    
    // Filtrar bancales para este sector específico
    val sectorBancales = remember(sector.id, bancalesList, state) {
        bancalesList[sector.id] ?: emptyList()
    }
    
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
    var internalState by remember { mutableStateOf(state) }

    // Resetear el estado cuando cambia el sector
    LaunchedEffect(sector) {
        Log.d("BancalesScreen", "LaunchedEffect: Sector cambiado a ${sector.id}")
        selectedBancal = null
        dragOffset = Offset.Zero
        canvasOffset = Offset.Zero
        isDraggingCanvas = false
        bancalToEdit = null
        lastClickTime = 0L
        lastClickPosition = Offset.Zero
    }

    // Obtener los colores del tema
    val colors = MaterialTheme.colorScheme
    val bancalBackgroundColor = colors.surfaceVariant.copy(alpha = 0.2f)
    val selectedBancalBackgroundColor = colors.primaryContainer.copy(alpha = 0.3f)
    val bancalBorderColor = colors.outline
    val selectedBancalBorderColor = colors.primary
    val textColor = colors.onSurface.toArgb()
    val textSecondaryColor = colors.onSurfaceVariant.toArgb()

    key(internalState) {
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
                        .pointerInput(sector.id) { // Usar sector.id como clave para reiniciar el pointerInput
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val currentTime = System.currentTimeMillis()
                                    val position = event.changes.first().position
                                    
                                    if (event.type == PointerEventType.Press) {
                                        // Verificar si se hizo clic en el botón "Eliminar" de algún bancal
                                        sectorBancales.forEach { bancal ->
                                            val x = bancal.x * 100f + canvasOffset.x
                                            val y = bancal.y * 100f + canvasOffset.y
                                            val deleteButtonY = y + bancal.height * 100f - 30f
                                            
                                            // Verificar si el clic está dentro del botón eliminar
                                            if (position.x >= x + 10f && position.x <= x + 80f &&
                                                position.y >= deleteButtonY && position.y <= deleteButtonY + 25f) {
                                                Log.d("BancalesScreen", "Clic en botón eliminar del bancal: ${bancal.id}")
                                                try {
                                                    viewModel.deleteBancal(sector, bancal)
                                                    // Forzar recomposición
                                                    internalState = if (internalState == 0) 1 else 0
                                                } catch (e: Exception) {
                                                    Log.e("BancalesScreen", "Error al eliminar bancal: ${e.message}", e)
                                                }
                                                return@awaitPointerEventScope
                                            }
                                        }
                                        
                                        if (currentTime - lastClickTime < 300) {
                                            // Doble clic para editar bancal
                                            val clickedBancal = sectorBancales.find { bancal ->
                                                val x = bancal.x * 100f + canvasOffset.x
                                                val y = bancal.y * 100f + canvasOffset.y
                                                val w = bancal.width * 100f
                                                val h = bancal.height * 100f
                                                position.x in x..(x + w) &&
                                                position.y in y..(y + h)
                                            }
                                            
                                            if (clickedBancal != null) {
                                                Log.d("BancalesScreen", "Bancal seleccionado para editar: ${clickedBancal.id}")
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
                        .pointerInput(sector.id) { // Usar sector.id como clave para reiniciar el pointerInput
                            detectDragGestures(
                                onDragStart = { offset ->
                                    // Buscar si hay un bancal en esta posición
                                    selectedBancal = sectorBancales.find { bancal ->
                                        val x = bancal.x * 100f + canvasOffset.x
                                        val y = bancal.y * 100f + canvasOffset.y
                                        val w = bancal.width * 100f
                                        val h = bancal.height * 100f
                                        offset.x in x..(x + w) &&
                                        offset.y in y..(y + h)
                                    }

                                    if (selectedBancal != null) {
                                        Log.d("BancalesScreen", "Bancal seleccionado para arrastrar: ${selectedBancal?.id}")
                                        dragOffset = Offset.Zero
                                    } else {
                                        isDraggingCanvas = true
                                        lastDragPosition = offset
                                    }
                                },
                                onDragEnd = {
                                    if (selectedBancal != null) {
                                        selectedBancal?.let { bancal ->
                                            val updatedBancal = bancal.copy(
                                                x = bancal.x + dragOffset.x / 100f,
                                                y = bancal.y + dragOffset.y / 100f
                                            )
                                            Log.d("BancalesScreen", "Actualizando posición de bancal: ${bancal.id} a (${updatedBancal.x}, ${updatedBancal.y})")
                                            viewModel.updateBancal(updatedBancal)
                                            // Forzar recomposición para actualizar la posición
                                            internalState = if (internalState == 0) 1 else 0
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
                    sectorBancales.forEach { bancal ->
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
                            val paint = android.graphics.Paint()
                            paint.color = colors.error.toArgb()
                            drawRect(
                                x + 10f,
                                deleteButtonY,
                                x + 80f,
                                deleteButtonY + 25f,
                                paint
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
                onClick = { 
                    Log.d("BancalesScreen", "Botón de crear bancal presionado") 
                    showCreateDialog = true 
                },
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
            Log.d("BancalesScreen", "Mostrando diálogo de crear bancal")
            CreateBancalDialog(
                onDismiss = { 
                    Log.d("BancalesScreen", "Diálogo de crear bancal cancelado")
                    showCreateDialog = false 
                },
                onConfirm = { name, width, height ->
                    Log.d("BancalesScreen", "Creando bancal: $name para el sector: ${sector.id}")
                    // Generar un nuevo ID único
                    val bancalId = UUID.randomUUID().toString()
                    Log.d("BancalesScreen", "ID generado para el bancal: $bancalId")
                    
                    val bancal = Bancal(
                        id = bancalId,
                        name = name,
                        sectorId = sector.id,
                        x = 0f,
                        y = 0f,
                        width = width.toFloatOrNull() ?: 1f,
                        height = height.toFloatOrNull() ?: 1f
                    )
                    Log.d("BancalesScreen", "Bancal creado: $bancal")
                    
                    try {
                        viewModel.addBancal(sector, bancal)
                        Log.d("BancalesScreen", "Bancal añadido correctamente, actualizando estado")
                        internalState = if (internalState == 0) 1 else 0 // Toggle state to force recomposition
                        showCreateDialog = false
                    } catch (e: Exception) {
                        Log.e("BancalesScreen", "Error al añadir bancal: ${e.message}", e)
                    }
                }
            )
        }

        if (bancalToEdit != null) {
            Log.d("BancalesScreen", "Mostrando diálogo de editar bancal: ${bancalToEdit?.id}")
            EditBancalDialog(
                bancal = bancalToEdit!!,
                onDismiss = { 
                    Log.d("BancalesScreen", "Diálogo de editar bancal cancelado")
                    bancalToEdit = null 
                },
                onConfirm = { name, width, height ->
                    val updatedBancal = bancalToEdit!!.copy(
                        name = name,
                        width = width.toFloatOrNull() ?: bancalToEdit!!.width,
                        height = height.toFloatOrNull() ?: bancalToEdit!!.height
                    )
                    Log.d("BancalesScreen", "Actualizando bancal: ${bancalToEdit?.id} -> $updatedBancal")
                    viewModel.updateBancal(updatedBancal)
                    internalState = if (internalState == 0) 1 else 0 // Toggle state to force recomposition
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

