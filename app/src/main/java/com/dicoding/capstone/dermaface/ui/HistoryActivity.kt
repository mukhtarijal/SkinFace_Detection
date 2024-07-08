package com.dicoding.capstone.dermaface.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.capstone.dermaface.R
import com.dicoding.capstone.dermaface.adapter.HistoryAdapter
import com.dicoding.capstone.dermaface.repository.HistoryRepository
import com.dicoding.capstone.dermaface.data.model.HistoryResponse
import com.dicoding.capstone.dermaface.viewmodel.HistoryViewModel
import com.dicoding.capstone.dermaface.viewmodel.ViewModelFactory
import com.dicoding.capstone.dermaface.databinding.ActivityHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val historyViewModel: HistoryViewModel by viewModels {
        ViewModelFactory(
            historyRepository = HistoryRepository(
                FirebaseFirestore.getInstance(),
                FirebaseAuth.getInstance()
            )
        )
    }
    private lateinit var historyAdapter: HistoryAdapter
    private val histories = mutableListOf<HistoryResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        observeViewModel()
        historyViewModel.fetchHistories()

        binding.btnBack.setOnClickListener {
            navigateToMainActivity()
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (isTaskRoot) {
            navigateToMainActivity()
        } else {
            super.onBackPressed()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(histories,
            onItemClick = { history ->
                val intent = Intent(this, DetailHistoryActivity::class.java).apply {
                    putExtra("HISTORY_ID", history.id)
                }
                startActivity(intent)
            },
            onDeleteClick = { history ->
                showDeleteConfirmationDialog(history)
            })

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = historyAdapter
    }

    private fun showDeleteConfirmationDialog(history: HistoryResponse) {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete_history)
            .setPositiveButton(R.string.yes) { _, _ ->
                historyViewModel.deleteHistory(history)
            }
            .setNegativeButton(R.string.no, null)
            .create()
            .show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        historyViewModel.histories.observe(this) { fetchedHistories ->
            binding.progressBar.visibility = View.GONE
            histories.clear()
            if (fetchedHistories != null) {
                histories.addAll(fetchedHistories)
            }
            historyAdapter.notifyDataSetChanged()
        }

        historyViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        historyViewModel.deletionStatus.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, R.string.history_deleted, Toast.LENGTH_SHORT).show()
            }
            result.onFailure {
                Toast.makeText(this, R.string.history_delete_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
