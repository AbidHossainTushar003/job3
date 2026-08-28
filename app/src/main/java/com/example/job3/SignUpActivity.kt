package com.example.job3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.job3.ViewModel.AuthViewModel
import com.example.job3.databinding.ActivitySignUpBinding
import com.example.job3.model.AppUser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivitySignUpBinding

    private val viewModel:
            AuthViewModel by viewModels()

    private lateinit var fusedLocationClient:
            FusedLocationProviderClient

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fine =
                permissions[
                    Manifest.permission.ACCESS_FINE_LOCATION
                ] ?: false

            val coarse =
                permissions[
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ] ?: false

            if (fine || coarse) {

                detectLocationAndSaveUser()

            } else {

                Toast.makeText(
                    this,
                    "Location permission is required",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding =
            ActivitySignUpBinding.inflate(layoutInflater)

        setContentView(binding.root)

        fusedLocationClient =
            LocationServices
                .getFusedLocationProviderClient(this)

        observeViewModel()

        binding.btnSignUp.setOnClickListener {

            val email =
                binding.etEmail.text
                    .toString()
                    .trim()

            val password =
                binding.etPassword.text
                    .toString()

            val confirmPassword =
                binding.etConfirmPassword.text
                    .toString()

            if (email.isEmpty()) {

                binding.etEmail.error =
                    "Enter email"

                return@setOnClickListener
            }

            if (password.isEmpty()) {

                binding.etPassword.error =
                    "Enter password"

                return@setOnClickListener
            }

            if (password.length < 6) {

                binding.etPassword.error =
                    "Password must be at least 6 characters"

                return@setOnClickListener
            }

            if (password != confirmPassword) {

                binding.etConfirmPassword.error =
                    "Password does not match"

                return@setOnClickListener
            }

            viewModel.signUp(
                email,
                password
            ) {

                checkLocationPermission()
            }
        }

        binding.tvLogin.setOnClickListener {

            finish()
        }
    }

    private fun observeViewModel() {

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.message.collect { msg ->

                    msg?.let {

                        Toast.makeText(
                            this@SignUpActivity,
                            it,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        lifecycleScope.launch {

            repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.loading.collect { isLoading ->

                    binding.progressBar.visibility =
                        if (isLoading) {
                            android.view.View.VISIBLE
                        } else {
                            android.view.View.GONE
                        }

                    binding.btnSignUp.isEnabled =
                        !isLoading
                }
            }
        }
    }

    private fun checkLocationPermission() {

        val fineGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {

            detectLocationAndSaveUser()

        } else {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun detectLocationAndSaveUser() {

        val fineGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {

                    saveUser(location)

                } else {

                    fusedLocationClient
                        .getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            null
                        )
                        .addOnSuccessListener { currentLocation ->

                            if (currentLocation != null) {

                                saveUser(
                                    currentLocation
                                )

                            } else {

                                Toast.makeText(
                                    this,
                                    "Could not detect location",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
    }

    private fun saveUser(location: Location) {

        val firebaseUser =
            viewModel.firebaseUser()
                ?: return

        val appUser = AppUser(

            userId = firebaseUser.uid,

            userEmail =
                firebaseUser.email ?: "",

            displayName = null,

            latitude =
                location.latitude,

            longitude =
                location.longitude
        )

        viewModel.saveUser(appUser) {

            startActivity(
                Intent(
                    this,
                    FriendListActivity::class.java
                )
            )

            finish()
        }
    }
}