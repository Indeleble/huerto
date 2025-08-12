package com.wyllyw.huertoplan.domain.model

sealed class HuertoPlanError : Exception() {
    
    // User related errors
    data class UserNotFound(val userId: String) : HuertoPlanError() {
        override val message: String = "Usuario con ID '$userId' no encontrado"
    }
    
    data class InvalidUserName(val name: String) : HuertoPlanError() {
        override val message: String = "Nombre de usuario inválido: '$name'"
    }
    
    // Terrain related errors
    data class TerrainNotFound(val terrainId: String) : HuertoPlanError() {
        override val message: String = "Terreno con ID '$terrainId' no encontrado"
    }
    
    data class InvalidTerrainName(val name: String) : HuertoPlanError() {
        override val message: String = "Nombre de terreno inválido: '$name'"
    }
    
    // Sector related errors
    data class SectorNotFound(val sectorId: String) : HuertoPlanError() {
        override val message: String = "Sector con ID '$sectorId' no encontrado"
    }
    
    data class InvalidSectorName(val name: String) : HuertoPlanError() {
        override val message: String = "Nombre de sector inválido: '$name'"
    }
    
    // Bancal related errors
    data class BancalNotFound(val bancalId: String) : HuertoPlanError() {
        override val message: String = "Bancal con ID '$bancalId' no encontrado"
    }
    
    data class InvalidBancalName(val name: String) : HuertoPlanError() {
        override val message: String = "Nombre de bancal inválido: '$name'"
    }
    
    data class InvalidBancalDimensions(val width: Float, val height: Float) : HuertoPlanError() {
        override val message: String = "Dimensiones de bancal inválidas: ancho=$width, alto=$height"
    }
    
    // Database related errors
    object DatabaseError : HuertoPlanError() {
        override val message: String = "Error en la base de datos"
    }
    
    data class DatabaseConstraintViolation(val constraint: String) : HuertoPlanError() {
        override val message: String = "Violación de restricción de base de datos: $constraint"
    }
    
    // Network related errors
    object NetworkError : HuertoPlanError() {
        override val message: String = "Error de conexión de red"
    }
    
    // Generic validation error
    data class ValidationError(val field: String, val reason: String) : HuertoPlanError() {
        override val message: String = "Error de validación en '$field': $reason"
    }
    
    // Unknown error

    data class UnknownError(override val cause: Throwable?) : HuertoPlanError() {
        override val message: String = "Error desconocido: ${cause?.message ?: "Sin detalles"}"
    }
}