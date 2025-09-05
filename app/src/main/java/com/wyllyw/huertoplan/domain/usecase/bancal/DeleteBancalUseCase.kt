package com.wyllyw.huertoplan.domain.usecase.bancal

import com.wyllyw.huertoplan.data.repository.HuertoPlanRepository
import javax.inject.Inject

class DeleteBancalUseCase @Inject constructor(
    private val repository: HuertoPlanRepository
) {
    suspend operator fun invoke(bancalId: String): Result<Unit> {
        return try {
            repository.deleteBancalById(bancalId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}