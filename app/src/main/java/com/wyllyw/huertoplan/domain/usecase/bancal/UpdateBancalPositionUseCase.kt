package com.wyllyw.huertoplan.domain.usecase.bancal

import android.util.Log
import com.wyllyw.huertoplan.data.repository.HuertoPlanRepository
import javax.inject.Inject

class UpdateBancalPositionUseCase @Inject constructor(
    private val repository: HuertoPlanRepository
) {
    companion object {
        private const val TAG = "UpdateBancalPositionUseCase"
    }
    
    suspend operator fun invoke(bancalId: String, newX: Float, newY: Float): Result<Unit> {
        return try {
            Log.d(TAG, "Updating position for bancal: $bancalId to ($newX, $newY)")
            
            repository.updateBancalPosition(bancalId, newX, newY)
            
            Log.d(TAG, "✅ Position updated successfully for bancal: $bancalId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating bancal position: ${e.message}", e)
            Result.failure(e)
        }
    }
}