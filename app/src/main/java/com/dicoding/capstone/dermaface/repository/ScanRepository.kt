package com.dicoding.capstone.dermaface.repository

import android.content.Context
import android.graphics.Bitmap
import com.dicoding.capstone.dermaface.R
import com.dicoding.capstone.dermaface.utils.FirebaseModelHandler
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ScanRepository(val context: Context) {

    private val firebaseModelHandler = FirebaseModelHandler(context)
    private val firestore = FirebaseFirestore.getInstance()

    fun downloadModel(
        onModelDownloaded: () -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseModelHandler.downloadModel(onModelDownloaded, onError)
    }

    fun analyzeImage(bitmap: Bitmap): FloatArray? {
        return firebaseModelHandler.analyzeImage(bitmap)
    }

    suspend fun getRecommendation(diagnosis: String): String {
        return try {
            val document = firestore.collection("recommendations")
                .document(diagnosis)
                .get()
                .await()

            val tips = document.get("tips") as? List<*>
            tips?.joinToString("\n") ?: context.getString(R.string.error_getting_recommendation)
        } catch (e: Exception) {
            context.getString(R.string.error_getting_recommendation)
        }
    }
}
