package com.dicoding.capstone.dermaface.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.capstone.dermaface.R
import com.dicoding.capstone.dermaface.data.UserPreferences
import com.dicoding.capstone.dermaface.databinding.ActivityLoginBinding
import com.dicoding.capstone.dermaface.repository.UserRepository
import com.dicoding.capstone.dermaface.viewmodel.LoginViewModel
import com.dicoding.capstone.dermaface.viewmodel.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(
            userRepository = UserRepository(FirebaseAuth.getInstance(), UserPreferences(this))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        googleSignInClient = configureGoogleSignIn()

        binding.btnLogin.setOnClickListener {
            signIn()
        }

        observeViewModel()
    }

    private fun configureGoogleSignIn(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(this, gso)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                account?.let { loginViewModel.firebaseAuthWithGoogle(it) }
            } catch (e: com.google.android.gms.common.api.ApiException) {
                Toast.makeText(this, getString(R.string.login_failed, e.message), Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun observeViewModel() {
        loginViewModel.authenticatedUser.observe(this) { user ->
            if (user != null) {
                loginViewModel.saveUserToken(
                    onSuccess = {
                        binding.progressBar.visibility = View.GONE
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onFailure = { errorMessage ->
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        loginViewModel.authError.observe(this) { error ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        loginViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}

