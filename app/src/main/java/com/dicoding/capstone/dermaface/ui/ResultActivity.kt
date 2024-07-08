package com.dicoding.capstone.dermaface.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.capstone.dermaface.R
import com.dicoding.capstone.dermaface.repository.ResultRepository
import com.dicoding.capstone.dermaface.viewmodel.ResultViewModel
import com.dicoding.capstone.dermaface.viewmodel.ViewModelFactory
import com.dicoding.capstone.dermaface.databinding.ActivityResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val resultViewModel: ResultViewModel by viewModels {
        ViewModelFactory(
            resultRepository = ResultRepository(
                FirebaseFirestore.getInstance(),
                FirebaseStorage.getInstance(),
                FirebaseAuth.getInstance()
            )
        )
    }
    private var isDataSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        val diagnosis = intent.getStringExtra(EXTRA_DIAGNOSIS)
        val recommendation = intent.getStringExtra(EXTRA_RECOMMENDATION)

        if (imageUri != null) {
            Glide.with(this)
                .load(imageUri)
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.ivResult)
        }

        binding.tvDiagnosis.text = diagnosis
        setRecommendationList(recommendation)

        binding.btnBack.setOnClickListener {
            navigateToMainActivity()
        }

        binding.btnSave.setOnClickListener {
            if (!isDataSaved && imageUri != null && diagnosis != null && recommendation != null) {
                binding.progressBar.visibility = View.VISIBLE
                resultViewModel.saveData(Uri.parse(imageUri), diagnosis, recommendation)
            } else {
                Toast.makeText(this, R.string.data_already_saved, Toast.LENGTH_SHORT).show()
            }
        }

        observeViewModel()
    }

    private fun setRecommendationList(recommendation: String?) {
        if (recommendation.isNullOrEmpty()) {
            binding.tvRecommendation.text = getString(R.string.no_recommendation)
        } else {
            binding.tvRecommendation.text = recommendation
        }
    }

    private fun observeViewModel() {
        resultViewModel.saveResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            result.onSuccess {
                Toast.makeText(this, R.string.data_saved_successfully, Toast.LENGTH_SHORT).show()
                isDataSaved = true
            }
            result.onFailure { exception ->
                Toast.makeText(this, getString(R.string.data_save_failed, exception.message), Toast.LENGTH_SHORT).show()
            }
        }

        resultViewModel.isSaving.observe(this) { isSaving ->
            binding.progressBar.visibility = if (isSaving) View.VISIBLE else View.GONE
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_DIAGNOSIS = "extra_diagnosis"
        const val EXTRA_RECOMMENDATION = "extra_recommendation"
    }
}
