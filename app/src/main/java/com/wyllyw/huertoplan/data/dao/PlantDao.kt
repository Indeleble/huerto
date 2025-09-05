package com.wyllyw.huertoplan.data.dao

import androidx.room.*
import com.wyllyw.huertoplan.model.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY familia, nombre")
    fun getAllPlants(): Flow<List<Plant>>
    
    @Query("SELECT * FROM plants WHERE id = :plantId")
    suspend fun getPlantById(plantId: String): Plant?
    
    @Query("SELECT * FROM plants WHERE familia = :familia ORDER BY nombre")
    fun getPlantsByFamily(familia: String): Flow<List<Plant>>
    
    @Query("SELECT DISTINCT familia FROM plants ORDER BY familia")
    fun getAllFamilies(): Flow<List<String>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: Plant)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlants(plants: List<Plant>)
    
    @Update
    suspend fun updatePlant(plant: Plant)
    
    @Delete
    suspend fun deletePlant(plant: Plant)
    
    @Query("DELETE FROM plants")
    suspend fun deleteAllPlants()
}