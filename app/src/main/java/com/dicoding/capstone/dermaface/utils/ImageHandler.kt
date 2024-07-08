package com.dicoding.capstone.dermaface.utils

import android.Manifest
import android.app.AlertDialog
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.capstone.dermaface.R
import com.dicoding.capstone.dermaface.databinding.ItemImageSourceBinding
import com.dicoding.capstone.dermaface.viewmodel.MainViewModel
import com.yalantis.ucrop.UCrop
import java.io.File

class ImageHandler(
    private val activity: AppCompatActivity,
    private val viewModel: MainViewModel
) {
    private val cameraLauncher: ActivityResultLauncher<Uri> =
        activity.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                viewModel.imageUri.value?.let { viewModel.setImageUri(it) }
            } else {
                Toast.makeText(activity, R.string.capture_error, Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { viewModel.setImageUri(it) }
        }

    private val cameraPermissionLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCamera()
            else showPermissionDeniedMessage()
        }

    private val galleryPermissionLauncher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true ||
                permissions[Manifest.permission.READ_MEDIA_IMAGES] == true) {
                openGallery()
            } else {
                showPermissionDeniedMessage()
            }
        }

    fun showImageSourceDialog() {
        val dialogBinding = ItemImageSourceBinding.inflate(activity.layoutInflater)
        AlertDialog.Builder(activity)
            .setView(dialogBinding.root)
            .create()
            .apply {
                dialogBinding.btnCamera.setOnClickListener {
                    requestCameraPermission()
                    dismiss()
                }
                dialogBinding.btnGallery.setOnClickListener {
                    requestGalleryPermission()
                    dismiss()
                }
            }
            .show()
    }

    private fun requestCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun requestGalleryPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        galleryPermissionLauncher.launch(permissions)
    }

    private fun openCamera() {
        val uri = MainUtil.createTempImageUri(activity)
        viewModel.setImageUri(uri)
        cameraLauncher.launch(uri)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(activity, R.string.permission_denied, Toast.LENGTH_SHORT).show()
    }

    fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(activity.cacheDir, "cropped.jpg"))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1080, 1080)
            .start(activity)
    }
}
