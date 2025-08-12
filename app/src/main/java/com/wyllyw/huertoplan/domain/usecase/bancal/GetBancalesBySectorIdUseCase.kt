package com.wyllyw.huertoplan.domain.usecase.bancal

import com.wyllyw.huertoplan.data.dao.BancalDao
import com.wyllyw.huertoplan.model.Bancal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBancalesBySectorIdUseCase @Inject constructor(
    private val bancalDao: BancalDao
) {
    operator fun invoke(sectorId: String): Flow<List<Bancal>> {
        require(sectorId.isNotBlank()) { "Sector ID cannot be blank" }
        return bancalDao.getBancalesBySectorId(sectorId)
    }
}