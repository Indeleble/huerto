package com.wyllyw.huertoplan.data.dao

import androidx.room.*
import com.wyllyw.huertoplan.model.Bancal
import kotlinx.coroutines.flow.Flow

@Dao
interface BancalDao {
    @Query("SELECT * FROM bancales")
    fun getAllBancales(): Flow<List<Bancal>>
    
    @Query("SELECT * FROM bancales WHERE sectorId = :sectorId")
    fun getBancalesBySectorId(sectorId: String): Flow<List<Bancal>>
    
    @Query("SELECT * FROM bancales WHERE id = :id")
    suspend fun getBancalById(id: String): Bancal?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBancal(bancal: Bancal)
    
    @Update
    suspend fun updateBancal(bancal: Bancal)
    
    @Delete
    suspend fun deleteBancal(bancal: Bancal)
    
    @Query("DELETE FROM bancales WHERE id = :id")
    suspend fun deleteBancalById(id: String)
}