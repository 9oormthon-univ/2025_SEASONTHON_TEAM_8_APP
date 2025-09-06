package com.example.myapplication.network

import com.example.myapplication.config.Config
import com.example.myapplication.model.*
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.runBlocking

/**
 * 채팅방 관련 API
 *
 * 주요 기능:
 * - 채팅 분석
 * - 채팅방 조회, 수정, 삭제
 */
class ChatRoomApi {
    private val baseUrl = Config.baseUrl
    private val connectionTimeout = Config.connectionTimeout
    private val readTimeout = Config.apiTimeout

    /**
     * 채팅 분석을 요청합니다
     *
     * @param chatRoomType 채팅방 타입 (GROUP, PRIVATE)
     * @param request 채팅 분석 요청 데이터
     * @return 분석 결과
     */
    suspend fun analyzeChat(
            chatRoomType: String,
            request: ChatAnalysisRequest
    ): Result<ChatAnalysisResponse> {
        return runBlocking { performChatAnalysisRequest(chatRoomType, request) }
    }

    /**
     * 채팅방 정보를 조회합니다
     *
     * @param roomId 채팅방 ID
     * @return 채팅방 정보
     */
    suspend fun getChatRoom(roomId: String): Result<ChatRoom> {
        return runBlocking { performGetChatRoomRequest(roomId) }
    }

    /**
     * 채팅방을 삭제합니다
     *
     * @param roomId 채팅방 ID
     * @return 삭제 결과
     */
    suspend fun deleteChatRoom(roomId: String): Result<Unit> {
        return runBlocking { performDeleteChatRoomRequest(roomId) }
    }

    /**
     * 채팅방 정보를 업데이트합니다
     *
     * @param roomId 채팅방 ID
     * @param request 채팅방 업데이트 요청 데이터
     * @return 업데이트된 채팅방 정보
     */
    suspend fun updateChatRoom(roomId: String, request: ChatRoomUpdateRequest): Result<ChatRoom> {
        return runBlocking { performUpdateChatRoomRequest(roomId, request) }
    }

    // 채팅 분석 API 구현 메서드들
    private fun performChatAnalysisRequest(
            chatRoomType: String,
            request: ChatAnalysisRequest
    ): Result<ChatAnalysisResponse> {
        val future = CompletableFuture<Result<ChatAnalysisResponse>>()

        Thread {
                    try {
                        if (Config.isLoggingEnabled) {
                            println("📊 채팅 분석 요청 시작 - 타입: $chatRoomType")
                        }

                        val url = URL("$baseUrl/api/chat/analysis?chatRoomType=$chatRoomType")
                        if (Config.isLoggingEnabled) {
                            if (Config.isLoggingEnabled) {
                                println("🌐 API 요청 URL: $url")
                            }
                        }

                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "POST"
                        connection.setRequestProperty("Accept", "application/json")
                        connection.setRequestProperty("User-Agent", Config.userAgent)
                        connection.doOutput = true
                        connection.connectTimeout = connectionTimeout.toInt()
                        connection.readTimeout = readTimeout.toInt()

                        // multipart/form-data 형식으로 파일 업로드
                        val boundary = "----WebKitFormBoundary${System.currentTimeMillis()}"
                        connection.setRequestProperty(
                                "Content-Type",
                                "multipart/form-data; boundary=$boundary"
                        )

                        val outputStream = connection.outputStream
                        val writer = OutputStreamWriter(outputStream, "UTF-8")

                        // 파일 데이터 추가
                        writer.append("--$boundary").append("\r\n")
                        writer.append(
                                        "Content-Disposition: form-data; name=\"file\"; filename=\"chat.txt\""
                                )
                                .append("\r\n")
                        writer.append("Content-Type: application/octet-stream").append("\r\n")
                        writer.append("\r\n")
                        writer.flush()

                        // Base64 디코딩된 파일 데이터를 바이트 배열로 변환하여 전송
                        val fileBytes =
                                android.util.Base64.decode(
                                        request.file,
                                        android.util.Base64.DEFAULT
                                )
                        outputStream.write(fileBytes)
                        outputStream.flush()

                        writer.append("\r\n")
                        writer.append("--$boundary--").append("\r\n")
                        writer.flush()
                        writer.close()

                        if (Config.isLoggingEnabled) {
                            if (Config.isLoggingEnabled) {
                                println("📡 HTTP 요청 전송 중...")
                            }
                        }

                        val responseCode = connection.responseCode
                        if (Config.isLoggingEnabled) {
                            if (Config.isLoggingEnabled) {
                                println("📊 HTTP 응답 코드: $responseCode")
                            }
                        }

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val response =
                                    connection.inputStream.bufferedReader().use { it.readText() }
                            if (Config.isLoggingEnabled) {
                                if (Config.isLoggingEnabled) {
                                    println("📄 서버 응답: $response")
                                }
                            }

                            val chatAnalysisResponse = parseChatAnalysisResponse(response)
                            if (Config.isLoggingEnabled) {
                                println("✅ 채팅 분석 성공 - ID: ${chatAnalysisResponse.id}")
                            }
                            future.complete(Result.success(chatAnalysisResponse))
                        } else {
                            val errorMessage =
                                    connection.errorStream?.bufferedReader()?.use { it.readText() }
                                            ?: "HTTP Error: $responseCode"
                            if (Config.isLoggingEnabled) {
                                if (Config.isLoggingEnabled) {
                                    println("❌ HTTP 오류: $errorMessage")
                                }
                            }
                            future.complete(
                                    Result.failure(
                                            Exception("서버 오류 ($responseCode): $errorMessage")
                                    )
                            )
                        }
                    } catch (e: Exception) {
                        if (Config.isLoggingEnabled) {
                            println("💥 예외 발생: ${e.javaClass.simpleName} - ${e.message}")
                            e.printStackTrace()
                        }
                        future.complete(
                                Result.failure(
                                        Exception("네트워크 오류: ${e.message ?: e.javaClass.simpleName}")
                                )
                        )
                    }
                }
                .start()

