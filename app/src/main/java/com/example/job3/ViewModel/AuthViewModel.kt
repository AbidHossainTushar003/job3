package com.example.job3.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.job3.model.AppUser
import com.example.job3.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * ViewModel responsible for handling authentication and user data management.
 * Exposes UI states via StateFlows for loading status, error messages, and user lists.
 */
class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loading = MutableStateFlow(false)
    /** Indicates if a background operation (sign in, sign up, update) is currently in progress */
    val loading: StateFlow<Boolean> = _loading

    private val _message = MutableStateFlow<String?>(null)
    /** Error or status messages to be displayed to the user as Toasts */
    val message: StateFlow<String?> = _message

    private val _users = MutableStateFlow<List<AppUser>>(emptyList())
    /** The list of all users registered in the system */
    val users: StateFlow<List<AppUser>> = _users

    private val _currentUser = MutableStateFlow<AppUser?>(null)
    /** The profile data for the currently authenticated user */
    val currentUser: StateFlow<AppUser?> = _currentUser

    /**
     * Signs in an existing user with email and password.
     */
    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.signIn(email, password)
            _loading.value = false

            if (result.isSuccess) {
                onSuccess()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Login failed"
            }
        }
    }

    /**
     * Registers a new user with email and password.
     */
    fun signUp(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.signUp(email, password)
            _loading.value = false

            if (result.isSuccess) {
                onSuccess()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Registration failed"
            }
        }
    }

    /**
     * Saves or updates a user's location and profile info in the database.
     */
    fun saveUser(
        appUser: AppUser,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.saveUser(appUser)
            _loading.value = false

            if (result.isSuccess) {
                onSuccess()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Could not save user"
            }
        }
    }

    /**
     * Fetches all registered users from the database.
     */
    fun loadAllUsers() {
        viewModelScope.launch {
            val result = repository.getAllUsers()
            if (result.isSuccess) {
                _users.value = result.getOrDefault(emptyList())
            } else {
                _message.value = result.exceptionOrNull()?.message
            }
        }
    }

    /**
     * Loads the profile details of the current authenticated user.
     */
    fun loadCurrentUser() {
        viewModelScope.launch {
            val result = repository.getCurrentUser()
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
            } else {
                _message.value = result.exceptionOrNull()?.message
            }
        }
    }

    /**
     * Updates the display name of a specific user.
     */
    fun updateDisplayName(
        userId: String,
        name: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.updateDisplayName(userId, name)
            _loading.value = false

            if (result.isSuccess) {
                onSuccess()
            } else {
                _message.value = result.exceptionOrNull()?.message ?: "Update failed"
            }
        }
    }

    /**
     * Signs out the current user.
     */
    fun logout() {
        repository.logout()
    }

    /**
     * Returns the current [com.google.firebase.auth.FirebaseUser] instance.
     */
    fun firebaseUser() = repository.getFirebaseUser()
}