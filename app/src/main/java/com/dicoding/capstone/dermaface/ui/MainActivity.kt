package com.dicoding.capstone.dermaface.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.capstone.dermaface.R
import com.dicoding.capstone.dermaface.adapter.ArticleAdapter
import com.dicoding.capstone.dermaface.data.UserPreferences
import com.dicoding.capstone.dermaface.databinding.ActivityMainBinding
import com.dicoding.capstone.dermaface.repository.ArticleRepository
import com.dicoding.capstone.dermaface.repository.UserRepository
import com.dicoding.capstone.dermaface.utils.ImageHandler
import com.dicoding.capstone.dermaface.viewmodel.ArticleViewModel
import com.dicoding.capstone.dermaface.viewmodel.MainViewModel
import com.dicoding.capstone.dermaface.viewmodel.UserViewModel
import com.dicoding.capstone.dermaface.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory(userRepository = UserRepository(FirebaseAuth.getInstance(), UserPreferences(this)))
    }
    private val articleViewModel: ArticleViewModel by viewModels {
        ViewModelFactory(articleRepository = ArticleRepository())
    }
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var imageHandler: ImageHandler
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        setSupportActionBar(binding.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageHandler = ImageHandler(this, mainViewModel)

        observeViewModels()
        setupRecyclerView()
        setupButtonListeners()
    }

    private fun observeViewModels() {
        userViewModel.user.observe(this) { user ->
            user?.let {
                supportActionBar?.title = getString(R.string.greeting, user.displayName)
            } ?: run {
                navigateToLogin()
            }
        }

        articleViewModel.articles.observe(this) { articles ->
            articleAdapter.updateArticles(articles)
            binding.progressBar.visibility = View.GONE
        }

        articleViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        mainViewModel.imageUri.observe(this) { uri ->
            uri?.let {
                imageHandler.startCrop(it)
                // Set imageUri to null to prevent continuous cropping
                mainViewModel.setImageUri(null)
            }
        }
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(this, emptyList())
        binding.rvArticles.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = articleAdapter
        }
    }

    private fun setupButtonListeners() {
        binding.btnScan.setOnClickListener {
            imageHandler.showImageSourceDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            R.id.action_logout -> {
                showLogoutConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.logout_confirmation_title)
            .setMessage(R.string.logout_confirmation_message)
            .setPositiveButton(R.string.logout) { dialog, _ ->
                signOut()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.setOnShowListener {
            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        }

        alertDialog.show()
    }

    private fun signOut() {
        lifecycleScope.launch {
            val credentialManager = androidx.credentials.CredentialManager.create(this@MainActivity)
            Firebase.auth.signOut()
            credentialManager.clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                // Menyimpan hasil cropping untuk penggunaan lebih lanjut
                val intent = Intent(this, ScanActivity::class.java).apply {
                    putExtra(ScanActivity.EXTRA_IMAGE_URI, it.toString())
                }
                startActivity(intent)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, R.string.crop_error, Toast.LENGTH_SHORT).show()
        }
    }
}
