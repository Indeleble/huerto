package com.wyllyw.huertoplan.viewmodel

import com.wyllyw.huertoplan.model.Bancal
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
class MainRepository @Inject constructor() {

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
            name, arrayListOf(
                Terrain(
                    "Terreno uno", "Asturias", arrayListOf(
                        Sector(
                            "Sector 1", arrayListOf(
                                Bancal("Bancal 1"),
                                Bancal("Bancal 2"),
                                Bancal("Bancal 3")
                            )
                        ),
                        Sector(
                            "Sector 2", arrayListOf(
                                Bancal("Bancal 1"),
                                Bancal("Bancal 2"),
                                Bancal("Bancal 3")
                            )
                        )
                    )
                ),
                Terrain(
                    "Terreno dos", "Salamanca", arrayListOf(
                        Sector(
                            "Sector 1", arrayListOf(
                                Bancal("Bancal 1"),
                                Bancal("Bancal 2"),
                                Bancal("Bancal 3")
                            )
                        ),
                        Sector(
                            "Sector 2", arrayListOf(
                                Bancal("Bancal 1"),
                                Bancal("Bancal 2"),
                                Bancal("Bancal 3")
                            )
                        )
                    )
                )
            )

        )
    }
}