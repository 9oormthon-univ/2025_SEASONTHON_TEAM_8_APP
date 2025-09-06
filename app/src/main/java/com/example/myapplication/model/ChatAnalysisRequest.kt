package com.example.myapplication.model

/**
 * 채팅 분석 요청 모델
 *
 * @param file 채팅 파일 데이터 (Base64 인코딩된 문자열)
 * ```
 *             API에서는 multipart/form-data 형식으로 전송됩니다.
 * ```
 */
data class ChatAnalysisRequest(val file: String)
