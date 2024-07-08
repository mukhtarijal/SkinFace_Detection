package com.dicoding.capstone.dermaface.ui

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
import com.dicoding.capstone.dermaface.databinding.ActivityDetailHistoryBinding
import com.dicoding.capstone.dermaface.repository.DetailHistoryRepository
import com.dicoding.capstone.dermaface.viewmodel.DetailHistoryViewModel
import com.dicoding.capstone.dermaface.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    private val detailHistoryViewModel: DetailHistoryViewModel by viewModels {
        ViewModelFactory(
            detailHistoryRepository = DetailHistoryRepository(
                FirebaseFirestore.getInstance(),
                FirebaseAuth.getInstance()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val historyId = intent.getStringExtra("HISTORY_ID")
        if (historyId != null) {
            detailHistoryViewModel.fetchHistoryDetails(historyId)
        } else {
            Toast.makeText(this, R.string.no_history_data, Toast.LENGTH_SHORT).show()
            finish()
        }

        observeViewModel()

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        detailHistoryViewModel.historyDetails.observe(this) { history ->
            binding.progressBar.visibility = View.GONE
            binding.tvDiagnosis.text = history.diagnosis
            binding.tvRecommendation.text = history.recommendation
            Glide.with(this).load(history.image_url).into(binding.ivHistory)
        }

        detailHistoryViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        detailHistoryViewModel.error.observe(this) { errorMessage ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
