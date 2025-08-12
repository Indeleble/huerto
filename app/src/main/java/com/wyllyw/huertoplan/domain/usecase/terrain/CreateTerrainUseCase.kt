package com.wyllyw.huertoplan.domain.usecase.terrain

import com.wyllyw.huertoplan.data.dao.TerrainDao
import com.wyllyw.huertoplan.data.dao.UserDao
import com.wyllyw.huertoplan.model.Terrain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class CreateTerrainUseCase @Inject constructor(
    private val terrainDao: TerrainDao,
    private val userDao: UserDao
) {
    suspend operator fun invoke(name: String, userId: String): Result<Terrain> {
        return withContext(Dispatchers.IO) {
            try {
                when {
                    name.isBlank() -> Result.failure(IllegalArgumentException("Terrain name cannot be blank"))
                    userId.isBlank() -> Result.failure(IllegalArgumentException("User ID cannot be blank"))
                    userDao.getUserById(userId) == null -> Result.failure(IllegalArgumentException("User not found"))
                    else -> {
                        val terrain = Terrain(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            userId = userId
                        )
                        terrainDao.insertTerrain(terrain)
                        Result.success(terrain)
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}