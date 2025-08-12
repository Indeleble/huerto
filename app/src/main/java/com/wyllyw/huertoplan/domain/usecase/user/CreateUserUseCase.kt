package com.wyllyw.huertoplan.domain.usecase.user

import com.wyllyw.huertoplan.data.repository.HuertoPlanRepository
import com.wyllyw.huertoplan.domain.model.HuertoPlanError
import com.wyllyw.huertoplan.domain.security.PasswordUtils
import com.wyllyw.huertoplan.domain.validation.ValidationUtils
import com.wyllyw.huertoplan.domain.validation.ValidationResult
import com.wyllyw.huertoplan.model.User
import java.util.UUID
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val repository: HuertoPlanRepository
) {
    suspend operator fun invoke(name: String, username: String, password: String): Result<User> {
        return try {
            // Validación de entrada
            val nameValidation = ValidationUtils.validateName(name.trim(), "Nombre completo")
            if (nameValidation is ValidationResult.Invalid) {
                return Result.failure(HuertoPlanError.ValidationError("nombre", nameValidation.message))
            }
            
            val usernameValidation = ValidationUtils.validateName(username.trim(), "Nombre de usuario")
            if (usernameValidation is ValidationResult.Invalid) {
                return Result.failure(HuertoPlanError.ValidationError("username", usernameValidation.message))
            }
            
            // Validación de contraseña
            val passwordError = PasswordUtils.getPasswordValidationError(password)
            if (passwordError != null) {
                return Result.failure(HuertoPlanError.ValidationError("contraseña", passwordError))
            }
            
            // Verificar que el username no exista
            val existingUserCount = repository.countUsersByUsername(username.trim())
            if (existingUserCount > 0) {
                return Result.failure(HuertoPlanError.ValidationError("username", "El nombre de usuario ya existe"))
            }
            
            // Hash de la contraseña
            val passwordHash = PasswordUtils.hashPassword(password)
            
            val user = User(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                username = username.trim(),
                passwordHash = passwordHash
            )
            
            repository.insertUser(user)
            Result.success(user)
            
        } catch (e: Exception) {
            Result.failure(HuertoPlanError.DatabaseError)
        }
    }
}