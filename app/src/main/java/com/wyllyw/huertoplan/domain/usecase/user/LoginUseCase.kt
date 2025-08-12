package com.wyllyw.huertoplan.domain.usecase.user

import com.wyllyw.huertoplan.data.repository.HuertoPlanRepository
import com.wyllyw.huertoplan.domain.model.HuertoPlanError
import com.wyllyw.huertoplan.domain.security.PasswordUtils
import com.wyllyw.huertoplan.model.User
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: HuertoPlanRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<User> {
        return try {
            // Validación de entrada
            if (username.isBlank()) {
                return Result.failure(HuertoPlanError.ValidationError("username", "El nombre de usuario no puede estar vacío"))
            }
            
            if (password.isBlank()) {
                return Result.failure(HuertoPlanError.ValidationError("contraseña", "La contraseña no puede estar vacía"))
            }
            
            // Buscar usuario por username
            val user = repository.getUserByUsername(username.trim())
            
            if (user == null) {
                return Result.failure(HuertoPlanError.ValidationError("credenciales", "Usuario o contraseña incorrectos"))
            }
            
            // Verificar contraseña
            val passwordIsValid = PasswordUtils.verifyPassword(password, user.passwordHash)
            
            if (!passwordIsValid) {
                return Result.failure(HuertoPlanError.ValidationError("credenciales", "Usuario o contraseña incorrectos"))
            }
            
            Result.success(user)
            
        } catch (e: Exception) {
            Result.failure(HuertoPlanError.DatabaseError)
        }
    }
}