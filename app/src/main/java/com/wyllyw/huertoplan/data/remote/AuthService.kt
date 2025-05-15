package com.wyllyw.huertoplan.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.wyllyw.huertoplan.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) {
    suspend fun signInAnonymously(): Result<String> {
        return try {
            val result = auth.signInAnonymously().await()
            result.user?.uid?.let { uid ->
                // Crear o actualizar el usuario en la base de datos
                val userRef = database.getReference("users").child(uid)
                val user = User(uid, null)
                userRef.setValue(user).await()
                Result.success(uid)
            } ?: Result.failure(Exception("No se pudo obtener el ID del usuario"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): String? = auth.currentUser?.uid
} 