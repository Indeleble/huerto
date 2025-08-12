package com.wyllyw.huertoplan.domain.usecase.sector

import com.wyllyw.huertoplan.data.dao.SectorDao
import com.wyllyw.huertoplan.model.Sector
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSectorsByTerrainIdUseCase @Inject constructor(
    private val sectorDao: SectorDao
) {
    operator fun invoke(terrainId: String): Flow<List<Sector>> {
        require(terrainId.isNotBlank()) { "Terrain ID cannot be blank" }
        return sectorDao.getSectorsByTerrainId(terrainId)
    }
}