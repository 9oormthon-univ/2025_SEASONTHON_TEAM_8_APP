package com.example.myapplication.repository

import com.example.myapplication.model.*
import com.example.myapplication.network.ApiService

/**
 * 채팅 분석 관련 데이터 처리를 담당하는 Repository
 *
 * 주요 기능:
 * - 채팅 분석 요청
 * - 채팅방 조회, 수정, 삭제
 * - API 호출 및 결과 처리
 */
class ChatAnalysisRepository {
    private val apiService = ApiService()

    /**
     * 채팅 분석을 요청합니다
     *
     * @param chatRoomType 채팅방 타입 (GROUP, PRIVATE)
     * @param fileData 채팅 파일 데이터 (Base64 인코딩된 문자열)
     * @return 분석 결과
     */
    suspend fun analyzeChat(chatRoomType: String, fileData: String): Result<ChatAnalysisResponse> {
        return try {
            val request = ChatAnalysisRequest(fileData)
            apiService.analyzeChat(chatRoomType, request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 채팅방 정보를 조회합니다
     *
     * @param roomId 채팅방 ID
     * @return 채팅방 정보
     */
    suspend fun getChatRoom(roomId: String): Result<ChatRoom> {
        return try {
            apiService.getChatRoom(roomId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 채팅방을 삭제합니다
     *
     * @param roomId 채팅방 ID
     * @return 삭제 결과
     */
    suspend fun deleteChatRoom(roomId: String): Result<Unit> {
        return try {
            apiService.deleteChatRoom(roomId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 채팅방 정보를 업데이트합니다
     *
     * @param roomId 채팅방 ID
     * @param name 새로운 채팅방 이름
     * @param type 채팅방 타입 (GROUP, PRIVATE)
     * @return 업데이트된 채팅방 정보
     */
    suspend fun updateChatRoom(roomId: String, name: String, type: String): Result<ChatRoom> {
        return try {
            val request = ChatRoomUpdateRequest(name, type)
            apiService.updateChatRoom(roomId, request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
