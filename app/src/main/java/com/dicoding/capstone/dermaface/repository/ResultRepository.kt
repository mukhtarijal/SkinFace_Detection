package com.dicoding.capstone.dermaface.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ResultRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    suspend fun uploadImageAndSaveData(imageUri: Uri, diagnosis: String, recommendation: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val timestamp = System.currentTimeMillis()
            val imageRef = storage.reference.child("images/${uid}/${timestamp}.jpg")

            imageRef.putFile(imageUri).await()

            val downloadUrl = imageRef.downloadUrl.await().toString()

            val userData = hashMapOf(
                "uid" to uid,
                "image_url" to downloadUrl,
                "diagnosis" to diagnosis,
                "recommendation" to recommendation,
                "timestamp" to timestamp
            )

            firestore.collection("user_data").document(uid)
                .collection("scans")
                .add(userData).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
