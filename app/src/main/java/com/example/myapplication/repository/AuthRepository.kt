package com.example.myapplication.repository

import com.example.myapplication.model.AuthResponse
import com.example.myapplication.network.NetworkModule

class AuthRepository {
    private val apiService = NetworkModule.apiService

    suspend fun authenticateWithGoogle(code: String): Result<AuthResponse> {
        return apiService.authenticateWithGoogle(code)
    }
}
