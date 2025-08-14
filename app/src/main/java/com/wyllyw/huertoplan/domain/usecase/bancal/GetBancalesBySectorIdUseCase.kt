package com.wyllyw.huertoplan.domain.usecase.bancal

import android.util.Log
import com.wyllyw.huertoplan.data.dao.BancalDao
import com.wyllyw.huertoplan.model.Bancal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBancalesBySectorIdUseCase @Inject constructor(
    private val bancalDao: BancalDao
) {
    companion object {
        private const val TAG = "GetBancalesBySectorIdUseCase"
    }
    
    operator fun invoke(sectorId: String): Flow<List<Bancal>> {
        require(sectorId.isNotBlank()) { "Sector ID cannot be blank" }
        
        return bancalDao.getBancalesBySectorId(sectorId).map { bancales ->
            bancales.forEach { bancal ->
                Log.d(TAG, "📍 Bancal loaded with position: ${bancal.name} at (${bancal.x}, ${bancal.y}) size ${bancal.width}x${bancal.height}")
            }
            bancales
        }
    }
}