package com.wyllyw.huertoplan.integration

import com.wyllyw.huertoplan.viewmodel.MainRepository
import com.wyllyw.huertoplan.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserFlowTest {

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
    fun `complete user flow should work correctly`() = runTest {
        // Given
        val userId = "test_user"

        // When - Login
        viewModel.setUser(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Verify user and terrains loaded
        assertTrue(viewModel.isUserLoggedIn())
        assertNotNull(viewModel.getCurrentUser())
        assertTrue(viewModel.terrains.value.isNotEmpty())

        // When - Select terrain
        val terrain = viewModel.getCurrentTerrain()
        assertNotNull(terrain)

        // Then - Verify sectors loaded
        assertTrue(viewModel.sectors.value.isNotEmpty())
        assertNotNull(viewModel.getCurrentSector())

        // When - Select sector
        val sector = viewModel.getCurrentSector()
        assertNotNull(sector)

        // Then - Verify bancales loaded
        assertTrue(viewModel.bancales.value.isNotEmpty())

        // When - Create new bancal
        val initialBancalesCount = viewModel.bancales.value.size
        viewModel.createBancal("Nuevo Bancal", 500f, 600f, 4f, 3f)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Verify bancal created
        assertEquals(initialBancalesCount + 1, viewModel.bancales.value.size)
        assertTrue(viewModel.bancales.value.any { it.name == "Nuevo Bancal" })

        // When - Update bancal position
        val bancal = viewModel.bancales.value.find { it.name == "Nuevo Bancal" }
        assertNotNull(bancal)
        viewModel.updateBancalPosition(bancal!!.id, 700f, 800f)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Verify position updated
        val updatedBancal = viewModel.bancales.value.find { it.id == bancal.id }
        assertEquals(700f, updatedBancal!!.x, 0.01f)
        assertEquals(800f, updatedBancal.y, 0.01f)
    }
} 