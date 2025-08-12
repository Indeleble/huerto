package com.wyllyw.huertoplan.domain.usecase.terrain

import com.wyllyw.huertoplan.data.dao.TerrainDao
import com.wyllyw.huertoplan.model.Terrain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTerrainsByUserIdUseCase @Inject constructor(
    private val terrainDao: TerrainDao
) {
    operator fun invoke(userId: String): Flow<List<Terrain>> {
        require(userId.isNotBlank()) { "User ID cannot be blank" }
        return terrainDao.getTerrainsByUserId(userId)
    }
}