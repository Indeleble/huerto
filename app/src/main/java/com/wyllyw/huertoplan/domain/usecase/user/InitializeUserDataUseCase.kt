package com.wyllyw.huertoplan.domain.usecase.user

import android.util.Log
import com.wyllyw.huertoplan.data.repository.HuertoPlanRepository
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.Sector
import java.util.UUID
import javax.inject.Inject

class InitializeUserDataUseCase @Inject constructor(
    private val repository: HuertoPlanRepository
) {
    companion object {
        private const val TAG = "InitializeUserDataUseCase"
    }
    
    suspend operator fun invoke(userId: String) {
        Log.d(TAG, "Initializing default data for user: $userId")
        try {
            // Para usuarios nuevos, siempre crear datos iniciales
            // En el futuro se podría verificar si ya existen, pero por simplicidad
            // asumimos que este Use Case solo se llama para usuarios nuevos
            
            // Crear terreno por defecto
            val defaultTerrain = Terrain(
                id = UUID.randomUUID().toString(),
                name = "Mi Primer Huerto",
                userId = userId
            )
            Log.d(TAG, "Creating default terrain: ${defaultTerrain.name} (id: ${defaultTerrain.id})")
            repository.insertTerrain(defaultTerrain)
            Log.d(TAG, "Default terrain inserted successfully")
            
            // Crear sector por defecto en ese terreno
            val defaultSector = Sector(
                id = UUID.randomUUID().toString(),
                name = "Zona Principal",
                terrainId = defaultTerrain.id
            )
            Log.d(TAG, "Creating default sector: ${defaultSector.name} (id: ${defaultSector.id})")
            repository.insertSector(defaultSector)
            Log.d(TAG, "Default sector inserted successfully")
            
            Log.d(TAG, "User data initialization completed successfully for user: $userId")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing user data for user $userId: ${e.message}", e)
            // Log error but don't fail user creation
            // En una implementación real, podrías usar logging aquí
            println("Error initializing user data: ${e.message}")
        }
    }
}