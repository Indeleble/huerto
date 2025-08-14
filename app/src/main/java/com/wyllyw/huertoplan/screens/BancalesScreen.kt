package com.wyllyw.huertoplan.screens

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
fun BancalesBodyContent(userViewModel: UserViewModel, bancalViewModel: BancalViewModel) {

    val bancales by bancalViewModel.bancales.collectAsStateWithLifecycle()
    val isLoading by bancalViewModel.isLoading.collectAsStateWithLifecycle()
    val error by bancalViewModel.error.collectAsStateWithLifecycle()
    val user by userViewModel.user.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Observar los estados de selección para trigger recomposición del botón
    val selectedTerrain by bancalViewModel.selectedTerrain.collectAsStateWithLifecycle()
    val selectedSector by bancalViewModel.selectedSector.collectAsStateWithLifecycle()
    
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
                        }
                    }
                }
                
                bancales.forEach { bancal ->
                    BancalDraggable(
                        bancal = bancal,
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
            
            // Botón de crear bancal
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                val hasSelections = selectedTerrain != null && selectedSector != null
                val buttonEnabled = !isLoading && hasSelections
                
                Log.d("BancalesScreen", "🔴 Button state: isLoading=$isLoading, hasSelections=$hasSelections (terrain=${selectedTerrain?.name}, sector=${selectedSector?.name}), buttonEnabled=$buttonEnabled")
                
                Button(
                    onClick = { showCreateBancalDialog = true },
                    modifier = Modifier.align(Alignment.Center),
                    enabled = buttonEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Crear Bancal")
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