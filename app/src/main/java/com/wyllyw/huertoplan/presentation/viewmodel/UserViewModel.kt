package com.wyllyw.huertoplan.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wyllyw.huertoplan.domain.usecase.user.CreateUserUseCase
import com.wyllyw.huertoplan.domain.usecase.user.GetUserByIdUseCase
import com.wyllyw.huertoplan.domain.usecase.user.LoginUseCase
import com.wyllyw.huertoplan.domain.usecase.user.InitializeUserDataUseCase
import com.wyllyw.huertoplan.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val loginUseCase: LoginUseCase,
    private val initializeUserDataUseCase: InitializeUserDataUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "UserViewModel"
    }

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun getUserById(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            getUserByIdUseCase(userId)
                .onSuccess { user ->
                    _user.value = user
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }

    fun createUser(name: String, username: String, password: String) {
        Log.d(TAG, "createUser called with name: $name, username: $username")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            createUserUseCase(name, username, password)
                .onSuccess { user ->
                    Log.d(TAG, "User created successfully: ${user.name} (id: ${user.id})")
                    // Inicializar datos por defecto ANTES de establecer el usuario
                    // Esto asegura que los datos existan cuando se navegue a BancalesScreen
                    try {
                        Log.d(TAG, "Initializing default data for user: ${user.id}")
                        initializeUserDataUseCase(user.id)
                        // Añadir pequeño delay para asegurar que Room ha procesado las inserciones
                        delay(200)
                        Log.d(TAG, "Setting user value in StateFlow")
                        _user.value = user
                        Log.d(TAG, "User registration flow completed successfully")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error initializing user data: ${e.message}", e)
                        _error.value = "Error al inicializar datos: ${e.message}"
                    }
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error creating user: ${exception.message}", exception)
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }
    
    fun login(username: String, password: String) {
        Log.d(TAG, "login called with username: $username")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            loginUseCase(username, password)
                .onSuccess { user ->
                    Log.d(TAG, "Login successful for user: ${user.name} (id: ${user.id})")
                    _user.value = user
                    // Mostrar información completa del usuario después del login
                    logUserCompleteInfo(user)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Login failed: ${exception.message}", exception)
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }
    
    private suspend fun logUserCompleteInfo(user: User) {
        try {
            Log.d(TAG, "=== USER COMPLETE INFO START ===")
            Log.d(TAG, "User: ${user.name} (username: ${user.username}, id: ${user.id})")
            // Esta información se mostrará cuando BancalViewModel cargue los datos
            Log.d(TAG, "BancalViewModel should now load terrains and sectors for user: ${user.id}")
            Log.d(TAG, "=== USER COMPLETE INFO END ===")
        } catch (e: Exception) {
            Log.e(TAG, "Error logging user info: ${e.message}", e)
        }
    }

    fun isUserLoggedIn(): Boolean = _user.value != null

    fun logout() {
        _user.value = null
        _error.value = null
    }

    fun clearError() {
        _error.value = null
    }
}