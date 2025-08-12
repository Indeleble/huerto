package com.wyllyw.huertoplan.domain.validation

object ValidationUtils {
    
    const val MIN_NAME_LENGTH = 1
    const val MAX_NAME_LENGTH = 50
    const val MIN_DIMENSION = 0.1f
    const val MAX_DIMENSION = 1000f
    
    fun validateName(name: String, fieldName: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Invalid("$fieldName no puede estar vacío")
            name.length < MIN_NAME_LENGTH -> ValidationResult.Invalid("$fieldName debe tener al menos $MIN_NAME_LENGTH carácter")
            name.length > MAX_NAME_LENGTH -> ValidationResult.Invalid("$fieldName no puede tener más de $MAX_NAME_LENGTH caracteres")
            name.trim() != name -> ValidationResult.Invalid("$fieldName no puede empezar o terminar con espacios")
            else -> ValidationResult.Valid
        }
    }
    
    fun validateId(id: String, fieldName: String): ValidationResult {
        return when {
            id.isBlank() -> ValidationResult.Invalid("$fieldName no puede estar vacío")
            else -> ValidationResult.Valid
        }
    }
    
    fun validateDimension(dimension: Float, fieldName: String): ValidationResult {
        return when {
            dimension <= 0 -> ValidationResult.Invalid("$fieldName debe ser positivo")
            dimension < MIN_DIMENSION -> ValidationResult.Invalid("$fieldName debe ser al menos $MIN_DIMENSION")
            dimension > MAX_DIMENSION -> ValidationResult.Invalid("$fieldName no puede ser mayor a $MAX_DIMENSION")
            dimension.isNaN() -> ValidationResult.Invalid("$fieldName no es un número válido")
            dimension.isInfinite() -> ValidationResult.Invalid("$fieldName no puede ser infinito")
            else -> ValidationResult.Valid
        }
    }
    
    fun validateCoordinate(coordinate: Float, fieldName: String): ValidationResult {
        return when {
            coordinate < 0 -> ValidationResult.Invalid("$fieldName no puede ser negativo")
            coordinate.isNaN() -> ValidationResult.Invalid("$fieldName no es un número válido")
            coordinate.isInfinite() -> ValidationResult.Invalid("$fieldName no puede ser infinito")
            else -> ValidationResult.Valid
        }
    }
    
    fun validateMultiple(vararg validations: ValidationResult): ValidationResult {
        val errors = validations.filterIsInstance<ValidationResult.Invalid>()
        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors.joinToString("; ") { it.message })
        }
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
    
    val isValid: Boolean get() = this is Valid
    val isInvalid: Boolean get() = this is Invalid
}