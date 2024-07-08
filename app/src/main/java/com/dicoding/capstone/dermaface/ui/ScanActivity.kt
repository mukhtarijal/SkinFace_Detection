package com.dicoding.capstone.dermaface.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.capstone.dermaface.R
import com.dicoding.capstone.dermaface.databinding.ActivityScanBinding
import com.dicoding.capstone.dermaface.repository.ScanRepository
import com.dicoding.capstone.dermaface.viewmodel.ScanViewModel
import com.dicoding.capstone.dermaface.viewmodel.ViewModelFactory
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ScanActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }

    private lateinit var binding: ActivityScanBinding
    private val scanViewModel: ScanViewModel by viewModels {
        ViewModelFactory(scanRepository = ScanRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        scanViewModel.modelStatusMessage.observe(this) { statusMessage ->
            binding.tvModelStatus.apply {
                text = statusMessage
                visibility = if (statusMessage.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        }

        scanViewModel.modelReady.observe(this) { isReady ->
            binding.btnScan.isEnabled = isReady
        }

        scanViewModel.analysisResult.observe(this) { result ->
            val diagnosis = result.first
            val recommendation = result.second

            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra(ResultActivity.EXTRA_IMAGE_URI, intent.getStringExtra(EXTRA_IMAGE_URI))
                putExtra(ResultActivity.EXTRA_DIAGNOSIS, diagnosis)
                putExtra(ResultActivity.EXTRA_RECOMMENDATION, recommendation)
            }
            startActivity(intent)
            finish()
        }

        binding.btnScan.setOnClickListener {
            if (scanViewModel.modelReady.value == true) {
                startScanning()
            } else {
                Toast.makeText(this, R.string.model_not_ready, Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvCancel.setOnClickListener {
            finish()
        }

        binding.btnBack.setOnClickListener {
            val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
            if (imageUri != null) {
                val cropIntent = UCrop.of(Uri.parse(imageUri), Uri.parse(imageUri))
                    .getIntent(this)
                startActivity(cropIntent)
            } else {
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        }

        loadImageFromIntent()
    }

    private fun loadImageFromIntent() {
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (imageUri != null) {
            loadImage(Uri.parse(imageUri))
        } else {
            Toast.makeText(this, R.string.image_not_found, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImage(uri: Uri) {
        lifecycleScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }
                bitmap?.let {
                    binding.ivScan.setImageBitmap(it)
                } ?: run {
                    Toast.makeText(this@ScanActivity, R.string.error_loading_image, Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Toast.makeText(this@ScanActivity, "${getString(R.string.error_loading_image)}: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startScanning() {
        val drawable = binding.ivScan.drawable
        if (drawable != null) {
            binding.scannerLine.visibility = View.VISIBLE

            startScanningAnimation {
                val bitmap = drawable.toBitmap()
                scanViewModel.analyzeImage(bitmap)
            }
        } else {
            Toast.makeText(this, R.string.no_image, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startScanningAnimation(onAnimationEnd: () -> Unit) {
        val scanAreaHeight = binding.ivScan.height
        binding.scannerLine.translationY = 0f

        val scanAnimator = ObjectAnimator.ofFloat(binding.scannerLine, "translationY", 0f, scanAreaHeight.toFloat())
        scanAnimator.duration = 2000
        scanAnimator.repeatCount = 3
        scanAnimator.repeatMode = ObjectAnimator.REVERSE

        scanAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                binding.scannerLine.visibility = View.GONE
                onAnimationEnd()
            }
        })

        scanAnimator.start()
    }
}
