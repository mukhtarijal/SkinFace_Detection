package com.dicoding.capstone.dermaface.repository

import com.dicoding.capstone.dermaface.data.model.HistoryResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DetailHistoryRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun fetchHistoryDetails(historyId: String): Result<HistoryResponse> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Pengguna belum masuk."))
            val document = firestore.collection("user_data").document(uid)
                .collection("scans").document(historyId).get().await()

            if (document.exists()) {
                val history = document.toObject(HistoryResponse::class.java)?.apply { id = document.id }
                Result.success(history!!)
            } else {
                Result.failure(Exception("Riwayat tidak ditemukan."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
