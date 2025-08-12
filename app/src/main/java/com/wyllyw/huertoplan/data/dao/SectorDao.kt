package com.wyllyw.huertoplan.data.dao

import androidx.room.*
import com.wyllyw.huertoplan.model.Sector
import kotlinx.coroutines.flow.Flow

@Dao
interface SectorDao {
    @Query("SELECT * FROM sectors")
    fun getAllSectors(): Flow<List<Sector>>
    
    @Query("SELECT * FROM sectors WHERE terrainId = :terrainId")
    fun getSectorsByTerrainId(terrainId: String): Flow<List<Sector>>
    
    @Query("SELECT * FROM sectors WHERE id = :id")
    suspend fun getSectorById(id: String): Sector?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSector(sector: Sector)
    
    @Update
    suspend fun updateSector(sector: Sector)
    
    @Delete
    suspend fun deleteSector(sector: Sector)
    
    @Query("DELETE FROM sectors WHERE id = :id")
    suspend fun deleteSectorById(id: String)
}