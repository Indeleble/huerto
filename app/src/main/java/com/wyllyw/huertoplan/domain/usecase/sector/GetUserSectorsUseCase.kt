package com.wyllyw.huertoplan.domain.usecase.sector

import com.wyllyw.huertoplan.data.repository.HuertoPlanRepository
import com.wyllyw.huertoplan.model.Sector
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetUserSectorsUseCase @Inject constructor(
    private val repository: HuertoPlanRepository
) {
    operator fun invoke(userId: String): Flow<List<Sector>> {
        return repository.getTerrainsByUserId(userId)
            .flatMapLatest { terrains ->
                if (terrains.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    // Para simplicidad, tomamos sectores del primer terrain
                    // En una versión más compleja podríamos combinar sectores de todos los terrains
                    repository.getSectorsByTerrainId(terrains.first().id)
                }
            }
    }
}