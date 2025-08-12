package com.wyllyw.huertoplan.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wyllyw.huertoplan.domain.usecase.bancal.CreateBancalUseCase
import com.wyllyw.huertoplan.domain.usecase.bancal.GetBancalesBySectorIdUseCase
import com.wyllyw.huertoplan.model.Bancal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BancalViewModel @Inject constructor(
    private val getBancalesBySectorIdUseCase: GetBancalesBySectorIdUseCase,
    private val createBancalUseCase: CreateBancalUseCase
) : ViewModel() {

    private val _bancales = MutableStateFlow<List<Bancal>>(emptyList())
    val bancales = _bancales.asStateFlow()

    private val _selectedBancal = MutableStateFlow<Bancal?>(null)
    val selectedBancal = _selectedBancal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadBancalesForSector(sectorId: String) {
        viewModelScope.launch {
            try {
                getBancalesBySectorIdUseCase(sectorId).collect { bancalList ->
                    _bancales.value = bancalList
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun createBancal(
        name: String,
        sectorId: String,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            createBancalUseCase(name, sectorId, x, y, width, height)
                .onSuccess { bancal ->
                    // La lista se actualizará automáticamente por el Flow
                    _selectedBancal.value = bancal
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }

    fun selectBancal(bancal: Bancal) {
        _selectedBancal.value = bancal
    }

    fun clearSelection() {
        _selectedBancal.value = null
    }

    fun clearError() {
        _error.value = null
    }
}