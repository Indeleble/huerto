package com.wyllyw.huertoplan.domain.usecase.bancal

import com.wyllyw.huertoplan.data.repository.HuertoPlanRepository
import com.wyllyw.huertoplan.model.Bancal
import javax.inject.Inject

class UpdateBancalUseCase @Inject constructor(
    private val repository: HuertoPlanRepository
) {
    suspend operator fun invoke(
        bancalId: String,
        name: String,
        width: Float,
        height: Float
    ): Result<Bancal> {
        return try {
            val existingBancal = repository.getBancalById(bancalId)
                ?: return Result.failure(Exception("Bancal not found"))
            
            val updatedBancal = existingBancal.copy(
                name = name,
                width = width,
                height = height
            )
            
            repository.updateBancal(updatedBancal)
            Result.success(updatedBancal)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}