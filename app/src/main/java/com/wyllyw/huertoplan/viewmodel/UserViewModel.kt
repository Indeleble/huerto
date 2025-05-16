package com.wyllyw.huertoplan.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.model.Sector
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: MainRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Estados para Usuario y sus datos relacionados
    private val _user = MutableStateFlow(User("", ""))
    val user: StateFlow<User> = _user.asStateFlow()

    private val _terrains = MutableStateFlow<List<Terrain>>(emptyList())
    val terrains: StateFlow<List<Terrain>> = _terrains.asStateFlow()

    private val _sectors = MutableStateFlow<Map<String, List<Sector>>>(emptyMap())
    val sectors: StateFlow<Map<String, List<Sector>>> = _sectors.asStateFlow()

    private val _bancales = MutableStateFlow<Map<String, List<Bancal>>>(emptyMap())
    val bancales: StateFlow<Map<String, List<Bancal>>> = _bancales.asStateFlow()

    // Para mantener el estado de selección actual
    private var _selectedTerrainId = MutableStateFlow<String?>(null)
    val selectedTerrainId: StateFlow<String?> = _selectedTerrainId.asStateFlow()
    
    private var _selectedSectorId = MutableStateFlow<String?>(null)
    val selectedSectorId: StateFlow<String?> = _selectedSectorId.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        _user.value = repository.getUser("")
        loadTerrains()
    }
    
    private fun loadTerrains() {
        val userId = _user.value.id
        if (userId.isNotEmpty()) {
            val terrainsList = repository.getTerrainsByUserId(userId)
            _terrains.value = terrainsList
            
            // Cargar sectores para cada terreno
            val sectorsMap = mutableMapOf<String, List<Sector>>()
            terrainsList.forEach { terrain ->
                val sectorsList = repository.getSectorsByTerrainId(terrain.id)
                sectorsMap[terrain.id] = sectorsList
                
                // Cargar bancales para cada sector
                val bancalesMap = mutableMapOf<String, List<Bancal>>()
                sectorsList.forEach { sector ->
                    val bancalesList = repository.getBancalesBySectorId(sector.id)
                    bancalesMap[sector.id] = bancalesList
                }
                
                _bancales.value = bancalesMap
            }
            
            _sectors.value = sectorsMap
            
            // Seleccionar el primer terreno por defecto si existe
            if (terrainsList.isNotEmpty() && _selectedTerrainId.value == null) {
                selectTerrain(terrainsList[0].id)
            }
        }
    }
    
    fun selectTerrain(terrainId: String) {
        _selectedTerrainId.value = terrainId
        
        // Seleccionar el primer sector de este terreno por defecto
        val terrainSectors = _sectors.value[terrainId] ?: emptyList()
        if (terrainSectors.isNotEmpty()) {
            selectSector(terrainSectors[0].id)
        } else {
            _selectedSectorId.value = null
        }
    }
    
    fun selectSector(sectorId: String) {
        _selectedSectorId.value = sectorId
    }

    fun changeName(name: String) {
        val updatedUser = _user.value.copy(name = name)
        _user.value = updatedUser
    }

    fun setUser(login: String) {
        _user.value = repository.getUser(login)
        loadTerrains()
    }

    fun createTerrain(tName: String, tUb: String) {
        val terrainId = UUID.randomUUID().toString()
        val newTerrain = Terrain(
            id = terrainId,
            name = tName,
            Location = tUb,
            userId = _user.value.id
        )
        
        // Actualizar la lista de terrenos
        val updatedTerrains = _terrains.value.toMutableList()
        updatedTerrains.add(newTerrain)
        _terrains.value = updatedTerrains
        
        // Actualizar los IDs de terrenos en el usuario
        val updatedUser = _user.value.copy(
            terrainsIds = _user.value.terrainsIds + terrainId
        )
        _user.value = updatedUser
        
        // Seleccionar el nuevo terreno
        selectTerrain(terrainId)
    }

    fun updateTerrain(terrain: Terrain, newName: String, newLocation: String) {
        val updatedTerrains = _terrains.value.toMutableList()
        val terrainIndex = updatedTerrains.indexOfFirst { it.id == terrain.id }
        
        if (terrainIndex != -1) {
            val updatedTerrain = terrain.copy(
                name = newName,
                Location = newLocation
            )
            updatedTerrains[terrainIndex] = updatedTerrain
            _terrains.value = updatedTerrains
        }
    }

    fun deleteTerrain(terrain: Terrain) {
        // Eliminar el terreno de la lista de terrenos
        val updatedTerrains = _terrains.value.filter { it.id != terrain.id }
        _terrains.value = updatedTerrains
        
        // Eliminar la referencia del ID de terreno en el usuario
        val updatedUser = _user.value.copy(
            terrainsIds = _user.value.terrainsIds.filter { it != terrain.id }
        )
        _user.value = updatedUser
        
        // Eliminar los sectores asociados a este terreno
        val updatedSectors = _sectors.value.toMutableMap()
        updatedSectors.remove(terrain.id)
        _sectors.value = updatedSectors
        
        // Si el terreno eliminado era el seleccionado, seleccionar otro
        if (_selectedTerrainId.value == terrain.id) {
            _selectedTerrainId.value = updatedTerrains.firstOrNull()?.id
            _selectedSectorId.value = null
        }
    }

    fun createSector(tName: String, terrain: Terrain) {
        val sectorId = UUID.randomUUID().toString()
        val newSector = Sector(
            id = sectorId,
            name = tName,
            terrainId = terrain.id
        )
        
        // Actualizar la lista de sectores para este terreno
        val updatedSectorsMap = _sectors.value.toMutableMap()
        val updatedSectors = (updatedSectorsMap[terrain.id] ?: emptyList()).toMutableList()
        updatedSectors.add(newSector)
        updatedSectorsMap[terrain.id] = updatedSectors
        _sectors.value = updatedSectorsMap
        
        // Actualizar la lista de IDs de sectores en el terreno
        val updatedTerrains = _terrains.value.toMutableList()
        val terrainIndex = updatedTerrains.indexOfFirst { it.id == terrain.id }
        
        if (terrainIndex != -1) {
            val updatedTerrain = updatedTerrains[terrainIndex].copy(
                sectorsIds = updatedTerrains[terrainIndex].sectorsIds + sectorId
            )
            updatedTerrains[terrainIndex] = updatedTerrain
            _terrains.value = updatedTerrains
        }
        
        // Seleccionar el nuevo sector
        selectSector(sectorId)
    }

    fun updateSector(terrain: Terrain, sector: Sector, newName: String) {
        val updatedSectorsMap = _sectors.value.toMutableMap()
        val sectorsList = updatedSectorsMap[terrain.id]?.toMutableList() ?: return
        
        val sectorIndex = sectorsList.indexOfFirst { it.id == sector.id }
        if (sectorIndex != -1) {
            val updatedSector = sector.copy(name = newName)
            sectorsList[sectorIndex] = updatedSector
            updatedSectorsMap[terrain.id] = sectorsList
            _sectors.value = updatedSectorsMap
        }
    }

    fun deleteSector(terrain: Terrain, sector: Sector) {
        // Eliminar el sector de la lista de sectores
        val updatedSectorsMap = _sectors.value.toMutableMap()
        val sectorsList = updatedSectorsMap[terrain.id]?.filter { it.id != sector.id } ?: emptyList()
        updatedSectorsMap[terrain.id] = sectorsList
        _sectors.value = updatedSectorsMap
        
        // Eliminar la referencia del ID de sector en el terreno
        val updatedTerrains = _terrains.value.toMutableList()
        val terrainIndex = updatedTerrains.indexOfFirst { it.id == terrain.id }
        
        if (terrainIndex != -1) {
            val updatedTerrain = updatedTerrains[terrainIndex].copy(
                sectorsIds = updatedTerrains[terrainIndex].sectorsIds.filter { it != sector.id }
            )
            updatedTerrains[terrainIndex] = updatedTerrain
            _terrains.value = updatedTerrains
        }
        
        // Eliminar los bancales asociados a este sector
        val updatedBancalesMap = _bancales.value.toMutableMap()
        updatedBancalesMap.remove(sector.id)
        _bancales.value = updatedBancalesMap
        
        // Si el sector eliminado era el seleccionado, seleccionar otro
        if (_selectedSectorId.value == sector.id) {
            _selectedSectorId.value = sectorsList.firstOrNull()?.id
        }
    }

    fun addBancal(sector: Sector, bancal: Bancal) {
        // Asegurarnos de que el bancal tenga el sectorId correcto y un ID válido
        Log.d("UserViewModel", "Intentando añadir bancal para sector: ${sector.id}, bancal: ${bancal.name}")
        
        // Verificar si el bancal ya tiene un ID, si no, generar uno nuevo
        val bancalId = if (bancal.id.isBlank()) UUID.randomUUID().toString() else bancal.id
        Log.d("UserViewModel", "ID de bancal a usar: $bancalId")
        
        val newBancal = bancal.copy(
            id = bancalId,
            sectorId = sector.id
        )
        
        Log.d("UserViewModel", "Bancal preparado: $newBancal")
        
        // Actualizar la lista de bancales para este sector
        val updatedBancalesMap = _bancales.value.toMutableMap()
        val updatedBancales = (updatedBancalesMap[sector.id] ?: emptyList()).toMutableList()
        updatedBancales.add(newBancal)
        updatedBancalesMap[sector.id] = updatedBancales
        _bancales.value = updatedBancalesMap
        
        Log.d("UserViewModel", "Bancales actualizados para sector ${sector.id}: ${updatedBancales.size}")
        
        // Actualizar la lista de IDs de bancales en el sector
        val terrainId = sector.terrainId
        Log.d("UserViewModel", "Actualizando sector en terrain: $terrainId")
        
        val updatedSectorsMap = _sectors.value.toMutableMap()
        val sectorsList = updatedSectorsMap[terrainId]?.toMutableList()
        
        if (sectorsList == null) {
            Log.e("UserViewModel", "No se encontró la lista de sectores para el terreno: $terrainId")
            return
        }
        
        val sectorIndex = sectorsList.indexOfFirst { it.id == sector.id }
        Log.d("UserViewModel", "Índice del sector a actualizar: $sectorIndex")
        
        if (sectorIndex != -1) {
            val updatedSector = sectorsList[sectorIndex].copy(
                bancalesIds = sectorsList[sectorIndex].bancalesIds + bancalId
            )
            sectorsList[sectorIndex] = updatedSector
            updatedSectorsMap[terrainId] = sectorsList
            _sectors.value = updatedSectorsMap
            
            Log.d("UserViewModel", "Sector actualizado, bancalesIds: ${updatedSector.bancalesIds}")
            
            // Forzar notificación a los collectors
            viewModelScope.launch {
                _bancales.value = _bancales.value
            }
        } else {
            Log.e("UserViewModel", "No se encontró el sector con ID ${sector.id} en la lista")
        }
    }

    fun updateBancal(bancal: Bancal) {
        val sectorId = bancal.sectorId
        
        val updatedBancalesMap = _bancales.value.toMutableMap()
        val bancalesList = updatedBancalesMap[sectorId]?.toMutableList() ?: return
        
        val bancalIndex = bancalesList.indexOfFirst { it.id == bancal.id }
        if (bancalIndex != -1) {
            bancalesList[bancalIndex] = bancal
            updatedBancalesMap[sectorId] = bancalesList
            _bancales.value = updatedBancalesMap
            
            // Forzar notificación a los collectors
            viewModelScope.launch {
                _bancales.value = _bancales.value
            }
        }
    }

    fun deleteBancal(sector: Sector, bancal: Bancal) {
        // Eliminar el bancal de la lista de bancales
        val updatedBancalesMap = _bancales.value.toMutableMap()
        val bancalesList = updatedBancalesMap[sector.id]?.filter { it.id != bancal.id } ?: emptyList()
        updatedBancalesMap[sector.id] = bancalesList
        _bancales.value = updatedBancalesMap
        
        // Eliminar la referencia del ID de bancal en el sector
        val terrainId = sector.terrainId
        val updatedSectorsMap = _sectors.value.toMutableMap()
        val sectorsList = updatedSectorsMap[terrainId]?.toMutableList() ?: return
        
        val sectorIndex = sectorsList.indexOfFirst { it.id == sector.id }
        if (sectorIndex != -1) {
            val updatedSector = sectorsList[sectorIndex].copy(
                bancalesIds = sectorsList[sectorIndex].bancalesIds.filter { it != bancal.id }
            )
            sectorsList[sectorIndex] = updatedSector
            updatedSectorsMap[terrainId] = sectorsList
            _sectors.value = updatedSectorsMap
            
            // Forzar notificación a los collectors
            viewModelScope.launch {
                _bancales.value = _bancales.value
            }
        }
    }
    
    // Métodos de ayuda para obtener objetos específicos por ID
    
    fun getTerrainById(terrainId: String): Terrain? {
        return _terrains.value.find { it.id == terrainId }
    }
    
    fun getSectorById(sectorId: String): Sector? {
        return _sectors.value.values.flatten().find { it.id == sectorId }
    }
    
    fun getBancalById(bancalId: String): Bancal? {
        return _bancales.value.values.flatten().find { it.id == bancalId }
    }
    
    fun getSelectedTerrain(): Terrain? {
        return _selectedTerrainId.value?.let { getTerrainById(it) }
    }
    
    fun getSelectedSector(): Sector? {
        return _selectedSectorId.value?.let { getSectorById(it) }
    }
    
    fun getSectorsForTerrain(terrainId: String): List<Sector> {
        return _sectors.value[terrainId] ?: emptyList()
    }
    
    fun getBancalesForSector(sectorId: String): List<Bancal> {
        return _bancales.value[sectorId] ?: emptyList()
    }
}