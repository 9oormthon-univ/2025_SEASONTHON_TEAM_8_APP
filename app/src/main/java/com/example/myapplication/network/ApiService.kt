package com.example.myapplication.network

import com.example.myapplication.model.*

/**
 * 통합 API 서비스
 *
 * 모든 API 기능을 하나의 인터페이스로 통합하여 제공합니다. AuthApi와 ChatRoomApi의 기능을 모두 포함합니다.
 */
class ApiService {
    private val authApi = AuthApi()
    private val chatRoomApi = ChatRoomApi()

    // 인증 관련 API
    suspend fun authenticateWithGoogle(code: String): Result<AuthResponse> {
        return authApi.authenticateWithGoogle(code)
    }

    // 채팅 분석 관련 API
    suspend fun analyzeChat(
            chatRoomType: String,
            request: ChatAnalysisRequest
    ): Result<ChatAnalysisResponse> {
        return chatRoomApi.analyzeChat(chatRoomType, request)
    }

    suspend fun getChatRoom(roomId: String): Result<ChatRoom> {
        return chatRoomApi.getChatRoom(roomId)
    }

    suspend fun deleteChatRoom(roomId: String): Result<Unit> {
        return chatRoomApi.deleteChatRoom(roomId)
    }

    suspend fun updateChatRoom(roomId: String, request: ChatRoomUpdateRequest): Result<ChatRoom> {
        return chatRoomApi.updateChatRoom(roomId, request)
    }
}
