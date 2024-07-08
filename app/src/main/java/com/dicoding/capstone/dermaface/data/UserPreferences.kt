package com.dicoding.capstone.dermaface.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_TOKEN = "user_token"
    }

    fun saveUserToken(token: String) {
        prefs.edit().putString(KEY_USER_TOKEN, token).apply()
    }

    fun getUserToken(): String? {
        return prefs.getString(KEY_USER_TOKEN, null)
    }
}
