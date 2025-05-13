package com.wyllyw.huertoplan.repository

import android.content.Context
import com.wyllyw.huertoplan.model.FamiliaPlanta
import com.wyllyw.huertoplan.model.PlantaHorticola
import java.io.BufferedReader

class PlantasRepository(private val context: Context) {
    
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
                        variedad = partes[2],
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
} 