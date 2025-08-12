package com.wyllyw.huertoplan.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.wyllyw.huertoplan.data.dao.*
import com.wyllyw.huertoplan.model.*

@Database(
    entities = [User::class, Terrain::class, Sector::class, Bancal::class],
    version = 1,
    exportSchema = false
)
abstract class HuertoPlanDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun terrainDao(): TerrainDao
    abstract fun sectorDao(): SectorDao
    abstract fun bancalDao(): BancalDao
    
    companion object {
        @Volatile
        private var INSTANCE: HuertoPlanDatabase? = null
        
        fun getDatabase(context: Context): HuertoPlanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HuertoPlanDatabase::class.java,
                    "huertoplan_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}