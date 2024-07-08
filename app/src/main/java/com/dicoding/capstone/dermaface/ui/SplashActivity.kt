package com.dicoding.capstone.dermaface.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.capstone.dermaface.R
import com.dicoding.capstone.dermaface.repository.SplashRepository
import com.dicoding.capstone.dermaface.viewmodel.SplashViewModel
import com.dicoding.capstone.dermaface.data.UserPreferences
import com.dicoding.capstone.dermaface.viewmodel.ViewModelFactory
import com.dicoding.capstone.dermaface.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashViewModel: SplashViewModel by viewModels {
        ViewModelFactory(
            splashRepository = SplashRepository(
                UserPreferences(this),
                FirebaseAuth.getInstance()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        observeNavigation()
        splashViewModel.checkUserStatus()
    }

    override fun onStop() {
        super.onStop()
        splashViewModel.navigateTo.removeObservers(this)
    }

    private fun observeNavigation() {
        splashViewModel.navigateTo.observe(this) { activityClass ->
            startActivity(Intent(this, activityClass))
            finish()
        }
    }
}
