package com.dicoding.capstone.dermaface.repository

import com.dicoding.capstone.dermaface.data.model.HistoryResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HistoryRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    suspend fun fetchHistories(): Result<List<HistoryResponse>> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val querySnapshot = firestore.collection("user_data").document(uid)
                .collection("scans")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get().await()

            val histories = querySnapshot.documents.map { document ->
                document.toObject(HistoryResponse::class.java)!!.apply { id = document.id }
            }
            Result.success(histories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteHistory(historyId: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            firestore.collection("user_data").document(uid)
                .collection("scans").document(historyId)
                .delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
