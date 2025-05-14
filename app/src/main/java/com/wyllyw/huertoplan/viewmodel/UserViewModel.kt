package com.wyllyw.huertoplan.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.model.Sector
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.User
import com.wyllyw.huertoplan.repository.PlantasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: MainRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _user = MutableStateFlow(User("", null))
    val user = _user.asStateFlow()

    private lateinit var terrainToShow: Terrain
    private lateinit var sectorToShow: Sector

    private val plantasRepository = PlantasRepository(context)

    init {
        _user.value = repository.getUser("")
    }

    fun changeName(name: String) {
        val copy: User = _user.value.copy()
        copy.name = name
        _user.value = copy
    }

    fun setUser(login: String) {
        _user.value = repository.getUser(login)
    }

    fun deleteTerrain(terrain: Terrain) {
        var copy: User = _user.value.copy()
        val terrains = _user.value.terrains
        terrains?.remove(terrain)
        copy.terrains = terrains
        _user.value = copy
    }

    fun setTerrainToShow(terrain: Terrain){
        terrainToShow = terrain
    }

    fun getTerrainToShow(): Terrain {
        return terrainToShow
    }

    fun setSectorToShow(sector: Sector){
        sectorToShow = sector
    }

    fun getSectorToShow(): Sector {
        return sectorToShow
    }

    fun createTerrain(tName: String, tUb: String) {
        val newTerrain = Terrain(tName, tUb, ArrayList())
        newTerrain.sectors.add(Sector("Sector 1", ArrayList()))
        _user.value.terrains?.add(newTerrain)
        _user.value = _user.value.copy()
    }

    fun updateTerrain(terrain: Terrain, newName: String, newLocation: String) {
        val terrainIndex = _user.value.terrains?.indexOf(terrain)
        if (terrainIndex != null && terrainIndex != -1) {
            val updatedTerrain = terrain.copy(
                name = newName,
                Location = newLocation
            )
            _user.value.terrains?.set(terrainIndex, updatedTerrain)
            _user.value = _user.value.copy()
        }
    }

    fun deleteSector(terrain: Terrain, sector: Sector) {
        _user.value.terrains?.find { it.name == terrain.name }?.sectors?.remove(sector)
        _user.value = _user.value.copy()
    }

    fun createSector(tName: String, terrain: Terrain) {
        _user.value.terrains?.find { it.name == terrain.name }?.sectors?.add(Sector(tName, ArrayList()))
        _user.value = _user.value.copy()
    }

    fun updateSector(terrain: Terrain, sector: Sector, newName: String) {
        val terrainIndex = _user.value.terrains?.indexOf(terrain)
        if (terrainIndex != null && terrainIndex != -1) {
            val sectorIndex = _user.value.terrains?.get(terrainIndex)?.sectors?.indexOf(sector)
            if (sectorIndex != null && sectorIndex != -1) {
                val updatedSector = sector.copy(name = newName)
                _user.value.terrains?.get(terrainIndex)?.sectors?.set(sectorIndex, updatedSector)
                _user.value = _user.value.copy()
            }
        }
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