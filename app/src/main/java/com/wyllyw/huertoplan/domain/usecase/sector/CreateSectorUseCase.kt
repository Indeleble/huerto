package com.wyllyw.huertoplan.domain.usecase.sector

import com.wyllyw.huertoplan.data.dao.SectorDao
import com.wyllyw.huertoplan.data.dao.TerrainDao
import com.wyllyw.huertoplan.model.Sector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class CreateSectorUseCase @Inject constructor(
    private val sectorDao: SectorDao,
    private val terrainDao: TerrainDao
) {
    suspend operator fun invoke(name: String, terrainId: String): Result<Sector> {
        return withContext(Dispatchers.IO) {
            try {
                when {
                    name.isBlank() -> Result.failure(IllegalArgumentException("Sector name cannot be blank"))
                    terrainId.isBlank() -> Result.failure(IllegalArgumentException("Terrain ID cannot be blank"))
                    terrainDao.getTerrainById(terrainId) == null -> Result.failure(IllegalArgumentException("Terrain not found"))
                    else -> {
                        val sector = Sector(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            terrainId = terrainId
                        )
                        sectorDao.insertSector(sector)
                        Result.success(sector)
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}