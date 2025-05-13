package com.wyllyw.huertoplan.viewmodel

import android.content.Context
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

    /**
     * Mocking internet call, in real case this function will fetch a new integer
     * from internet. And here we just mocked it by delaying 500 milli-seconds and
     * then returning a new random number.
     */
    suspend fun fetchNewNumber(): Int {
        delay(500)
        return Random().nextInt(1000)
    }

    fun getUser(name: String): User {
        return User(
            name,
            arrayListOf(
                Terrain(
                    "Terreno uno", "Asturias",
                    arrayListOf(
                        Sector(
                            "Sector 1",
                            arrayListOf(
                                Bancal(
                                    "Bancal 1",
                                    id = 1,
                                    x = 0f,
                                    y = 0f,
                                    width = 1f,
                                    height = 5f
                                )
                            ),
                        )
                    ),
                )
            ),
        )
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
                        id = id.toInt(),
                        nombre = nombre,
                        descripcion = descripcion
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
                        id = partes[0].toInt(),
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

    fun getFamiliaPorId(familiaId: Int): FamiliaPlanta? {
        return getFamilias().find { it.id == familiaId }
    }

    fun addUser(user: User) {
        // Implementación pendiente
    }
}