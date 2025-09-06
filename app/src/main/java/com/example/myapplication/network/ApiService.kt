package com.example.myapplication.network

import com.example.myapplication.model.AuthResponse

interface ApiService {
    suspend fun authenticateWithGoogle(code: String): Result<AuthResponse>
}
