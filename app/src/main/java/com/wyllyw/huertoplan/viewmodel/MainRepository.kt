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
        return User(
            id = "iduser",
            name,
            arrayListOf(
                Terrain(
                    "TerrenoID",
                    "Terreno uno", "Asturias",
                    arrayListOf(
                        Sector(
                            "SectorID",
                            "Sector 1",
                            arrayListOf(
                                Bancal(
                                    name = "Bancal 1",
                                    id = "IDDEBANCAL",
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