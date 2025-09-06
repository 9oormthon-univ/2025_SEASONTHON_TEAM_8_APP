package com.example.myapplication.model

/**
 * 채팅방 업데이트 요청 모델
 *
 * @param name 채팅방 이름
 * @param type 채팅방 타입 (GROUP, PRIVATE)
 */
data class ChatRoomUpdateRequest(val name: String, val type: String)
