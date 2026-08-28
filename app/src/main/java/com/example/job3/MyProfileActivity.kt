package com.example.job3

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.job3.ViewModel.AuthViewModel
import com.example.job3.databinding.ActivityMyProfileBinding
import kotlinx.coroutines.launch

/**
 * Activity for viewing and updating the current user's profile information.
 * It displays the user's email, captured coordinates, and allows changing the display name.
 */
class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupSystemInsets()

        // Fetch user data from Firestore
        viewModel.loadCurrentUser()

        observeUser()

        binding.btnUpdateName.setOnClickListener {

            val name =
                binding.etDisplayName.text
                    .toString()
                    .trim()

            if (name.isEmpty()) {

                binding.etDisplayName.error =
                    "Enter display name"

                return@setOnClickListener
            }

            val user = viewModel.firebaseUser()

            if (user != null) {

                viewModel.updateDisplayName(
                    user.uid,
                    name
                ) {

                    Toast.makeText(
                        this,
                        "Display name updated",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Refresh local data after update
                    viewModel.loadCurrentUser()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    /**
     * Handles system bar padding to support edge-to-edge layouts.
     */
    private fun setupSystemInsets() {

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root
        ) { _, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            binding.contentLayout.updatePadding(
                top = systemBars.top + dp(24),
                bottom = systemBars.bottom + dp(24),
                left = dp(24),
                right = dp(24)
            )

            insets
        }

        ViewCompat.requestApplyInsets(binding.root)
    }

    /**
     * Observes the user data and operation states from the ViewModel.
     */
    private fun observeUser() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.currentUser.collect { user ->

                    if (user != null) {

                        binding.tvEmail.text =
                            user.userEmail

                        binding.tvLatitude.text =
                            "Latitude: ${user.latitude}"

                        binding.tvLongitude.text =
                            "Longitude: ${user.longitude}"

                        binding.etDisplayName.setText(
                            user.displayName ?: ""
                        )
                    }
                }
            }
        }

        // Collect loading state to toggle the progress bar
        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.loading.collect { isLoading ->

                    binding.progressBar.visibility =
                        if (isLoading) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }

                    binding.btnUpdateName.isEnabled =
                        !isLoading
                }
            }
        }

        // Collect and display potential error messages
        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.message.collect { msg ->

                    msg?.let {

                        Toast.makeText(
                            this@MyProfileActivity,
                            it,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    /**
     * Utility method to convert density-independent pixels to pixels.
     */
    private fun dp(value: Int): Int {

        return (
                value *
                        resources.displayMetrics.density
                ).toInt()
    }
}