package com.wyllyw.huertoplan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wyllyw.huertoplan.domain.usecase.sector.CreateSectorUseCase
import com.wyllyw.huertoplan.domain.usecase.sector.GetSectorsByTerrainIdUseCase
import com.wyllyw.huertoplan.model.Sector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SectorViewModel @Inject constructor(
    private val getSectorsByTerrainIdUseCase: GetSectorsByTerrainIdUseCase,
    private val createSectorUseCase: CreateSectorUseCase
) : ViewModel() {

    private val _sectors = MutableStateFlow<List<Sector>>(emptyList())
    val sectors = _sectors.asStateFlow()

    private val _selectedSector = MutableStateFlow<Sector?>(null)
    val selectedSector = _selectedSector.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadSectorsForTerrain(terrainId: String) {
        viewModelScope.launch {
            try {
                getSectorsByTerrainIdUseCase(terrainId).collect { sectorList ->
                    _sectors.value = sectorList
                    
                    // Seleccionar el primer sector por defecto si hay sectores disponibles
                    if (sectorList.isNotEmpty() && _selectedSector.value == null) {
                        selectSector(sectorList[0])
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun createSector(name: String, terrainId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            createSectorUseCase(name, terrainId)
                .onSuccess { sector ->
                    // La lista se actualizará automáticamente por el Flow
                    selectSector(sector)
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }

    fun selectSector(sector: Sector) {
        _selectedSector.value = sector
    }

    fun clearSelection() {
        _selectedSector.value = null
    }

    fun clearError() {
        _error.value = null
    }
}