package com.dicoding.capstone.dermaface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone.dermaface.data.model.HistoryResponse
import com.dicoding.capstone.dermaface.repository.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: HistoryRepository) : ViewModel() {

    private val _histories = MutableLiveData<List<HistoryResponse>?>()
    val histories: MutableLiveData<List<HistoryResponse>?> get() = _histories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _deletionStatus = MutableLiveData<Result<Unit>>()
    val deletionStatus: LiveData<Result<Unit>> get() = _deletionStatus

    fun fetchHistories() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.fetchHistories()
            _histories.value = result.getOrElse { emptyList() }
            _isLoading.value = false
        }
    }

    fun deleteHistory(history: HistoryResponse) {
        viewModelScope.launch {
            val result = repository.deleteHistory(history.id)
            _deletionStatus.value = result
            if (result.isSuccess) {
                _histories.value = _histories.value?.filter { it.id != history.id }
            }
        }
    }
}
