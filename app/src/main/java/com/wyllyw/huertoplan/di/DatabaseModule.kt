package com.wyllyw.huertoplan.di

import android.content.Context
import androidx.room.Room
import com.wyllyw.huertoplan.data.dao.*
import com.wyllyw.huertoplan.data.database.HuertoPlanDatabase
import com.wyllyw.huertoplan.domain.dispatcher.CoroutineDispatcherProvider
import com.wyllyw.huertoplan.domain.dispatcher.DefaultCoroutineDispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideHuertoPlanDatabase(@ApplicationContext context: Context): HuertoPlanDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            HuertoPlanDatabase::class.java,
            "huertoplan_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun provideUserDao(database: HuertoPlanDatabase): UserDao = database.userDao()
    
    @Provides
    fun provideTerrainDao(database: HuertoPlanDatabase): TerrainDao = database.terrainDao()
    
    @Provides
    fun provideSectorDao(database: HuertoPlanDatabase): SectorDao = database.sectorDao()
    
    @Provides
    fun provideBancalDao(database: HuertoPlanDatabase): BancalDao = database.bancalDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DispatcherModule {
    
    @Binds
    @Singleton
    abstract fun bindCoroutineDispatcherProvider(
        defaultCoroutineDispatcherProvider: DefaultCoroutineDispatcherProvider
    ): CoroutineDispatcherProvider
}