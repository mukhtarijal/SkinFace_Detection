package com.dicoding.capstone.dermaface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone.dermaface.data.model.HistoryResponse
import com.dicoding.capstone.dermaface.repository.DetailHistoryRepository
import kotlinx.coroutines.launch

class DetailHistoryViewModel(private val repository: DetailHistoryRepository) : ViewModel() {

    private val _historyDetails = MutableLiveData<HistoryResponse>()
    val historyDetails: LiveData<HistoryResponse> get() = _historyDetails

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchHistoryDetails(historyId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.fetchHistoryDetails(historyId)
            result.onSuccess { history ->
                _historyDetails.value = history
                _isLoading.value = false
            }
            result.onFailure { exception ->
                _error.value = exception.message
                _isLoading.value = false
            }
        }
    }
}
