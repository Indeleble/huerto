package com.wyllyw.huertoplan.viewmodel

import androidx.lifecycle.ViewModel
import com.wyllyw.huertoplan.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(repository: MainRepository) : ViewModel() {

    private val _user = MutableStateFlow(repository.getUser())
    val user = _user.asStateFlow()
    val repo = repository
    init {
        _user.value = repository.getUser();
    }

    fun changeName(name: String) {
        val copy: User = _user.value.copy()
        copy.name = name
        _user.value = copy
    }
}