package com.example.job3

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.job3.ViewModel.AuthViewModel
import com.example.job3.databinding.ActivityGoogleMapBinding
import com.example.job3.model.AppUser
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class GoogleMapActivity :
    AppCompatActivity(),
    OnMapReadyCallback {

    private lateinit var binding: ActivityGoogleMapBinding

    private lateinit var googleMap: GoogleMap

    private val viewModel: AuthViewModel by viewModels()

    private var targetUserId: String? = null
    private var hasZoomedToTarget = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoogleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        targetUserId = intent.getStringExtra("USER_ID")

        setupSystemInsets()

        val mapFragment =
            supportFragmentManager
                .findFragmentById(R.id.map) as? SupportMapFragment

        mapFragment?.getMapAsync(this)

        viewModel.loadAllUsers()

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.users.collect { users ->

                    if (::googleMap.isInitialized) {
                        showUsersOnMap(users)
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupSystemInsets() {

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root
        ) { _, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            val topInset = systemBars.top
            val bottomInset = systemBars.bottom

            // Move the Back button below the status bar.
            binding.topBar.updateLayoutParams<ViewGroup.MarginLayoutParams> {

                topMargin = dp(18) + topInset
                bottomMargin = 0
            }

            // Keep map controls away from system bars.
            if (::googleMap.isInitialized) {

                googleMap.setPadding(
                    0,
                    topInset,
                    0,
                    bottomInset + dp(8)
                )
            }

            insets
        }

        ViewCompat.requestApplyInsets(binding.root)
    }

    override fun onMapReady(map: GoogleMap) {

        googleMap = map

        googleMap.uiSettings.isZoomControlsEnabled = true

        // Safe initial map padding.
        ViewCompat.getRootWindowInsets(binding.root)?.let {

            val systemBars = it.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            googleMap.setPadding(
                0,
                systemBars.top,
                0,
                systemBars.bottom + dp(8)
            )
        }

        showUsersOnMap(viewModel.users.value)
    }

    private fun showUsersOnMap(
        users: List<AppUser>
    ) {

        if (!::googleMap.isInitialized) {
            return
        }

        googleMap.clear()

        if (users.isEmpty()) {
            return
        }

        val currentUserId =
            viewModel.firebaseUser()?.uid

        var currentUserLocation: LatLng? = null

        for (user in users) {

            // Ignore invalid coordinates.
            if (
                user.latitude !in -90.0..90.0 ||
                user.longitude !in -180.0..180.0
            ) {
                continue
            }

            val location = LatLng(
                user.latitude,
                user.longitude
            )

            val isCurrentUser =
                user.userId == currentUserId

            if (isCurrentUser) {
                currentUserLocation = location
            }

            val name =
                if (user.displayName.isNullOrBlank()) {

                    if (isCurrentUser) {
                        "Me"
                    } else {
                        "Unknown User"
                    }

                } else {

                    if (isCurrentUser) {
                        "${user.displayName} (Me)"
                    } else {
                        user.displayName
                    }
                }

            googleMap.addMarker(

                MarkerOptions()
                    .position(location)
                    .title(name)
                    .snippet(user.userEmail)
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            if (isCurrentUser) {
                                BitmapDescriptorFactory.HUE_BLUE
                            } else {
                                BitmapDescriptorFactory.HUE_RED
                            }
                        )
                    )
            )
        }

        val focusLocation = if (targetUserId != null) {
            users.find { it.userId == targetUserId }?.let {
                LatLng(it.latitude, it.longitude)
            } ?: currentUserLocation
        } else {
            currentUserLocation
        } ?: users.firstOrNull {
            it.latitude in -90.0..90.0 &&
                    it.longitude in -180.0..180.0
        }?.let {
            LatLng(
                it.latitude,
                it.longitude
            )
        }

        if (focusLocation != null && !hasZoomedToTarget) {
            hasZoomedToTarget = true
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    focusLocation,
                    15f
                )
            )
        }
    }

    private fun dp(value: Int): Int {

        return (
                value *
                        resources.displayMetrics.density
                ).toInt()
    }
}