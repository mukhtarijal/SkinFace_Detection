package com.dicoding.capstone.dermaface.repository

import com.dicoding.capstone.dermaface.data.UserPreferences
import com.google.firebase.auth.FirebaseAuth

class SplashRepository(
    private val userPreferences: UserPreferences,
    private val auth: FirebaseAuth
) {
    fun getUserToken(): String? = userPreferences.getUserToken()

    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}
