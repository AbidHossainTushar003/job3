package com.example.job3.repo

import com.example.job3.model.AppUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun signIn(
        email: String,
        password: String
    ): Result<Boolean> {

        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(
        email: String,
        password: String
    ): Result<Boolean> {

        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUser(appUser: AppUser): Result<Boolean> {

        return try {
            firestore.collection("AppUsers")
                .document(appUser.userId)
                .set(appUser)
                .await()

            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): Result<List<AppUser>> {

        return try {

            val snapshot = firestore
                .collection("AppUsers")
                .get()
                .await()

            val users = snapshot.toObjects(AppUser::class.java)

            Result.success(users)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<AppUser?> {

        return try {

            val currentUser = auth.currentUser
                ?: return Result.success(null)

            val document = firestore
                .collection("AppUsers")
                .document(currentUser.uid)
                .get()
                .await()

            val user = document.toObject(AppUser::class.java)

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateDisplayName(
        userId: String,
        displayName: String
    ): Result<Boolean> {

        return try {

            firestore.collection("AppUsers")
                .document(userId)
                .update("displayName", displayName)
                .await()

            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getFirebaseUser() = auth.currentUser
}