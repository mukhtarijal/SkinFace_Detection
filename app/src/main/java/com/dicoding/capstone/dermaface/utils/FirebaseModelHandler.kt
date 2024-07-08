package com.dicoding.capstone.dermaface.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.dicoding.capstone.dermaface.R
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter
import java.io.File

class FirebaseModelHandler(private val context: Context) {

    private var interpreter: Interpreter? = null

    init {
        loadLocalModel()
    }

    private fun loadLocalModel() {
        val localModelFile = File(context.filesDir, "modelquantized.tflite")
        if (localModelFile.exists()) {
            try {
                interpreter = Interpreter(localModelFile)
            } catch (e: Exception) {
                Log.e("FirebaseModelHandler", context.getString(R.string.error_loading_model))
            }
        }
    }

    fun downloadModel(
        onModelDownloaded: () -> Unit,
        onError: (String) -> Unit
    ) {
        val conditions = CustomModelDownloadConditions.Builder().build()

        FirebaseModelDownloader.getInstance()
            .getModel("Face_Detection", DownloadType.LOCAL_MODEL, conditions)
            .addOnSuccessListener { model: CustomModel ->
                model.file?.let { file ->
                    try {
                        val localModelFile = File(context.filesDir, "modelquantized.tflite")
                        file.copyTo(localModelFile, overwrite = true)
                        interpreter = Interpreter(localModelFile)
                        onModelDownloaded()
                    } catch (e: Exception) {
                        onError(context.getString(R.string.error_loading_model))
                    }
                } ?: onError(context.getString(R.string.error_downloading_model))
            }
            .addOnFailureListener { e: Exception ->
                onError("${context.getString(R.string.error_downloading_model)}: ${e.message}")
            }
    }

    fun analyzeImage(bitmap: Bitmap): FloatArray? {
        val inputBuffer = ScanUtil.convertBitmapToByteBuffer(bitmap)
        val outputBuffer = Array(1) { FloatArray(10) }
        return try {
            interpreter?.run(inputBuffer, outputBuffer)
            outputBuffer[0]
        } catch (e: Exception) {
            null
        }
    }
}
