package com.wyllyw.huertoplan.viewmodel

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.wyllyw.huertoplan.model.Bancal
import com.wyllyw.huertoplan.model.FamiliaPlanta
import com.wyllyw.huertoplan.model.PlantaHorticola
import com.wyllyw.huertoplan.model.Sector
import com.wyllyw.huertoplan.model.Terrain
import com.wyllyw.huertoplan.model.User
import kotlinx.coroutines.delay
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

/**
 * By adding @Inject constructor(), we let hilt library knew that
 * how to make an instance of this class
 */
@Singleton
class MainRepository @Inject constructor(private val context: Context) {

    val db = FirebaseDatabase.getInstance()

    fun getUser(name: String): User {
        // Crear el usuario con su lista de IDs de terrenos
        val userId = "iduser"
        val terreno1Id = "TerrenoID"
        val sector1Id = "SectorID"
        val bancal1Id = "IDDEBANCAL"
        
        // Guardamos referencias a cada objeto usando sus IDs
        val user = User(
            id = userId,
            name = name,
            terrainsIds = listOf(terreno1Id)
        )
        
        // Aquí simularíamos guardar cada entidad por separado en la base de datos
        // Terreno
        val terreno = Terrain(
            id = terreno1Id,
            name = "Terreno uno",
            Location = "Asturias",
            userId = userId,
            sectorsIds = listOf(sector1Id)
        )
        
        // Sector
        val sector = Sector(
            id = sector1Id,
            name = "Sector 1",
            terrainId = terreno1Id,
            bancalesIds = listOf(bancal1Id)
        )
        
        // Bancal
        val bancal = Bancal(
            id = bancal1Id,
            name = "Bancal 1",
            sectorId = sector1Id,
            x = 0f,
            y = 0f,
            width = 1f,
            height = 5f
        )
        
        // En una implementación real, estos objetos se guardarían en la base de datos
        // y se recuperarían por sus IDs
        
        return user
    }

    // Función auxiliar para recuperar todos los terrenos asociados a un usuario
    fun getTerrainsByUserId(userId: String): List<Terrain> {
        // En una implementación real, esto buscaría en la BD los terrenos por userId
        return if (userId == "iduser") {
            listOf(
                Terrain(
                    id = "TerrenoID",
                    name = "Terreno uno",
                    Location = "Asturias",
                    userId = userId,
                    sectorsIds = listOf("SectorID")
                )
            )
        } else {
            emptyList()
        }
    }
    
    // Función auxiliar para recuperar todos los sectores asociados a un terreno
    fun getSectorsByTerrainId(terrainId: String): List<Sector> {
        // En una implementación real, esto buscaría en la BD los sectores por terrainId
        return if (terrainId == "TerrenoID") {
            listOf(
                Sector(
                    id = "SectorID",
                    name = "Sector 1",
                    terrainId = terrainId,
                    bancalesIds = listOf("IDDEBANCAL")
                )
            )
        } else {
            emptyList()
        }
    }
    
    // Función auxiliar para recuperar todos los bancales asociados a un sector
    fun getBancalesBySectorId(sectorId: String): List<Bancal> {
        // En una implementación real, esto buscaría en la BD los bancales por sectorId
        return if (sectorId == "SectorID") {
            listOf(
                Bancal(
                    id = "IDDEBANCAL",
                    name = "Bancal 1",
                    sectorId = sectorId,
                    x = 0f,
                    y = 0f,
                    width = 1f,
                    height = 5f
                )
            )
        } else {
            emptyList()
        }
    }

    fun getFamilias(): List<FamiliaPlanta> {
        val familias = mutableListOf<FamiliaPlanta>()
        context.assets.open("familias_plantas.csv").bufferedReader().use { reader ->
            // Saltar la línea de encabezados
            reader.readLine()

            reader.forEachLine { line ->
                val (id, nombre, descripcion) = line.split(",")
                familias.add(
                    FamiliaPlanta(
                        id = id.toString(), nombre = nombre, descripcion = descripcion
                    )
                )
            }
        }
        return familias
    }

    fun getPlantas(): List<PlantaHorticola> {
        val plantas = mutableListOf<PlantaHorticola>()
        context.assets.open("plantas_horticolas.csv").bufferedReader().use { reader ->
            // Saltar la línea de encabezados
            reader.readLine()

            reader.forEachLine { line ->
                val partes = line.split(",")
                plantas.add(
                    PlantaHorticola(
                        id = partes[0].toString(),
                        nombreComun = partes[1],
                        variedad = if (partes[2].isBlank()) null else partes[2],
                        familiaId = partes[3].toInt(),
                        nombreCientifico = partes[4],
                        tiempoRecoleccion = partes[5].toInt()
                    )
                )
            }
        }
        return plantas
    }

    fun getPlantasPorFamilia(familiaId: Int): List<PlantaHorticola> {
        return getPlantas().filter { it.familiaId == familiaId }
    }

    fun getFamiliaPorId(familiaId: String): FamiliaPlanta? {
        return getFamilias().find { it.id == familiaId }
    }

    fun addUser(user: User) {
        // Implementación pendiente
    }
}