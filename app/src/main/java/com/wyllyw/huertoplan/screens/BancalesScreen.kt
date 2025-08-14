package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chihsuanwu.freescroll.freeScroll
import com.chihsuanwu.freescroll.rememberFreeScrollState
import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.User
import com.wyllyw.huertoplan.screens.popups.CreateSectorDialog
import com.wyllyw.huertoplan.screens.popups.CreateBancalDialog
import com.wyllyw.huertoplan.presentation.viewmodel.UserViewModel
import com.wyllyw.huertoplan.presentation.viewmodel.BancalViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlin.math.roundToInt


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BancalesScreen(navController: NavController, userViewModel: UserViewModel) {
    val bancalViewModel: BancalViewModel = hiltViewModel()

    Scaffold(
        topBar = {
            BarraSuperior(navController, "Bancales", true)
        },
    ) {
        BancalesBodyContent(userViewModel, bancalViewModel)
    }

}

@Composable
fun BancalDraggable(
    bancal: Bancal,
    scale: Float = 1.0f,
    onBancalClick: (Bancal) -> Unit,
    onBancalMoved: (Bancal, Float, Float) -> Unit
) {
    // Posiciones escaladas para la visualización
    var offsetX by remember { mutableStateOf(bancal.x * scale) }
    var offsetY by remember { mutableStateOf(bancal.y * scale) }
    var isDragging by remember { mutableStateOf(false) }
    var lastScale by remember { mutableStateOf(scale) }
    
    // Actualizar posiciones cuando cambie el bancal (pero no solo la escala)
    LaunchedEffect(bancal.id, bancal.x, bancal.y) {
        if (!isDragging) {
            offsetX = bancal.x * scale
            offsetY = bancal.y * scale
            Log.d("BancalDraggable", "📍 Updated positions for ${bancal.name}: scaled pos ($offsetX, $offsetY) from real pos (${bancal.x}, ${bancal.y}) at scale $scale")
            Log.d("BancalDraggable", "📐 Visual size: ${bancal.width * 2}×${bancal.height} (real: ${bancal.width}×${bancal.height})")
        }
    }
    
    // Manejar cambios de escala manteniendo la posición relativa
    LaunchedEffect(scale) {
        if (!isDragging && scale != lastScale) {
            val scaleRatio = scale / lastScale
            offsetX *= scaleRatio
            offsetY *= scaleRatio
            Log.d("BancalDraggable", "🔄 Scale changed from $lastScale to $scale: adjusting positions by ratio $scaleRatio")
            Log.d("BancalDraggable", "📍 New scaled positions: ($offsetX, $offsetY)")
            lastScale = scale
        }
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(
                width = (bancal.width * 2 * 70 * scale).dp,  // Ancho visual = ancho real × 2
                height = (bancal.height * 70 * scale).dp      // Alto normal
            )
            .background(
                color = if (isDragging) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInput(bancal.id) {
                detectDragGestures(
                    onDragStart = { offset ->
                        Log.d("BancalDraggable", "🎯 Drag started for bancal: ${bancal.name} at offset: $offset")
                        isDragging = true
                    },
                    onDragEnd = {
                        val realX = offsetX / scale
                        val realY = offsetY / scale
                        Log.d("BancalDraggable", "🎯 Drag ended for bancal: ${bancal.name} final scaled position: ($offsetX, $offsetY), real position: ($realX, $realY)")
                        isDragging = false
                        // Las posiciones ya se actualizarán automáticamente por LaunchedEffect cuando se actualice el bancal en la DB
                    },
                    onDragCancel = {
                        Log.d("BancalDraggable", "🎯 Drag cancelled for bancal: ${bancal.name}")
                        isDragging = false
                    }
                ) { change, dragAmount ->
                    // Umbral mínimo para distinguir drag de scroll accidental
                    val minDragThreshold = 3f
                    if (kotlin.math.abs(dragAmount.x) > minDragThreshold || kotlin.math.abs(dragAmount.y) > minDragThreshold) {
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        
                        // Limitar movimiento dentro del área de trabajo (considerar escalado inverso)
                        val workspaceScale = 1f / scale  // Escalado inverso del área
                        val visualWidth = bancal.width * 2 * 70 * scale * density   // Ancho visual del bancal
                        val visualHeight = bancal.height * 70 * scale * density      // Alto visual del bancal
                        val maxX = 3000f * density * workspaceScale - visualWidth   // Área más grande con zoom out
                        val maxY = 2000f * density * workspaceScale - visualHeight
                        offsetX = offsetX.coerceIn(0f, maxX)
                        offsetY = offsetY.coerceIn(0f, maxY)
                        
                        // Convertir posición escalada a posición real (sin escalar) para almacenar
                        val realX = offsetX / scale
                        val realY = offsetY / scale
                        
                        Log.d("BancalDraggable", "🔄 Scaling: visual pos ($offsetX, $offsetY) -> real pos ($realX, $realY) at scale $scale")
                        Log.d("BancalDraggable", "💾 Saving real position for ${bancal.name}: ($realX, $realY)")
                        onBancalMoved(bancal, realX, realY)
                    }
                }
            }
            .clickable {
                if (!isDragging) {
                    onBancalClick(bancal)
                }
            },
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = bancal.name,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = (12 * scale).sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = (4 * scale).dp, vertical = (2 * scale).dp)
        )
    }
}

