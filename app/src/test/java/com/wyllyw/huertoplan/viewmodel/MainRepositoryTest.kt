package com.wyllyw.huertoplan.viewmodel

import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.model.Sector
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MainRepositoryTest {

    private lateinit var repository: MainRepository

    @Before
    fun setUp() {
        repository = MainRepository()
    }

    @Test
    fun `getUser should create mock user when user does not exist`() {
        // Given
        val userId = "test_user_123"

        // When
        val user = repository.getUser(userId)

        // Then
        assertNotNull(user)
        assertEquals(userId, user.id)
        assertEquals("Usuario Mock", user.name)
    }

    @Test
    fun `getUser should return existing user when user exists`() {
        // Given
        val userId = "existing_user"
        val existingUser = User(userId, "Usuario Existente")
        repository.getUser(userId) // Esto crea el usuario

        // When
        val retrievedUser = repository.getUser(userId)

        // Then
        assertEquals(existingUser.id, retrievedUser.id)
        assertEquals(existingUser.name, retrievedUser.name)
    }

    @Test
    fun `getTerrainsForUser should return terrains for specific user`() {
        // Given
        val userId = "test_user"
        repository.getUser(userId) // Esto crea datos mock

        // When
        val terrains = repository.getTerrainsForUser(userId)

        // Then
        assertNotNull(terrains)
        assertTrue(terrains.isNotEmpty())
        assertTrue(terrains.all { it.userId == userId })
    }

    @Test
    fun `createTerrain should add new terrain to repository`() {
        // Given
        val userId = "test_user"
        val terrainName = "Mi Nuevo Terreno"

        // When
        val newTerrain = repository.createTerrain(terrainName, userId)
        val terrains = repository.getTerrainsForUser(userId)

        // Then
        assertNotNull(newTerrain)
        assertEquals(terrainName, newTerrain.name)
        assertEquals(userId, newTerrain.userId)
        assertTrue(terrains.contains(newTerrain))
    }

    @Test
    fun `deleteTerrain should remove terrain and related data`() {
        // Given
        val userId = "test_user"
        repository.getUser(userId) // Crear datos mock
        val terrains = repository.getTerrainsForUser(userId)
        val terrainToDelete = terrains.first()

        // When
        repository.deleteTerrain(terrainToDelete.id)
        val remainingTerrains = repository.getTerrainsForUser(userId)

        // Then
        assertFalse(remainingTerrains.contains(terrainToDelete))
    }

    @Test
    fun `getSectorsForTerrain should return sectors for specific terrain`() {
        // Given
        val userId = "test_user"
        repository.getUser(userId) // Crear datos mock
        val terrains = repository.getTerrainsForUser(userId)
        val terrain = terrains.first()

        // When
        val sectors = repository.getSectorsForTerrain(terrain.id)

        // Then
        assertNotNull(sectors)
        assertTrue(sectors.isNotEmpty())
        assertTrue(sectors.all { it.terrainId == terrain.id })
    }

    @Test
    fun `createSector should add new sector to repository`() {
        // Given
        val userId = "test_user"
        repository.getUser(userId) // Crear datos mock
        val terrains = repository.getTerrainsForUser(userId)
        val terrain = terrains.first()
        val sectorName = "Nuevo Sector"

        // When
        val newSector = repository.createSector(sectorName, terrain.id)
        val sectors = repository.getSectorsForTerrain(terrain.id)

        // Then
        assertNotNull(newSector)
        assertEquals(sectorName, newSector.name)
        assertEquals(terrain.id, newSector.terrainId)
        assertTrue(sectors.contains(newSector))
    }

    @Test
    fun `getBancalesForSector should return bancales for specific sector`() {
        // Given
        val userId = "test_user"
        repository.getUser(userId) // Crear datos mock
        val terrains = repository.getTerrainsForUser(userId)
        val terrain = terrains.first()
        val sectors = repository.getSectorsForTerrain(terrain.id)
        val sector = sectors.first()

        // When
        val bancales = repository.getBancalesForSector(sector.id)

        // Then
        assertNotNull(bancales)
        assertTrue(bancales.isNotEmpty())
        assertTrue(bancales.all { it.sectorId == sector.id })
    }

    @Test
    fun `createBancal should add new bancal to repository`() {
        // Given
        val userId = "test_user"
        repository.getUser(userId) // Crear datos mock
        val terrains = repository.getTerrainsForUser(userId)
        val terrain = terrains.first()
        val sectors = repository.getSectorsForTerrain(terrain.id)
        val sector = sectors.first()
        val bancalName = "Nuevo Bancal"
        val x = 100f
        val y = 200f
        val width = 3f
        val height = 2f

        // When
        val newBancal = repository.createBancal(bancalName, sector.id, x, y, width, height)
        val bancales = repository.getBancalesForSector(sector.id)

        // Then
        assertNotNull(newBancal)
        assertEquals(bancalName, newBancal.name)
        assertEquals(sector.id, newBancal.sectorId)
        assertEquals(x, newBancal.x, 0.01f)
        assertEquals(y, newBancal.y, 0.01f)
        assertEquals(width, newBancal.width, 0.01f)
        assertEquals(height, newBancal.height, 0.01f)
        assertTrue(bancales.contains(newBancal))
    }

    @Test
    fun `updateBancalPosition should update bancal coordinates`() {
        // Given
        val userId = "test_user"
        repository.getUser(userId) // Crear datos mock
        val terrains = repository.getTerrainsForUser(userId)
        val terrain = terrains.first()
        val sectors = repository.getSectorsForTerrain(terrain.id)
        val sector = sectors.first()
        val bancales = repository.getBancalesForSector(sector.id)
        val bancal = bancales.first()
        val newX = 500f
        val newY = 600f

        // When
        repository.updateBancalPosition(bancal.id, newX, newY)
        val updatedBancales = repository.getBancalesForSector(sector.id)
        val updatedBancal = updatedBancales.find { it.id == bancal.id }

        // Then
        assertNotNull(updatedBancal)
        assertEquals(newX, updatedBancal!!.x, 0.01f)
        assertEquals(newY, updatedBancal.y, 0.01f)
    }

    @Test
    fun `deleteBancal should remove bancal from repository`() {
        // Given
        val userId = "test_user"
        repository.getUser(userId) // Crear datos mock
        val terrains = repository.getTerrainsForUser(userId)
        val terrain = terrains.first()
        val sectors = repository.getSectorsForTerrain(terrain.id)
        val sector = sectors.first()
        val bancales = repository.getBancalesForSector(sector.id)
        val bancalToDelete = bancales.first()

        // When
        repository.deleteBancal(bancalToDelete.id)
        val remainingBancales = repository.getBancalesForSector(sector.id)

        // Then
        assertFalse(remainingBancales.contains(bancalToDelete))
    }
} 