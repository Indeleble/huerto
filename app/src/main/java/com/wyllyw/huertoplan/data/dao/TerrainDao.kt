package com.wyllyw.huertoplan.data.dao

import androidx.room.*
import com.wyllyw.huertoplan.model.Terrain
import kotlinx.coroutines.flow.Flow

@Dao
interface TerrainDao {
    @Query("SELECT * FROM terrains")
    fun getAllTerrains(): Flow<List<Terrain>>
    
    @Query("SELECT * FROM terrains WHERE userId = :userId")
    fun getTerrainsByUserId(userId: String): Flow<List<Terrain>>
    
    @Query("SELECT * FROM terrains WHERE id = :id")
    suspend fun getTerrainById(id: String): Terrain?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTerrain(terrain: Terrain)
    
    @Update
    suspend fun updateTerrain(terrain: Terrain)
    
    @Delete
    suspend fun deleteTerrain(terrain: Terrain)
    
    @Query("DELETE FROM terrains WHERE id = :id")
    suspend fun deleteTerrainById(id: String)
}