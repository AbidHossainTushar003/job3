package com.example.job3.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.job3.model.AppUser
import com.example.job3.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _users = MutableStateFlow<List<AppUser>>(emptyList())
    val users: StateFlow<List<AppUser>> = _users

    private val _currentUser = MutableStateFlow<AppUser?>(null)
    val currentUser: StateFlow<AppUser?> = _currentUser


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
                _message.value =
                    result.exceptionOrNull()?.message
                        ?: "Login failed"
            }
        }
    }


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
                _message.value =
                    result.exceptionOrNull()?.message
                        ?: "Registration failed"
            }
        }
    }


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
                _message.value =
                    result.exceptionOrNull()?.message
                        ?: "Could not save user"
            }
        }
    }


    fun loadAllUsers() {

        viewModelScope.launch {

            val result = repository.getAllUsers()

            if (result.isSuccess) {
                _users.value = result.getOrDefault(emptyList())
            } else {
                _message.value =
                    result.exceptionOrNull()?.message
            }
        }
    }


    fun loadCurrentUser() {

        viewModelScope.launch {

            val result = repository.getCurrentUser()

            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
            } else {
                _message.value =
                    result.exceptionOrNull()?.message
            }
        }
    }


    fun updateDisplayName(
        userId: String,
        name: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _loading.value = true

            val result =
                repository.updateDisplayName(userId, name)

            _loading.value = false

            if (result.isSuccess) {
                onSuccess()
            } else {
                _message.value =
                    result.exceptionOrNull()?.message
                        ?: "Update failed"
            }
        }
    }


    fun logout() {
        repository.logout()
    }

    fun firebaseUser() = repository.getFirebaseUser()
}