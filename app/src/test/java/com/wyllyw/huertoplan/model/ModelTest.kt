package com.wyllyw.huertoplan.model

import org.junit.Assert.*
import org.junit.Test

class ModelTest {

    @Test
    fun `User should have correct properties`() {
        // Given
        val id = "user123"
        val name = "Test User"

        // When
        val user = User(id, name)

        // Then
        assertEquals(id, user.id)
        assertEquals(name, user.name)
    }

    @Test
    fun `Terrain should have correct properties`() {
        // Given
        val id = "terrain123"
        val name = "Test Terrain"
        val userId = "user123"

        // When
        val terrain = Terrain(id, name, userId)

        // Then
        assertEquals(id, terrain.id)
        assertEquals(name, terrain.name)
        assertEquals(userId, terrain.userId)
    }

    @Test
    fun `Sector should have correct properties`() {
        // Given
        val id = "sector123"
        val name = "Test Sector"
        val terrainId = "terrain123"

        // When
        val sector = Sector(id, name, terrainId)

        // Then
        assertEquals(id, sector.id)
        assertEquals(name, sector.name)
        assertEquals(terrainId, sector.terrainId)
    }

    @Test
    fun `Bancal should have correct properties`() {
        // Given
        val id = "bancal123"
        val name = "Test Bancal"
        val sectorId = "sector123"
        val x = 100f
        val y = 200f
        val width = 3f
        val height = 2f

        // When
        val bancal = Bancal(id, name, sectorId, x, y, width, height)

        // Then
        assertEquals(id, bancal.id)
        assertEquals(name, bancal.name)
        assertEquals(sectorId, bancal.sectorId)
        assertEquals(x, bancal.x, 0.01f)
        assertEquals(y, bancal.y, 0.01f)
        assertEquals(width, bancal.width, 0.01f)
        assertEquals(height, bancal.height, 0.01f)
    }

    @Test
    fun `Bancal copy should create new instance with updated values`() {
        // Given
        val originalBancal = Bancal("bancal123", "Original", "sector123", 100f, 200f, 3f, 2f)

        // When
        val copiedBancal = originalBancal.copy(
            name = "Updated",
            x = 300f,
            y = 400f
        )

        // Then
        assertEquals(originalBancal.id, copiedBancal.id)
        assertEquals(originalBancal.sectorId, copiedBancal.sectorId)
        assertEquals(originalBancal.width, copiedBancal.width, 0.01f)
        assertEquals(originalBancal.height, copiedBancal.height, 0.01f)
        assertEquals("Updated", copiedBancal.name)
        assertEquals(300f, copiedBancal.x, 0.01f)
        assertEquals(400f, copiedBancal.y, 0.01f)
    }
} 