        return future.get()
    }

    private fun performGetChatRoomRequest(roomId: String): Result<ChatRoom> {
        val future = CompletableFuture<Result<ChatRoom>>()

        Thread {
                    try {
                        if (Config.isLoggingEnabled) {
                            println("📋 채팅방 조회 요청 시작 - ID: $roomId")
                        }

                        val url = URL("$baseUrl/api/chat/analysis/$roomId")
                        if (Config.isLoggingEnabled) {
                            if (Config.isLoggingEnabled) {
                                println("🌐 API 요청 URL: $url")
                            }
                        }

                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"
                        connection.setRequestProperty("Accept", "application/json")
                        connection.setRequestProperty("User-Agent", Config.userAgent)
                        connection.connectTimeout = connectionTimeout.toInt()
                        connection.readTimeout = readTimeout.toInt()

                        if (Config.isLoggingEnabled) {
                            if (Config.isLoggingEnabled) {
                                println("📡 HTTP 요청 전송 중...")
                            }
                        }

                        val responseCode = connection.responseCode
                        if (Config.isLoggingEnabled) {
                            if (Config.isLoggingEnabled) {
                                println("📊 HTTP 응답 코드: $responseCode")
                            }
                        }

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val response =
                                    connection.inputStream.bufferedReader().use { it.readText() }
                            if (Config.isLoggingEnabled) {
                                if (Config.isLoggingEnabled) {
                                    println("📄 서버 응답: $response")
                                }
                            }

                            val chatRoom = parseChatRoomResponse(response)
                            if (Config.isLoggingEnabled) {
                                println("✅ 채팅방 조회 성공 - 이름: ${chatRoom.name}")
                            }
                            future.complete(Result.success(chatRoom))
                        } else {
                            val errorMessage =
                                    connection.errorStream?.bufferedReader()?.use { it.readText() }
                                            ?: "HTTP Error: $responseCode"
                            if (Config.isLoggingEnabled) {
                                if (Config.isLoggingEnabled) {
                                    println("❌ HTTP 오류: $errorMessage")
                                }
                            }
                            future.complete(
                                    Result.failure(
                                            Exception("서버 오류 ($responseCode): $errorMessage")
                                    )
                            )
                        }
                    } catch (e: Exception) {
                        if (Config.isLoggingEnabled) {
                            println("💥 예외 발생: ${e.javaClass.simpleName} - ${e.message}")
                            e.printStackTrace()
                        }
                        future.complete(
                                Result.failure(
                                        Exception("네트워크 오류: ${e.message ?: e.javaClass.simpleName}")
                                )
                        )
                    }
                }
                .start()

        return future.get()
    }

    private fun performDeleteChatRoomRequest(roomId: String): Result<Unit> {
        val future = CompletableFuture<Result<Unit>>()

        Thread {
                    try {
                        if (Config.isLoggingEnabled) {
                            println("🗑️ 채팅방 삭제 요청 시작 - ID: $roomId")
                        }

                        val url = URL("$baseUrl/api/chat/analysis/$roomId")
                        if (Config.isLoggingEnabled) {
                            println("🌐 API 요청 URL: $url")
                        }

                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "DELETE"
                        connection.setRequestProperty("Accept", "application/json")
                        connection.setRequestProperty("User-Agent", Config.userAgent)
                        connection.connectTimeout = connectionTimeout.toInt()
                        connection.readTimeout = readTimeout.toInt()

                        if (Config.isLoggingEnabled) {
                            println("📡 HTTP 요청 전송 중...")
                        }

                        val responseCode = connection.responseCode
                        if (Config.isLoggingEnabled) {
                            println("📊 HTTP 응답 코드: $responseCode")
                        }

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            if (Config.isLoggingEnabled) {
                                println("✅ 채팅방 삭제 성공")
                            }
                            future.complete(Result.success(Unit))
                        } else {
                            val errorMessage =
                                    connection.errorStream?.bufferedReader()?.use { it.readText() }
                                            ?: "HTTP Error: $responseCode"
                            if (Config.isLoggingEnabled) {
                                println("❌ HTTP 오류: $errorMessage")
                            }
                            future.complete(
                                    Result.failure(
                                            Exception("서버 오류 ($responseCode): $errorMessage")
                                    )
                            )
                        }
                    } catch (e: Exception) {
                        if (Config.isLoggingEnabled) {
                            println("💥 예외 발생: ${e.javaClass.simpleName} - ${e.message}")
                            e.printStackTrace()
                        }
                        future.complete(
                                Result.failure(
                                        Exception("네트워크 오류: ${e.message ?: e.javaClass.simpleName}")
                                )
                        )
                    }
                }
                .start()

        return future.get()
    }

    private fun performUpdateChatRoomRequest(
            roomId: String,
            request: ChatRoomUpdateRequest
    ): Result<ChatRoom> {
        val future = CompletableFuture<Result<ChatRoom>>()

        Thread {
                    try {
                        if (Config.isLoggingEnabled) {
                            println("✏️ 채팅방 업데이트 요청 시작 - ID: $roomId")
                        }

                        val url = URL("$baseUrl/api/chat/analysis/$roomId")
                        if (Config.isLoggingEnabled) {
                            println("🌐 API 요청 URL: $url")
                        }

                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "PATCH"
                        connection.setRequestProperty("Accept", "application/json")
                        connection.setRequestProperty("Content-Type", "application/json")
                        connection.setRequestProperty("User-Agent", Config.userAgent)
                        connection.doOutput = true
                        connection.connectTimeout = connectionTimeout.toInt()
                        connection.readTimeout = readTimeout.toInt()

                        // 요청 본문 작성
                        val requestBody = """{"name":"${request.name}","type":"${request.type}"}"""
                        OutputStreamWriter(connection.outputStream).use { writer ->
                            writer.write(requestBody)
                            writer.flush()
                        }

                        if (Config.isLoggingEnabled) {
                            println("📡 HTTP 요청 전송 중...")
                        }

                        val responseCode = connection.responseCode
                        if (Config.isLoggingEnabled) {
                            println("📊 HTTP 응답 코드: $responseCode")
                        }

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val response =
                                    connection.inputStream.bufferedReader().use { it.readText() }
                            if (Config.isLoggingEnabled) {
                                println("📄 서버 응답: $response")
                            }

                            val chatRoom = parseChatRoomResponse(response)
                            if (Config.isLoggingEnabled) {
                                println("✅ 채팅방 업데이트 성공 - 이름: ${chatRoom.name}")
                            }
                            future.complete(Result.success(chatRoom))
                        } else {
                            val errorMessage =
                                    connection.errorStream?.bufferedReader()?.use { it.readText() }
                                            ?: "HTTP Error: $responseCode"
                            if (Config.isLoggingEnabled) {
                                println("❌ HTTP 오류: $errorMessage")
                            }
                            future.complete(
                                    Result.failure(
                                            Exception("서버 오류 ($responseCode): $errorMessage")
                                    )
                            )
                        }
                    } catch (e: Exception) {
                        if (Config.isLoggingEnabled) {
                            println("💥 예외 발생: ${e.javaClass.simpleName} - ${e.message}")
                            e.printStackTrace()
                        }
                        future.complete(
                                Result.failure(
                                        Exception("네트워크 오류: ${e.message ?: e.javaClass.simpleName}")
                                )
                        )
                    }
                }
                .start()

        return future.get()
    }

    // JSON 파싱 메서드들
    private fun parseChatAnalysisResponse(json: String): ChatAnalysisResponse {
        val id = extractIntValue(json, "id")
        val type = extractValue(json, "type")
        val summary = extractValue(json, "summary")
        val resultJson = extractValue(json, "resultJson")
        val createdAt = extractValue(json, "createdAt")
        return ChatAnalysisResponse(id, type, summary, resultJson, createdAt)
    }

    private fun parseChatRoomResponse(json: String): ChatRoom {
        val id = extractValue(json, "id")
        val name = extractValue(json, "name")
        val type = extractValue(json, "type")
        val pinned = extractBooleanValue(json, "pinned")
        val deleted = extractBooleanValue(json, "deleted")
        val createdAt = extractValue(json, "createdAt")
        val updatedAt = extractValue(json, "updatedAt")
        return ChatRoom(id, name, type, pinned, deleted, createdAt, updatedAt)
    }

    private fun extractValue(json: String, key: String): String {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]+)\""
        val regex = pattern.toRegex()
        return regex.find(json)?.groupValues?.get(1) ?: ""
    }

    private fun extractIntValue(json: String, key: String): Int {
        val pattern = "\"$key\"\\s*:\\s*(\\d+)"
        val regex = pattern.toRegex()
        return regex.find(json)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }

    private fun extractBooleanValue(json: String, key: String): Boolean {
        val pattern = "\"$key\"\\s*:\\s*(true|false)"
        val regex = pattern.toRegex()
        return regex.find(json)?.groupValues?.get(1)?.toBoolean() ?: false
    }
}
