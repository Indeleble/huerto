package com.wyllyw.huertoplan.viewmodel

import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.model.Sector
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    private lateinit var viewModel: UserViewModel
    private lateinit var repository: MainRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = MainRepository()
        viewModel = UserViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setUser should load user and terrains`() = runTest {
        // Given
        val userId = "test_user"

        // When
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val user = viewModel.getCurrentUser()
        assertNotNull(user)
        assertEquals(userId, user?.id)

        val terrains = viewModel.terrains.value
        assertTrue(terrains.isNotEmpty())
        assertTrue(terrains.all { it.userId == userId })
    }

    @Test
    fun `loadTerrainsForUser should select first terrain automatically`() = runTest {
        // Given
        val userId = "test_user"

        // When
        viewModel.loadTerrainsForUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val selectedTerrain = viewModel.getCurrentTerrain()
        assertNotNull(selectedTerrain)
        assertEquals(userId, selectedTerrain?.userId)
    }

    @Test
    fun `selectTerrain should load sectors for that terrain`() = runTest {
        // Given
        val userId = "test_user"
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()
        val terrains = viewModel.terrains.value
        val terrain = terrains.first()

        // When
        viewModel.selectTerrain(terrain)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val selectedTerrain = viewModel.getCurrentTerrain()
        assertEquals(terrain.id, selectedTerrain?.id)

        val sectors = viewModel.sectors.value
        assertTrue(sectors.isNotEmpty())
        assertTrue(sectors.all { it.terrainId == terrain.id })
    }

    @Test
    fun `loadSectorsForTerrain should select first sector automatically`() = runTest {
        // Given
        val userId = "test_user"
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()
        val terrains = viewModel.terrains.value
        val terrain = terrains.first()

        // When
        viewModel.loadSectorsForTerrain(terrain.id)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val selectedSector = viewModel.getCurrentSector()
        assertNotNull(selectedSector)
        assertEquals(terrain.id, selectedSector?.terrainId)
    }

    @Test
    fun `selectSector should load bancales for that sector`() = runTest {
        // Given
        val userId = "test_user"
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()
        val terrains = viewModel.terrains.value
        val terrain = terrains.first()
        val sectors = viewModel.sectors.value
        val sector = sectors.first()

        // When
        viewModel.selectSector(sector)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val selectedSector = viewModel.getCurrentSector()
        assertEquals(sector.id, selectedSector?.id)

        val bancales = viewModel.bancales.value
        assertTrue(bancales.isNotEmpty())
        assertTrue(bancales.all { it.sectorId == sector.id })
    }

    @Test
    fun `createTerrain should add terrain to list`() = runTest {
        // Given
        val userId = "test_user"
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()
        val initialTerrainsCount = viewModel.terrains.value.size
        val newTerrainName = "Nuevo Terreno"

        // When
        viewModel.createTerrain(newTerrainName)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val terrains = viewModel.terrains.value
        assertEquals(initialTerrainsCount + 1, terrains.size)
        assertTrue(terrains.any { it.name == newTerrainName && it.userId == userId })
    }

    @Test
    fun `createSector should add sector to list`() = runTest {
        // Given
        val userId = "test_user"
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()
        val terrain = viewModel.getCurrentTerrain()
        assertNotNull(terrain)
        val initialSectorsCount = viewModel.sectors.value.size
        val newSectorName = "Nuevo Sector"

        // When
        viewModel.createSector(newSectorName)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val sectors = viewModel.sectors.value
        assertEquals(initialSectorsCount + 1, sectors.size)
        assertTrue(sectors.any { it.name == newSectorName && it.terrainId == terrain?.id })
    }

    @Test
    fun `createBancal should add bancal to list`() = runTest {
        // Given
        val userId = "test_user"
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()
        val sector = viewModel.getCurrentSector()
        assertNotNull(sector)
        val initialBancalesCount = viewModel.bancales.value.size
        val newBancalName = "Nuevo Bancal"
        val x = 150f
        val y = 250f
        val width = 4f
        val height = 3f

        // When
        viewModel.createBancal(newBancalName, x, y, width, height)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val bancales = viewModel.bancales.value
        assertEquals(initialBancalesCount + 1, bancales.size)
        assertTrue(bancales.any { 
            it.name == newBancalName && 
            it.sectorId == sector?.id &&
            it.x == x && it.y == y &&
            it.width == width && it.height == height
        })
    }

    @Test
    fun `updateBancalPosition should update bancal coordinates`() = runTest {
        // Given
        val userId = "test_user"
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()
        val bancales = viewModel.bancales.value
        assertTrue(bancales.isNotEmpty())
        val bancal = bancales.first()
        val newX = 300f
        val newY = 400f

        // When
        viewModel.updateBancalPosition(bancal.id, newX, newY)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val updatedBancales = viewModel.bancales.value
        val updatedBancal = updatedBancales.find { it.id == bancal.id }
        assertNotNull(updatedBancal)
        assertEquals(newX, updatedBancal!!.x, 0.01f)
        assertEquals(newY, updatedBancal.y, 0.01f)
    }

    @Test
    fun `deleteTerrain should remove terrain and clear related data`() = runTest {
        // Given
        val userId = "test_user"
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()
        val terrain = viewModel.getCurrentTerrain()
        assertNotNull(terrain)

        // When
        viewModel.deleteTerrain(terrain!!.id)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val terrains = viewModel.terrains.value
        assertFalse(terrains.contains(terrain))
        
        val selectedTerrain = viewModel.getCurrentTerrain()
        assertNull(selectedTerrain)
        
        val sectors = viewModel.sectors.value
        assertTrue(sectors.isEmpty())
        
        val bancales = viewModel.bancales.value
        assertTrue(bancales.isEmpty())
    }

    @Test
    fun `deleteSector should remove sector and clear bancales`() = runTest {
        // Given
        val userId = "test_user"
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()
        val sector = viewModel.getCurrentSector()
        assertNotNull(sector)

        // When
        viewModel.deleteSector(sector!!.id)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val sectors = viewModel.sectors.value
        assertFalse(sectors.contains(sector))
        
        val selectedSector = viewModel.getCurrentSector()
        assertNull(selectedSector)
        
        val bancales = viewModel.bancales.value
        assertTrue(bancales.isEmpty())
    }

    @Test
    fun `deleteBancal should remove bancal from list`() = runTest {
        // Given
        val userId = "test_user"
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()
        val bancales = viewModel.bancales.value
        assertTrue(bancales.isNotEmpty())
        val bancal = bancales.first()

        // When
        viewModel.deleteBancal(bancal.id)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val updatedBancales = viewModel.bancales.value
        assertFalse(updatedBancales.contains(bancal))
    }

    @Test
    fun `isUserLoggedIn should return true when user is set`() = runTest {
        // Given
        val userId = "test_user"

        // When
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.isUserLoggedIn())
    }

    @Test
    fun `isUserLoggedIn should return false when no user is set`() {
        // When & Then
        assertFalse(viewModel.isUserLoggedIn())
    }

    @Test
    fun `logout should clear all data`() = runTest {
        // Given
        val userId = "test_user"
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.isUserLoggedIn())

        // When
        viewModel.logout()

        // Then
        assertFalse(viewModel.isUserLoggedIn())
        assertNull(viewModel.getCurrentUser())
        assertNull(viewModel.getCurrentTerrain())
        assertNull(viewModel.getCurrentSector())
        assertTrue(viewModel.terrains.value.isEmpty())
        assertTrue(viewModel.sectors.value.isEmpty())
        assertTrue(viewModel.bancales.value.isEmpty())
    }
} 