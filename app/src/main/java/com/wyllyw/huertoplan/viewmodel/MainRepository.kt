package com.wyllyw.huertoplan.viewmodel

import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.model.Sector
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.User
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * By adding @Inject constructor(), we let hilt library knew that
 * how to make an instance of this class
 */
@Singleton
class MainRepository @Inject constructor() {

    // Datos mock en memoria
    private val users = mutableListOf<User>()
    private val terrains = mutableListOf<Terrain>()
    private val sectors = mutableListOf<Sector>()
    private val bancales = mutableListOf<Bancal>()

    // MARK: - Usuarios
    fun getUser(userId: String): User {
        // Buscar usuario existente o crear uno mock si no existe
        return users.find { it.id == userId } ?: createMockUser(userId)
    }

    private fun createMockUser(userId: String): User {
        val mockUser = User(userId, "Usuario Mock")
        users.add(mockUser)
        
        // Crear datos mock para este usuario
        createMockDataForUser(userId)
        
        return mockUser
    }

    // MARK: - Terrenos
    fun getTerrainsForUser(userId: String): List<Terrain> {
        return terrains.filter { it.userId == userId }
    }

    fun createTerrain(name: String, userId: String): Terrain {
        val newTerrain = Terrain(
            id = UUID.randomUUID().toString(),
            name = name,
            userId = userId
        )
        terrains.add(newTerrain)
        return newTerrain
    }

    fun deleteTerrain(terrainId: String) {
        terrains.removeAll { it.id == terrainId }
        // También eliminar sectores y bancales relacionados
        val sectorsToDelete = sectors.filter { it.terrainId == terrainId }
        sectorsToDelete.forEach { sector ->
            deleteSector(sector.id)
        }
    }

    // MARK: - Sectores
    fun getSectorsForTerrain(terrainId: String): List<Sector> {
        return sectors.filter { it.terrainId == terrainId }
    }

    fun createSector(name: String, terrainId: String): Sector {
        val newSector = Sector(
            id = UUID.randomUUID().toString(),
            name = name,
            terrainId = terrainId
        )
        sectors.add(newSector)
        return newSector
    }

    fun deleteSector(sectorId: String) {
        sectors.removeAll { it.id == sectorId }
        // También eliminar bancales relacionados
        bancales.removeAll { it.sectorId == sectorId }
    }

    // MARK: - Bancales
    fun getBancalesForSector(sectorId: String): List<Bancal> {
        return bancales.filter { it.sectorId == sectorId }
    }

    fun createBancal(name: String, sectorId: String, x: Float, y: Float, width: Float, height: Float): Bancal {
        val newBancal = Bancal(
            id = UUID.randomUUID().toString(),
            name = name,
            sectorId = sectorId,
            x = x,
            y = y,
            width = width,
            height = height
        )
        bancales.add(newBancal)
        return newBancal
    }

    fun updateBancalPosition(bancalId: String, newX: Float, newY: Float) {
        val bancal = bancales.find { it.id == bancalId }
        bancal?.let {
            val index = bancales.indexOf(it)
            bancales[index] = it.copy(x = newX, y = newY)
        }
    }

    fun deleteBancal(bancalId: String) {
        bancales.removeAll { it.id == bancalId }
    }

    // MARK: - Setup de datos mock para un usuario específico
    private fun createMockDataForUser(userId: String) {
        // Crear terrenos mock para este usuario
        val mockTerrain1 = Terrain("terrain1_$userId", "Mi Huerto", userId)
        val mockTerrain2 = Terrain("terrain2_$userId", "Huerto Familiar", userId)
        terrains.addAll(listOf(mockTerrain1, mockTerrain2))

        // Crear sectores mock
        val mockSector1 = Sector("sector1_$userId", "Sector Norte", "terrain1_$userId")
        val mockSector2 = Sector("sector2_$userId", "Sector Sur", "terrain1_$userId")
        sectors.addAll(listOf(mockSector1, mockSector2))

        // Crear bancales mock
        val mockBancal1 = Bancal("bancal1_$userId", "Bancal 1", "sector1_$userId", 100f, 100f, 1f, 4f)
        val mockBancal2 = Bancal("bancal2_$userId", "Bancal 2", "sector1_$userId", 300f, 100f, 1f, 7f)
        val mockBancal3 = Bancal("bancal3_$userId", "Bancal 3", "sector2_$userId", 100f, 400f, 1f, 3f)
        bancales.addAll(listOf(mockBancal1, mockBancal2, mockBancal3))
    }
}