package com.dicoding.capstone.dermaface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.capstone.dermaface.R
import com.dicoding.capstone.dermaface.repository.UserRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _authenticatedUser = MutableLiveData<FirebaseUser?>()
    val authenticatedUser: LiveData<FirebaseUser?> get() = _authenticatedUser

    private val _authError = MutableLiveData<String>()
    val authError: LiveData<String> get() = _authError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        _isLoading.value = true
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        userRepository.auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful) {
                    _authenticatedUser.value = userRepository.getCurrentUser()
                } else {
                    _authError.value = (task.exception?.localizedMessage ?: R.string.auth_error).toString()
                }
            }
    }

    fun saveUserToken(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        _isLoading.value = true
        userRepository.getCurrentUser()?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
            _isLoading.value = false
            if (tokenTask.isSuccessful) {
                val token = tokenTask.result?.token.orEmpty()
                userRepository.saveUserToken(token)
                onSuccess()
            } else {
                onFailure((tokenTask.exception?.localizedMessage ?: R.string.token_error).toString())
            }
        }
    }
}
