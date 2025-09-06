package com.example.myapplication.model

/**
 * 채팅방 모델
 *
 * @param id 채팅방 ID
 * @param name 채팅방 이름
 * @param type 채팅방 타입 (GROUP, PRIVATE)
 * @param pinned 고정 여부
 * @param deleted 삭제 여부
 * @param createdAt 생성 시간
 * @param updatedAt 수정 시간
 */
data class ChatRoom(
        val id: String,
        val name: String,
        val type: String,
        val pinned: Boolean,
        val deleted: Boolean,
        val createdAt: String,
        val updatedAt: String
)