@Composable
fun BancalesBodyContent(userViewModel: UserViewModel, bancalViewModel: BancalViewModel) {

    val bancales by bancalViewModel.bancales.collectAsStateWithLifecycle()
    val isLoading by bancalViewModel.isLoading.collectAsStateWithLifecycle()
    val error by bancalViewModel.error.collectAsStateWithLifecycle()
    val user by userViewModel.user.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Observar los estados de selección para trigger recomposición del botón
    val selectedTerrain by bancalViewModel.selectedTerrain.collectAsStateWithLifecycle()
    val selectedSector by bancalViewModel.selectedSector.collectAsStateWithLifecycle()
    val scale by bancalViewModel.scale.collectAsStateWithLifecycle()
    
    var showCreateBancalDialog by rememberSaveable { mutableStateOf(false) }
    val freeScrollState = rememberFreeScrollState()

    // Configurar el userId en el BancalViewModel cuando el usuario esté disponible
    user?.let { currentUser ->
        Log.d("BancalesScreen", "User loaded: ${currentUser.name} (id: ${currentUser.id})")
        Log.d("BancalesScreen", "Calling bancalViewModel.setUserId(${currentUser.id})")
        bancalViewModel.setUserId(currentUser.id)
    } ?: run {
        Log.w("BancalesScreen", "User is null - BancalViewModel will not be initialized")
    }
    
    // Mostrar errores como Toast
    error?.let { errorMessage ->
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        bancalViewModel.clearError()
    }

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

        // Área scrolleable para bancales con scroll libre mejorado  
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .freeScroll(state = freeScrollState)
        ) {
            // Indicador de posición (minimap)
            ScrollPositionIndicator(
                freeScrollState = freeScrollState,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )
            // Área de trabajo con escalado inverso: más grande con zoom out, más pequeña con zoom in
            val workspaceScale = 1f / scale  // Escalado inverso
            Box(
                modifier = Modifier
                    .size(width = (3000 * workspaceScale).dp, height = (2000 * workspaceScale).dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f))
            ) {
                // Grid pattern de fondo (opcional)
                GridPattern(
                    modifier = Modifier.fillMaxSize(),
                    scale = 1.0f  // Grid con tamaño fijo, independiente del zoom
                )
                
                // Mostrar mensaje si no hay bancales
                if (bancales.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .size((400 * workspaceScale).dp, (300 * workspaceScale).dp) // Área escalada inversamente
                            .offset(x = (1300 * workspaceScale).dp, y = (850 * workspaceScale).dp), // Centrado
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🌱 No hay bancales aún",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Usa el botón 'Crear Bancal' para empezar",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = "💡 Arrastra para moverte por el área",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                
                // Bancales draggables
                bancales.forEach { bancal ->
                    BancalDraggable(
                        bancal = bancal,
                        scale = scale,
                        onBancalClick = { clickedBancal ->
                            bancalViewModel.selectBancal(clickedBancal)
                        },
                        onBancalMoved = { movedBancal, newX, newY ->
                            Log.d("BancalesScreen", "🚀 Bancal ${movedBancal.name} moved to ($newX, $newY)")
                            bancalViewModel.updateBancalPosition(movedBancal, newX, newY)
                        }
                    )
                }
            }
        }

        // Selectores y botón de crear bancal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Fila con los selectores de terreno y sector
            TerrainSectorSelectors(bancalViewModel)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón de crear bancal con controles de zoom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val hasSelections = selectedTerrain != null && selectedSector != null
                val buttonEnabled = !isLoading && hasSelections
                
                Log.d("BancalesScreen", "🔴 Button state: isLoading=$isLoading, hasSelections=$hasSelections (terrain=${selectedTerrain?.name}, sector=${selectedSector?.name}), buttonEnabled=$buttonEnabled")
                Log.d("BancalesScreen", "🔍 Current scale: ${(scale * 100).toInt()}%")
                Log.d("BancalesScreen", "📏 Workspace scale: ${(1f/scale * 100).toInt()}% (inverse of zoom)")
                
                Button(
                    onClick = { showCreateBancalDialog = true },
                    enabled = buttonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Crear Bancal")
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Controles de zoom
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón zoom out (-)
                    IconButton(
                        onClick = { bancalViewModel.zoomOut() },
                        enabled = scale > 0.25f
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Reducir zoom",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // Indicador de escala
                    Text(
                        text = "${(scale * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    // Botón zoom in (+)
                    IconButton(
                        onClick = { bancalViewModel.zoomIn() },
                        enabled = scale < 3.0f
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Aumentar zoom",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
    
    // Dialog para crear bancal
    CreateBancalDialog(
        showDialog = showCreateBancalDialog,
        sectorName = bancalViewModel.getCurrentSectorName(),
        isLoading = isLoading,
        onDismiss = { showCreateBancalDialog = false },
        onConfirm = { name, width, height ->
            bancalViewModel.createBancal(name, width, height)
            showCreateBancalDialog = false
        }
    )

}

@Composable
fun TerrainSelectorDropdown(
    selectedTerrainName: String,
    terrains: List<com.wyllyw.huertoplan.model.Terrain>,
    onTerrainSelected: (com.wyllyw.huertoplan.model.Terrain) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { if (enabled) expanded = true },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏞️ $selectedTerrainName",
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar terreno"
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            terrains.forEach { terrain ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = terrain.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onTerrainSelected(terrain)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SectorSelectorDropdown(
    selectedSectorName: String,
    sectors: List<com.wyllyw.huertoplan.model.Sector>,
    onSectorSelected: (com.wyllyw.huertoplan.model.Sector) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { if (enabled) expanded = true },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📍 $selectedSectorName",
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Seleccionar sector"
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sectors.forEach { sector ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = sector.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onSectorSelected(sector)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TerrainSectorSelectors(bancalViewModel: BancalViewModel) {
    val terrains by bancalViewModel.terrains.collectAsStateWithLifecycle()
    val sectors by bancalViewModel.sectors.collectAsStateWithLifecycle()
    val selectedTerrain by bancalViewModel.selectedTerrain.collectAsStateWithLifecycle()
    val selectedSector by bancalViewModel.selectedSector.collectAsStateWithLifecycle()
    
    // Log del estado actual de los selectores
    Log.d("TerrainSectorSelectors", "UI State - Terrains: ${terrains.size}, Sectors: ${sectors.size}")
    Log.d("TerrainSectorSelectors", "Selected Terrain: ${selectedTerrain?.name ?: "None"}")
    Log.d("TerrainSectorSelectors", "Selected Sector: ${selectedSector?.name ?: "None"}")
    Log.d("TerrainSectorSelectors", "Has selections: ${bancalViewModel.hasSelections()}")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Selector de terreno
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Terreno",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            TerrainSelectorDropdown(
                selectedTerrainName = bancalViewModel.getCurrentTerrainName(),
                terrains = terrains,
                onTerrainSelected = { terrain ->
                    bancalViewModel.selectTerrain(terrain)
                },
                enabled = terrains.isNotEmpty()
            )
        }

        // Selector de sector
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Sector",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            SectorSelectorDropdown(
                selectedSectorName = bancalViewModel.getCurrentSectorName(),
                sectors = sectors,
                onSectorSelected = { sector ->
                    bancalViewModel.selectSector(sector)
                },
                enabled = sectors.isNotEmpty()
            )
        }
    }
}

@Composable
fun GridPattern(
    modifier: Modifier = Modifier,
    gridSize: Float = 100f, // Tamaño de cada celda del grid en dp convertido a píxeles
    scale: Float = 1.0f,
    color: Color = Color.Gray.copy(alpha = 0.2f)
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val gridSizePx = gridSize * scale // Aplicar escala al tamaño del grid

        // Líneas verticales
        var x = 0f
        while (x <= canvasWidth) {
            drawLine(
                color = color,
                start = Offset(x, 0f),
                end = Offset(x, canvasHeight),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
            x += gridSizePx
        }

        // Líneas horizontales
        var y = 0f
        while (y <= canvasHeight) {
            drawLine(
                color = color,
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
            y += gridSizePx
        }
    }
}

@Composable
fun ScrollPositionIndicator(
    freeScrollState: com.chihsuanwu.freescroll.FreeScrollState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(100.dp, 60.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = "📍 Vista",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "3000x2000 dp",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}