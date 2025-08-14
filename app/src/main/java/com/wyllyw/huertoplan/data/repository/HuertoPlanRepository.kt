package com.wyllyw.huertoplan.data.repository

import com.wyllyw.huertoplan.data.dao.*
import com.wyllyw.huertoplan.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HuertoPlanRepository @Inject constructor(
    private val userDao: UserDao,
    private val terrainDao: TerrainDao,
    private val sectorDao: SectorDao,
    private val bancalDao: BancalDao
) {
    
    // User operations with proper threading
    suspend fun insertUser(user: User) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }
    
    suspend fun getUserById(id: String): User? = withContext(Dispatchers.IO) {
        userDao.getUserById(id)
    }
    
    suspend fun getUserByUsername(username: String): User? = withContext(Dispatchers.IO) {
        userDao.getUserByUsername(username)
    }
    
    suspend fun countUsersByUsername(username: String): Int = withContext(Dispatchers.IO) {
        userDao.countUsersByUsername(username)
    }
    
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    
    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }
    
    suspend fun deleteUser(user: User) = withContext(Dispatchers.IO) {
        userDao.deleteUser(user)
    }
    
    // Terrain operations with proper threading
    suspend fun insertTerrain(terrain: Terrain) = withContext(Dispatchers.IO) {
        terrainDao.insertTerrain(terrain)
    }
    
    suspend fun getTerrainById(id: String): Terrain? = withContext(Dispatchers.IO) {
        terrainDao.getTerrainById(id)
    }
    
    fun getTerrainsByUserId(userId: String): Flow<List<Terrain>> = 
        terrainDao.getTerrainsByUserId(userId)
    
    fun getAllTerrains(): Flow<List<Terrain>> = terrainDao.getAllTerrains()
    
    suspend fun updateTerrain(terrain: Terrain) = withContext(Dispatchers.IO) {
        terrainDao.updateTerrain(terrain)
    }
    
    suspend fun deleteTerrain(terrain: Terrain) = withContext(Dispatchers.IO) {
        terrainDao.deleteTerrain(terrain)
    }
    
    suspend fun deleteTerrainById(id: String) = withContext(Dispatchers.IO) {
        terrainDao.deleteTerrainById(id)
    }
    
    // Sector operations with proper threading
    suspend fun insertSector(sector: Sector) = withContext(Dispatchers.IO) {
        sectorDao.insertSector(sector)
    }
    
    suspend fun getSectorById(id: String): Sector? = withContext(Dispatchers.IO) {
        sectorDao.getSectorById(id)
    }
    
    fun getSectorsByTerrainId(terrainId: String): Flow<List<Sector>> = 
        sectorDao.getSectorsByTerrainId(terrainId)
    
    fun getAllSectors(): Flow<List<Sector>> = sectorDao.getAllSectors()
    
    suspend fun updateSector(sector: Sector) = withContext(Dispatchers.IO) {
        sectorDao.updateSector(sector)
    }
    
    suspend fun deleteSector(sector: Sector) = withContext(Dispatchers.IO) {
        sectorDao.deleteSector(sector)
    }
    
    suspend fun deleteSectorById(id: String) = withContext(Dispatchers.IO) {
        sectorDao.deleteSectorById(id)
    }
    
    // Bancal operations with proper threading
    suspend fun insertBancal(bancal: Bancal) = withContext(Dispatchers.IO) {
        bancalDao.insertBancal(bancal)
    }
    
    suspend fun getBancalById(id: String): Bancal? = withContext(Dispatchers.IO) {
        bancalDao.getBancalById(id)
    }
    
    fun getBancalesBySectorId(sectorId: String): Flow<List<Bancal>> = 
        bancalDao.getBancalesBySectorId(sectorId)
    
    fun getAllBancales(): Flow<List<Bancal>> = bancalDao.getAllBancales()
    
    suspend fun updateBancal(bancal: Bancal) = withContext(Dispatchers.IO) {
        bancalDao.updateBancal(bancal)
    }
    
    suspend fun updateBancalPosition(bancalId: String, x: Float, y: Float) = withContext(Dispatchers.IO) {
        bancalDao.updateBancalPosition(bancalId, x, y)
    }
    
    suspend fun deleteBancal(bancal: Bancal) = withContext(Dispatchers.IO) {
        bancalDao.deleteBancal(bancal)
    }
    
    suspend fun deleteBancalById(id: String) = withContext(Dispatchers.IO) {
        bancalDao.deleteBancalById(id)
    }
}