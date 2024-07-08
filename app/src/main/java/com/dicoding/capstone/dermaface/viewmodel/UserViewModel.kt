package com.dicoding.capstone.dermaface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.capstone.dermaface.repository.UserRepository
import com.google.firebase.auth.FirebaseUser

class UserViewModel(userRepository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    init {
        _user.value = userRepository.getCurrentUser()
    }
}
