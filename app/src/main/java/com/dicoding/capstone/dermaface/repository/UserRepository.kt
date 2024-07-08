package com.dicoding.capstone.dermaface.repository

import com.dicoding.capstone.dermaface.data.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserRepository(
    val auth: FirebaseAuth,
    private val preferences: UserPreferences
) {

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun saveUserToken(token: String) {
        preferences.saveUserToken(token)
    }
}

