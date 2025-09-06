package com.example.myapplication.model

/**
 * 채팅 분석 응답 모델
 *
 * @param id 분석 결과 ID
 * @param type 채팅방 타입 (GROUP, PRIVATE)
 * @param summary 분석 요약
 * @param resultJson 분석 결과 JSON 문자열
 * @param createdAt 생성 시간
 */
data class ChatAnalysisResponse(
        val id: Int,
        val type: String,
        val summary: String,
        val resultJson: String,
        val createdAt: String
)
