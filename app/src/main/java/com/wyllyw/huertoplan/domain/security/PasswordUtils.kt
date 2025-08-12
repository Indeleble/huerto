package com.wyllyw.huertoplan.domain.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordUtils {
    
    private const val SALT_LENGTH = 16
    private const val HASH_ALGORITHM = "SHA-256"
    
    /**
     * Genera un salt aleatorio para el hash de la contraseña
     */
    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt
    }
    
    /**
     * Hashea una contraseña con salt
     * @param password Contraseña en texto plano
     * @return Hash de la contraseña en formato "salt:hash" (ambos en Base64)
     */
    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = hashPasswordWithSalt(password, salt)
        
        val saltBase64 = Base64.getEncoder().encodeToString(salt)
        val hashBase64 = Base64.getEncoder().encodeToString(hash)
        
        return "$saltBase64:$hashBase64"
    }
    
    /**
     * Verifica si una contraseña coincide con el hash almacenado
     * @param password Contraseña en texto plano
     * @param storedHash Hash almacenado en formato "salt:hash"
     * @return true si la contraseña es correcta
     */
    fun verifyPassword(password: String, storedHash: String): Boolean {
        try {
            val parts = storedHash.split(":")
            if (parts.size != 2) return false
            
            val salt = Base64.getDecoder().decode(parts[0])
            val expectedHash = Base64.getDecoder().decode(parts[1])
            
            val actualHash = hashPasswordWithSalt(password, salt)
            
            return MessageDigest.isEqual(expectedHash, actualHash)
        } catch (e: Exception) {
            return false
        }
    }
    
    /**
     * Hashea una contraseña con un salt específico
     */
    private fun hashPasswordWithSalt(password: String, salt: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        digest.update(salt)
        return digest.digest(password.toByteArray(Charsets.UTF_8))
    }
    
    /**
     * Valida que una contraseña cumpla con los requisitos mínimos
     * @param password Contraseña a validar
     * @return true si la contraseña es válida
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6 && password.length <= 100
    }
    
    /**
     * Obtiene mensaje de error para contraseña inválida
     */
    fun getPasswordValidationError(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña no puede estar vacía"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            password.length > 100 -> "La contraseña no puede tener más de 100 caracteres"
            else -> null
        }
    }
}