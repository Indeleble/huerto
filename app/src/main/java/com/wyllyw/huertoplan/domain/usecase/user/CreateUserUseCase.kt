package com.wyllyw.huertoplan.domain.usecase.user

import com.wyllyw.huertoplan.data.repository.HuertoPlanRepository
import com.wyllyw.huertoplan.domain.model.HuertoPlanError
import com.wyllyw.huertoplan.domain.validation.ValidationUtils
import com.wyllyw.huertoplan.domain.validation.ValidationResult
import com.wyllyw.huertoplan.model.User
import java.util.UUID
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val repository: HuertoPlanRepository
) {
    suspend operator fun invoke(name: String): Result<User> {
        return try {
            // Validación de entrada
            val validation = ValidationUtils.validateName(name.trim(), "Nombre de usuario")
            if (validation is ValidationResult.Invalid) {
                Result.failure(HuertoPlanError.InvalidUserName(name))
            } else {
                val user = User(
                    id = UUID.randomUUID().toString(),
                    name = name.trim()
                )
                repository.insertUser(user)
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(HuertoPlanError.DatabaseError)
        }
    }
}