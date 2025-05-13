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
class UserViewModel @Inject constructor(repository: MainRepository) : ViewModel() {

    private val _user = MutableStateFlow(User("", null))
    val user = _user.asStateFlow()

    private var repo = repository

    private lateinit var terrainToShow: Terrain
    private lateinit var sectorToShow: Sector

    init {
        repo = repository;
    }

    fun changeName(name: String) {
        val copy: User = _user.value.copy()
        copy.name = name
        _user.value = copy
    }

    fun setUser(login: String) {
        _user.value = repo.getUser(login)
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
        _user.value.terrains?.add(Terrain(tName, tUb, ArrayList()))
    }

    fun deleteSector(terrainToShow: Terrain, sector: Sector) {
        _user.value.terrains?.find { it.name == terrainToShow.name }?.sectors?.remove(sector)
    }

    fun createSector(tName: String, terrain: Terrain) {
        _user.value.terrains?.find { it.name == terrainToShow.name }?.sectors?.add(Sector(tName, ArrayList()))
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