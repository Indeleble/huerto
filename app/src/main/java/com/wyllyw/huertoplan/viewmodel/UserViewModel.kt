package com.wyllyw.huertoplan.viewmodel

import androidx.lifecycle.ViewModel
import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.model.Sector
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    // Estado principal del usuario
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    // Estado para navegación y selección
    private val _selectedTerrain = MutableStateFlow<Terrain?>(null)
    val selectedTerrain = _selectedTerrain.asStateFlow()

    private val _selectedSector = MutableStateFlow<Sector?>(null)
    val selectedSector = _selectedSector.asStateFlow()

    // Estados para las listas completas
    private val _terrains = MutableStateFlow<List<Terrain>>(emptyList())
    val terrains = _terrains.asStateFlow()

    private val _sectors = MutableStateFlow<List<Sector>>(emptyList())
    val sectors = _sectors.asStateFlow()

    private val _bancales = MutableStateFlow<List<Bancal>>(emptyList())
    val bancales = _bancales.asStateFlow()

    // MARK: - Usuario
    fun setUser(userId: String) {
        // Cargar usuario del repositorio (mock por ahora)
        val user = repository.getUser(userId)
        _user.value = user
        
        // Cargar terrenos del usuario
        loadTerrainsForUser(userId)
    }

    fun updateUserName(newName: String) {
        _user.value?.let { currentUser ->
            _user.value = currentUser.copy(name = newName)
            // Aquí también actualizarías en el repositorio
        }
    }

    // MARK: - Terrenos
    fun loadTerrainsForUser(userId: String) {
        val userTerrains = repository.getTerrainsForUser(userId)
        _terrains.value = userTerrains
        
        // Seleccionar el primer terreno por defecto si hay terrenos disponibles
        if (userTerrains.isNotEmpty()) {
            selectTerrain(userTerrains[0])
        }
    }

    fun createTerrain(name: String, location: String) {
        val userId = _user.value?.id ?: return
        val newTerrain = repository.createTerrain(name, location, userId)
        _terrains.value = _terrains.value + newTerrain
    }

    fun deleteTerrain(terrainId: String) {
        repository.deleteTerrain(terrainId)
        _terrains.value = _terrains.value.filter { it.id != terrainId }
        
        // Si el terreno eliminado era el seleccionado, limpiar selección
        if (_selectedTerrain.value?.id == terrainId) {
            _selectedTerrain.value = null
            _selectedSector.value = null
            _sectors.value = emptyList()
            _bancales.value = emptyList()
        }
    }

    fun selectTerrain(terrain: Terrain) {
        _selectedTerrain.value = terrain
        loadSectorsForTerrain(terrain.id)
        // Limpiar selecciones de niveles inferiores
        _selectedSector.value = null
     //   _bancales.value = emptyList()
    }

    // MARK: - Sectores
    fun loadSectorsForTerrain(terrainId: String) {
        val terrainSectors = repository.getSectorsForTerrain(terrainId)
        _sectors.value = terrainSectors
        
        // Seleccionar el primer sector por defecto si hay sectores disponibles
        if (terrainSectors.isNotEmpty()) {
            selectSector(terrainSectors[0])
        }
    }

    fun createSector(name: String) {
        val terrainId = _selectedTerrain.value?.id ?: return
        val newSector = repository.createSector(name, terrainId)
        _sectors.value = _sectors.value + newSector
    }

    fun deleteSector(sectorId: String) {
        repository.deleteSector(sectorId)
        _sectors.value = _sectors.value.filter { it.id != sectorId }
        
        // Si el sector eliminado era el seleccionado, limpiar selección
        if (_selectedSector.value?.id == sectorId) {
            _selectedSector.value = null
            _bancales.value = emptyList()
        }
    }

    fun selectSector(sector: Sector) {
        _selectedSector.value = sector
        loadBancalesForSector(sector.id)
    }

    // MARK: - Bancales
    fun loadBancalesForSector(sectorId: String) {
        val sectorBancales = repository.getBancalesForSector(sectorId)
        _bancales.value = sectorBancales
    }

    fun createBancal(name: String, x: Float, y: Float, width: Float, height: Float) {
        val sectorId = _selectedSector.value?.id ?: return
        val newBancal = repository.createBancal(name, sectorId, x, y, width, height)
        _bancales.value = _bancales.value + newBancal
    }

    fun updateBancalPosition(bancalId: String, newX: Float, newY: Float) {
        repository.updateBancalPosition(bancalId, newX, newY)
        _bancales.value = _bancales.value.map { bancal ->
            if (bancal.id == bancalId) {
                bancal.copy(x = newX, y = newY)
            } else {
                bancal
            }
        }
    }

    fun deleteBancal(bancalId: String) {
        repository.deleteBancal(bancalId)
        _bancales.value = _bancales.value.filter { it.id != bancalId }
    }

    // MARK: - Getters de conveniencia
    fun getCurrentUser(): User? = _user.value
    fun getCurrentTerrain(): Terrain? = _selectedTerrain.value
    fun getCurrentSector(): Sector? = _selectedSector.value
    fun getCurrentBancales(): List<Bancal> = _bancales.value

    // MARK: - Estado de autenticación
    fun isUserLoggedIn(): Boolean = _user.value != null
    
    fun logout() {
        _user.value = null
        _selectedTerrain.value = null
        _selectedSector.value = null
        _terrains.value = emptyList()
        _sectors.value = emptyList()
        _bancales.value = emptyList()
    }

    fun addBancal(sector: Sector, bancal: Bancal) {
        _user.value.terrains
            ?.flatMap { it.sectors }
            ?.find { it.name == sector.name }
            ?.bancales
            ?.add(bancal)
        _user.value = _user.value.copy()
    }

    fun deleteBancal(sector: Sector, bancal: Bancal) {
        _user.value.terrains
            ?.flatMap { it.sectors }
            ?.find { it.name == sector.name }
            ?.bancales
            ?.remove(bancal)
        _user.value = _user.value.copy()
    }
}