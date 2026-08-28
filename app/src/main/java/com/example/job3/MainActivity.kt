package com.example.job3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * The entry point of the application. 
 * This activity decides whether to navigate to the Sign In screen or the Friend List screen 
 * based on the user's current authentication status.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if a user is already signed in via Firebase Auth
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // User is logged in, skip to the main content
            startActivity(
                Intent(
                    this,
                    FriendListActivity::class.java
                )
            )
        } else {
            // No session found, redirect to the login screen
            startActivity(
                Intent(
                    this,
                    SignInActivity::class.java
                )
            )
        }

        // Close this activity so it doesn't stay in the back stack
        finish()
    }
}