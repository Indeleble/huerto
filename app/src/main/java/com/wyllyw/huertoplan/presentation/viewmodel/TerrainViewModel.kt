package com.wyllyw.huertoplan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wyllyw.huertoplan.domain.usecase.terrain.CreateTerrainUseCase
import com.wyllyw.huertoplan.domain.usecase.terrain.GetTerrainsByUserIdUseCase
import com.wyllyw.huertoplan.model.Terrain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TerrainViewModel @Inject constructor(
    private val getTerrainsByUserIdUseCase: GetTerrainsByUserIdUseCase,
    private val createTerrainUseCase: CreateTerrainUseCase
) : ViewModel() {

    private val _terrains = MutableStateFlow<List<Terrain>>(emptyList())
    val terrains = _terrains.asStateFlow()

    private val _selectedTerrain = MutableStateFlow<Terrain?>(null)
    val selectedTerrain = _selectedTerrain.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadTerrainsForUser(userId: String) {
        viewModelScope.launch {
            try {
                getTerrainsByUserIdUseCase(userId).collect { terrainList ->
                    _terrains.value = terrainList
                    
                    // Seleccionar el primer terreno por defecto si hay terrenos disponibles
                    if (terrainList.isNotEmpty() && _selectedTerrain.value == null) {
                        selectTerrain(terrainList[0])
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun createTerrain(name: String, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            createTerrainUseCase(name, userId)
                .onSuccess { terrain ->
                    // La lista se actualizará automáticamente por el Flow
                    selectTerrain(terrain)
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }

    fun selectTerrain(terrain: Terrain) {
        _selectedTerrain.value = terrain
    }

    fun clearSelection() {
        _selectedTerrain.value = null
    }

    fun clearError() {
        _error.value = null
    }
}