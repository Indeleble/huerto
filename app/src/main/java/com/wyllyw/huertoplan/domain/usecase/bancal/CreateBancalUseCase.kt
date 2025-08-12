package com.wyllyw.huertoplan.domain.usecase.bancal

import com.wyllyw.huertoplan.data.dao.BancalDao
import com.wyllyw.huertoplan.data.dao.SectorDao  
import com.wyllyw.huertoplan.domain.model.HuertoPlanError
import com.wyllyw.huertoplan.domain.validation.ValidationUtils
import com.wyllyw.huertoplan.domain.validation.ValidationResult
import com.wyllyw.huertoplan.model.Bancal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class CreateBancalUseCase @Inject constructor(
    private val bancalDao: BancalDao,
    private val sectorDao: SectorDao
) {
    suspend operator fun invoke(
        name: String,
        sectorId: String,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ): Result<Bancal> {
        return withContext(Dispatchers.IO) {
            try {
                // Validaciones múltiples
                val validation = ValidationUtils.validateMultiple(
                    ValidationUtils.validateName(name.trim(), "Nombre de bancal"),
                    ValidationUtils.validateId(sectorId, "ID de sector"),
                    ValidationUtils.validateCoordinate(x, "Coordenada X"),
                    ValidationUtils.validateCoordinate(y, "Coordenada Y"),
                    ValidationUtils.validateDimension(width, "Ancho"),
                    ValidationUtils.validateDimension(height, "Alto")
                )
                
                if (validation is ValidationResult.Invalid) {
                    return@withContext Result.failure(HuertoPlanError.ValidationError("bancal", validation.message))
                }
                
                // Verificar que el sector existe
                if (sectorDao.getSectorById(sectorId) == null) {
                    return@withContext Result.failure(HuertoPlanError.SectorNotFound(sectorId))
                }
                
                val bancal = Bancal(
                    id = UUID.randomUUID().toString(),
                    name = name.trim(),
                    sectorId = sectorId,
                    x = x,
                    y = y,
                    width = width,
                    height = height
                )
                bancalDao.insertBancal(bancal)
                Result.success(bancal)
            } catch (e: Exception) {
                Result.failure(HuertoPlanError.DatabaseError)
            }
        }
    }
}