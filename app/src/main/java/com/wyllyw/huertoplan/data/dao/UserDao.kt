package com.wyllyw.huertoplan.data.dao

import androidx.room.*
import com.wyllyw.huertoplan.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): User?
    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?
    
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun countUsersByUsername(username: String): Int
    
    @Insert(onConflict = OnConflictStrategy.ABORT) // Cambio a ABORT para evitar duplicados
    suspend fun insertUser(user: User)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)
}