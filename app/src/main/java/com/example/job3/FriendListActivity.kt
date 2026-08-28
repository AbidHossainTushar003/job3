package com.example.job3

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.job3.ViewModel.AuthViewModel
import com.example.job3.adapter.FriendAdapter
import com.example.job3.databinding.ActivityFriendListBinding
import kotlinx.coroutines.launch

class FriendListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendListBinding

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var adapter: FriendAdapter

    private var menuOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSystemInsets()
        setupRecyclerView()
        observeUsers()

        viewModel.loadAllUsers()

        binding.fabMain.setOnClickListener {
            toggleMenu()
        }

        binding.fabProfile.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MyProfileActivity::class.java
                )
            )
        }

        binding.fabMap.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    GoogleMapActivity::class.java
                )
            )
        }

        binding.fabLogout.setOnClickListener {

            viewModel.logout()

            startActivity(
                Intent(
                    this,
                    SignInActivity::class.java
                )
            )

            finishAffinity()
        }

        // Start with the menu closed.
        toggleMenu()
    }

    private fun setupSystemInsets() {

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            val topInset = systemBars.top
            val bottomInset = systemBars.bottom

            // Header
            binding.header.updateLayoutParams<android.view.ViewGroup.LayoutParams> {
                height = dp(80) + topInset
            }

            binding.tvHeader.updatePadding(
                top = topInset,
                left = dp(24),
                right = dp(16)
            )

            // RecyclerView bottom space.
            // This prevents the last user card from being hidden
            // behind the floating buttons/navigation bar.
            binding.recyclerView.updatePadding(
                left = dp(12),
                top = dp(12),
                right = dp(12),
                bottom = dp(260) + bottomInset
            )

            // Main FAB
            setFabBottomMargin(
                binding.fabMain,
                dp(16) + bottomInset
            )

            // Logout
            setFabBottomMargin(
                binding.fabLogout,
                dp(88) + bottomInset
            )

            // Map
            setFabBottomMargin(
                binding.fabMap,
                dp(160) + bottomInset
            )

            // Profile
            setFabBottomMargin(
                binding.fabProfile,
                dp(232) + bottomInset
            )

            insets
        }

        ViewCompat.requestApplyInsets(binding.root)
    }

    private fun setFabBottomMargin(
        view: View,
        margin: Int
    ) {
        view.updateLayoutParams<android.view.ViewGroup.MarginLayoutParams> {
            bottomMargin = margin
        }
    }

    private fun dp(value: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun setupRecyclerView() {

        adapter = FriendAdapter(emptyList()) { user ->
            val intent = Intent(this, GoogleMapActivity::class.java).apply {
                putExtra("USER_ID", user.userId)
                putExtra("LATITUDE", user.latitude)
                putExtra("LONGITUDE", user.longitude)
                putExtra("DISPLAY_NAME", user.displayName)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerView.adapter = adapter

        binding.recyclerView.setHasFixedSize(false)

        binding.recyclerView.clipToPadding = false
    }

    private fun observeUsers() {

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.users.collect { users ->

                    adapter.updateList(users)
                }
            }
        }
    }

    private fun toggleMenu() {

        menuOpen = !menuOpen

        if (menuOpen) {

            binding.fabProfile.visibility = View.VISIBLE
            binding.fabMap.visibility = View.VISIBLE
            binding.fabLogout.visibility = View.VISIBLE

        } else {

            binding.fabProfile.visibility = View.GONE
            binding.fabMap.visibility = View.GONE
            binding.fabLogout.visibility = View.GONE
        }
    }
}