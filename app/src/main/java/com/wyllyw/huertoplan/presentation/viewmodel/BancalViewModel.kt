package com.wyllyw.huertoplan.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wyllyw.huertoplan.domain.usecase.bancal.CreateBancalUseCase
import com.wyllyw.huertoplan.domain.usecase.bancal.GetBancalesBySectorIdUseCase
import com.wyllyw.huertoplan.domain.usecase.bancal.UpdateBancalPositionUseCase
import com.wyllyw.huertoplan.domain.usecase.terrain.GetTerrainsByUserIdUseCase
import com.wyllyw.huertoplan.domain.usecase.sector.GetSectorsByTerrainIdUseCase
import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.model.Sector
import com.wyllyw.huertoplan.model.Terrain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BancalViewModel @Inject constructor(
    private val getBancalesBySectorIdUseCase: GetBancalesBySectorIdUseCase,
    private val createBancalUseCase: CreateBancalUseCase,
    private val updateBancalPositionUseCase: UpdateBancalPositionUseCase,
    private val getTerrainsByUserIdUseCase: GetTerrainsByUserIdUseCase,
    private val getSectorsByTerrainIdUseCase: GetSectorsByTerrainIdUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "BancalViewModel"
    }

    private val _bancales = MutableStateFlow<List<Bancal>>(emptyList())
    val bancales = _bancales.asStateFlow()

    // Terrenos del usuario
    private val _terrains = MutableStateFlow<List<Terrain>>(emptyList())
    val terrains = _terrains.asStateFlow()

    // Sectores del terreno seleccionado
    private val _sectors = MutableStateFlow<List<Sector>>(emptyList())
    val sectors = _sectors.asStateFlow()

    // Terreno actualmente seleccionado
    private val _selectedTerrain = MutableStateFlow<Terrain?>(null)
    val selectedTerrain = _selectedTerrain.asStateFlow()

    // Sector actualmente seleccionado
    private val _selectedSector = MutableStateFlow<Sector?>(null)
    val selectedSector = _selectedSector.asStateFlow()

    private val _selectedBancal = MutableStateFlow<Bancal?>(null)
    val selectedBancal = _selectedBancal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Control de escala/zoom
    private val _scale = MutableStateFlow(1.0f) // Escala por defecto
    val scale = _scale.asStateFlow()

    // Control del switch de movimiento de bancales
    private val _bancalMovementEnabled = MutableStateFlow(true) 
    val bancalMovementEnabled = _bancalMovementEnabled.asStateFlow()

    private val _currentUserId = MutableStateFlow<String?>(null)
    
    fun setUserId(userId: String) {
        Log.d(TAG, "🔄 setUserId called with userId: $userId")
        Log.d(TAG, "   Current userId: ${_currentUserId.value}")
        Log.d(TAG, "   Current selections: Terrain=${_selectedTerrain.value?.name}, Sector=${_selectedSector.value?.name}")
        
        if (_currentUserId.value != userId) {
            _currentUserId.value = userId
            Log.d(TAG, "🆕 UserId changed, resetting selections and loading terrains for user: $userId")
            // Reset selections when user changes
            _selectedTerrain.value = null
            _selectedSector.value = null
            _terrains.value = emptyList()
            _sectors.value = emptyList()
            _bancales.value = emptyList()
            loadTerrainsForUser(userId)
        } else {
            Log.d(TAG, "✅ UserId unchanged, keeping current state")
            Log.d(TAG, "   Current terrains count: ${_terrains.value.size}")
            Log.d(TAG, "   Current sectors count: ${_sectors.value.size}")
            Log.d(TAG, "   Has selections: ${hasSelections()}")
        }
    }

    private fun loadTerrainsForUser(userId: String) {
        Log.d(TAG, "loadTerrainsForUser called for userId: $userId")
        viewModelScope.launch {
            try {
                getTerrainsByUserIdUseCase(userId).collect { terrainList ->
                    Log.d(TAG, "=== TERRAINS LOADED FROM DATABASE ===")
                    Log.d(TAG, "User ID: $userId")
                    Log.d(TAG, "Number of terrains found: ${terrainList.size}")
                    terrainList.forEachIndexed { index, terrain ->
                        Log.d(TAG, "Terrain $index: ${terrain.name} (id: ${terrain.id}, userId: ${terrain.userId})")
                    }
                    
                    _terrains.value = terrainList
                    
                    // Seleccionar el primer terreno por defecto
                    if (terrainList.isNotEmpty()) {
                        if (_selectedTerrain.value == null) {
                            Log.d(TAG, "Auto-selecting first terrain: ${terrainList.first().name}")
                            selectTerrain(terrainList.first())
                        } else {
                            Log.d(TAG, "Terrain already selected: ${_selectedTerrain.value?.name}, keeping current selection")
                        }
                    } else {
                        Log.w(TAG, "No terrains found for user: $userId - This should not happen for registered users!")
                        _error.value = "No se encontraron terrenos para el usuario. Puede que necesites reinicializar los datos."
                    }
                    Log.d(TAG, "=== END TERRAINS LOADED ===")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading terrains: ${e.message}", e)
                _error.value = "Error cargando terrenos: ${e.message}"
            }
        }
    }

    fun selectTerrain(terrain: Terrain) {
        Log.d(TAG, "selectTerrain called with terrain: ${terrain.name} (id: ${terrain.id})")
        _selectedTerrain.value = terrain
        Log.d(TAG, "✅ Terrain selected successfully: ${_selectedTerrain.value?.name}")
        loadSectorsForTerrain(terrain.id)
    }

    private fun loadSectorsForTerrain(terrainId: String) {
        Log.d(TAG, "loadSectorsForTerrain called for terrainId: $terrainId")
        viewModelScope.launch {
            try {
                getSectorsByTerrainIdUseCase(terrainId).collect { sectorList ->
                    Log.d(TAG, "=== SECTORS LOADED FROM DATABASE ===")
                    Log.d(TAG, "Terrain ID: $terrainId")
                    Log.d(TAG, "Number of sectors found: ${sectorList.size}")
                    sectorList.forEachIndexed { index, sector ->
                        Log.d(TAG, "Sector $index: ${sector.name} (id: ${sector.id}, terrainId: ${sector.terrainId})")
                    }
                    
                    _sectors.value = sectorList
                    
                    // Seleccionar el primer sector por defecto
                    if (sectorList.isNotEmpty()) {
                        if (_selectedSector.value == null) {
                            Log.d(TAG, "Auto-selecting first sector: ${sectorList.first().name}")
                            selectSector(sectorList.first())
                        } else {
                            Log.d(TAG, "Sector already selected: ${_selectedSector.value?.name}, keeping current selection")
                        }
                    } else {
                        Log.w(TAG, "No sectors found for terrain: $terrainId - This should not happen!")
                        _error.value = "No se encontraron sectores para el terreno seleccionado."
                    }
                    Log.d(TAG, "=== END SECTORS LOADED ===")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading sectors: ${e.message}", e)
                _error.value = "Error cargando sectores: ${e.message}"
            }
        }
    }

    fun selectSector(sector: Sector) {
        Log.d(TAG, "selectSector called with sector: ${sector.name} (id: ${sector.id})")
        _selectedSector.value = sector
        Log.d(TAG, "✅ Sector selected successfully: ${_selectedSector.value?.name}")
        Log.d(TAG, "🎯 Both selections now: Terrain=${_selectedTerrain.value?.name}, Sector=${_selectedSector.value?.name}")
        loadBancalesForSector(sector.id)
    }

    fun loadBancalesForSector(sectorId: String) {
        Log.d(TAG, "loadBancalesForSector called for sectorId: $sectorId")
        viewModelScope.launch {
            try {
                getBancalesBySectorIdUseCase(sectorId).collect { bancalList ->
                    Log.d(TAG, "=== BANCALES LOADED FROM DATABASE ===")
                    Log.d(TAG, "Sector ID: $sectorId")
                    Log.d(TAG, "Number of bancales found: ${bancalList.size}")
                    bancalList.forEachIndexed { index, bancal ->
                        Log.d(TAG, "Bancal $index: ${bancal.name} (id: ${bancal.id}, sectorId: ${bancal.sectorId}, dimensions: ${bancal.width}x${bancal.height})")
                    }
                    
                    _bancales.value = bancalList
                    Log.d(TAG, "=== END BANCALES LOADED ===")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading bancales: ${e.message}", e)
                _error.value = "Error cargando bancales: ${e.message}"
            }
        }
    }

    fun createBancal(
        name: String,
        width: Float,
        height: Float
    ) {
        Log.d(TAG, "createBancal called with name: $name, width: $width, height: $height")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val currentSector = _selectedSector.value
                if (currentSector == null) {
                    Log.w(TAG, "No sector selected when trying to create bancal")
                    _error.value = "No hay sector seleccionado. Selecciona un sector primero."
                    return@launch
                }
                
                Log.d(TAG, "Creating bancal in sector: ${currentSector.name} (id: ${currentSector.id})")
                
                // Posición inicial en el centro de la pantalla (coordenadas arbitrarias)
                val centerX = 400f
                val centerY = 400f
                
                createBancalUseCase(name, currentSector.id, centerX, centerY, width, height)
                    .onSuccess { bancal ->
                        Log.d(TAG, "Bancal created successfully: ${bancal.name} (id: ${bancal.id})")
                        _selectedBancal.value = bancal
                        // La lista se actualizará automáticamente por el Flow
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Error creating bancal: ${exception.message}", exception)
                        _error.value = exception.message
                    }
                    
            } catch (e: Exception) {
                Log.e(TAG, "Exception in createBancal: ${e.message}", e)
                _error.value = "Error al crear bancal: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBancalPosition(bancal: Bancal, newX: Float, newY: Float) {
        Log.d(TAG, "🔄 Updating bancal position: ${bancal.name} from (${bancal.x}, ${bancal.y}) to ($newX, $newY)")
        viewModelScope.launch {
            try {
                updateBancalPositionUseCase(bancal.id, newX, newY)
                    .onSuccess {
                        Log.d(TAG, "✅ Position updated successfully in database for bancal: ${bancal.name}")
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "❌ Failed to update position for bancal: ${bancal.name}", exception)
                        _error.value = "Error al actualizar posición: ${exception.message}"
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception updating bancal position: ${e.message}", e)
                _error.value = "Error al mover bancal: ${e.message}"
            }
        }
    }

    fun selectBancal(bancal: Bancal) {
        _selectedBancal.value = bancal
    }

    fun clearSelection() {
        _selectedBancal.value = null
    }

    fun clearError() {
        _error.value = null
    }
    
    fun getCurrentTerrainName(): String {
        return _selectedTerrain.value?.name ?: "Seleccionar terreno"
    }
    
    fun getCurrentSectorName(): String {
        return _selectedSector.value?.name ?: "Seleccionar sector"
    }
    
    fun zoomIn() {
        val currentScale = _scale.value
        val newScale = (currentScale * 1.25f).coerceAtMost(3.0f) // Máximo 300%
        if (newScale != currentScale) {
            _scale.value = newScale
            Log.d(TAG, "🔍 Zoom In: Escala cambiada de ${(currentScale * 100).toInt()}% a ${(newScale * 100).toInt()}%")
            Log.d(TAG, "📱 StateFlow scale updated to: $newScale - UI should recompose")
        } else {
            Log.d(TAG, "🔍 Zoom In: Ya en escala máxima (${(newScale * 100).toInt()}%)")
        }
    }
    
    fun zoomOut() {
        val currentScale = _scale.value
        val newScale = (currentScale / 1.25f).coerceAtLeast(0.25f) // Mínimo 25%
        if (newScale != currentScale) {
            _scale.value = newScale
            Log.d(TAG, "🔍 Zoom Out: Escala cambiada de ${(currentScale * 100).toInt()}% a ${(newScale * 100).toInt()}%")
            Log.d(TAG, "📱 StateFlow scale updated to: $newScale - UI should recompose")
        } else {
            Log.d(TAG, "🔍 Zoom Out: Ya en escala mínima (${(newScale * 100).toInt()}%)")
        }
    }
    
    fun resetZoom() {
        val currentScale = _scale.value
        _scale.value = 1.0f
        Log.d(TAG, "🔍 Zoom Reset: Escala cambiada de ${(currentScale * 100).toInt()}% a 100%")
        Log.d(TAG, "📱 StateFlow scale reset to: 1.0 - UI should recompose")
    }

    fun toggleBancalMovement() {
        val currentValue = _bancalMovementEnabled.value
        _bancalMovementEnabled.value = !currentValue
        Log.d(TAG, "🔄 Bancal movement toggled: ${!currentValue}")
    }

    fun hasSelections(): Boolean {
        val hasTerrainSelection = _selectedTerrain.value != null
        val hasSectorSelection = _selectedSector.value != null
        val result = hasTerrainSelection && hasSectorSelection
        
        Log.d(TAG, "hasSelections() check:")
        Log.d(TAG, "  - Has terrain selection: $hasTerrainSelection (${_selectedTerrain.value?.name})")
        Log.d(TAG, "  - Has sector selection: $hasSectorSelection (${_selectedSector.value?.name})")
        Log.d(TAG, "  - Result: $result")
        
        return result
    }
}