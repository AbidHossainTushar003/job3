package com.example.job3.model

/**
 * Data class representing a user within the application.
 * This class is designed to be compatible with Firebase Firestore's automatic mapping.
 *
 * @property userId The unique identifier for the user (sourced from Firebase Auth UID).
 * @property userEmail The email address of the user.
 * @property displayName The name the user chose to show others; may be null initially.
 * @property latitude The last recorded latitude coordinate of the user.
 * @property longitude The last recorded longitude coordinate of the user.
 */
data class AppUser(
    val userId: String = "",
    val userEmail: String = "",
    val displayName: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)