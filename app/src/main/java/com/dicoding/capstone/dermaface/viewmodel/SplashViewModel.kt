package com.dicoding.capstone.dermaface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.capstone.dermaface.repository.SplashRepository
import com.dicoding.capstone.dermaface.ui.LoginActivity
import com.dicoding.capstone.dermaface.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(private val repository: SplashRepository) : ViewModel() {

    private val _navigateTo = MutableLiveData<Class<*>>()
    val navigateTo: LiveData<Class<*>> get() = _navigateTo

    fun checkUserStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000)
            if (isActive) {
                val targetActivity = if (repository.getUserToken() != null && repository.isUserLoggedIn()) {
                    MainActivity::class.java
                } else {
                    LoginActivity::class.java
                }
                withContext(Dispatchers.Main) {
                    _navigateTo.value = targetActivity
                }
            }
        }
    }
}
