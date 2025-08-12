package com.wyllyw.huertoplan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wyllyw.huertoplan.domain.usecase.user.CreateUserUseCase
import com.wyllyw.huertoplan.domain.usecase.user.GetUserByIdUseCase
import com.wyllyw.huertoplan.domain.usecase.user.LoginUseCase
import com.wyllyw.huertoplan.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

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
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            createUserUseCase(name, username, password)
                .onSuccess { user ->
                    _user.value = user
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            loginUseCase(username, password)
                .onSuccess { user ->
                    _user.value = user
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
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