/**
 * 대화 분석 화면
 *
 * 이 화면은 단체/개인 톡방 분석 기능을 제공합니다.
 *
 * 주요 기능:
 * - 뒤로가기 버튼
 * - 단체/개인 톡방 분석 카드
 * - 가로 스크롤 가능한 분석 옵션 버튼들
 * - 분석 시작하기 버튼
 * - 파일 선택 기능
 *
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.myapplication.R
import com.example.myapplication.ui.theme.BackgroundColor
import kotlin.math.abs
import com.example.myapplication.ui.components.ConversationScreen.ConversationStartDialog
import com.example.myapplication.ui.components.ConversationScreen.ConversationAnalysisCard
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.shadow
import com.example.myapplication.ui.theme.MainColor2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationAnalysisScreen(
    onBackClick: () -> Unit = {}
) {
    var selectedAnalysisType by remember { mutableStateOf("group") }
    var currentPage by remember { mutableStateOf(0) }
    val totalPages = 2

    val scrollState = rememberScrollState()

    var dragOffset by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    var isDragging by remember { mutableStateOf(false) }
    var isStartDialogVisible by remember { mutableStateOf(false) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    // 파일 선택을 위한 ActivityResultLauncher - 텍스트 파일만 선택 가능
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            // 카카오톡 채팅 파일 형식 검증
            if (isValidKakaoChatFile(context, it)) {
                isStartDialogVisible = false
                handleSelectedFile(context, it)
            } else {
                errorMessage = "카카오톡 채팅 파일이 아닙니다.\n올바른 채팅 내보내기 파일을 선택해주세요."
                showErrorDialog = true
            }
        }
    }

    val animatedOffset by animateFloatAsState(
        targetValue = dragOffset,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ),
        label = "page_transition",
        finishedListener = {
            isAnimating = false
        }
    )

    Scaffold(
        topBar = {
            // 상단 앱바
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        spotColor = Color.White.copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black
                )
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = "대화 분석",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "뒤로가기",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                    // 하얀색 그림자
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        MainColor2.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(currentPage) {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    dragOffset = 0f
                                    isDragging = true
                                },
                                onDragEnd = {
                                    isDragging = false
                                    isAnimating = true
                                    if (abs(dragOffset) > 60) {
                                        if (dragOffset > 0 && currentPage > 0) {
                                            currentPage--
                                        } else if (dragOffset < 0 && currentPage < totalPages - 1) {
                                            currentPage++
                                        }
                                    }
                                    dragOffset = 0f
                                }
                            ) { _, dragAmount ->
                                if (abs(dragAmount) > 1f) {
                                    dragOffset = (dragOffset + dragAmount * 0.8f).coerceIn(-150f, 150f)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    repeat(totalPages) { index ->
                        val isCurrentPage = index == currentPage
                        val offset = if (isCurrentPage) animatedOffset else (index - currentPage) * 280f + animatedOffset
                        val scale = if (isCurrentPage) 1f else 0.8f
                        val alpha = if (isCurrentPage) 1f else 0.6f
                        val elevation = if (isCurrentPage) 12.dp else 4.dp

                        if (abs(offset) < 600f) {
                            ConversationAnalysisCard(
                                type = if (index == 0) "group" else "personal",
                                modifier = Modifier
                                    .graphicsLayer(
                                        translationX = offset,
                                        scaleX = scale,
                                        scaleY = scale,
                                        alpha = alpha,
                                        compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
                                    )
                                    .zIndex(if (isCurrentPage) 1f else 0f)
                                    .clickable(enabled = !isCurrentPage && !isDragging && !isAnimating) {
                                        currentPage = index
                                        dragOffset = 0f
                                    },
                                elevation = elevation,
                                onStartClick = { isStartDialogVisible = true }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(totalPages) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (index == currentPage) Color.White else Color.White.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                                .clickable {
                                    if (!isDragging && !isAnimating) {
                                        currentPage = index
                                        dragOffset = 0f
                                    }
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = when (currentPage) {
                        0 -> "단체 톡방의 특별한 분석으로\n그룹 내 역학관계를 파악해보세요"
                        1 -> "개인 대화만의 깊이 있는 분석으로\n더 의미 있는 관계를 만들어보세요"
                        else -> ""
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        ConversationStartDialog(
            visible = isStartDialogVisible,
            onDismiss = { isStartDialogVisible = false },
            onPickFile = { 
                // 텍스트 파일만 선택할 수 있도록 필터링
                filePickerLauncher.launch("text/*")
            }
        )

        // 에러 다이얼로그
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = {
                    Text(
                        text = "파일 형식 오류",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(errorMessage)
                },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("확인")
                    }
                }
            )
        }
        }
    }
}

/**
 * 선택된 파일을 처리하는 함수
 * @param context 앱 컨텍스트
 * @param uri 선택된 파일의 URI
 */
private fun handleSelectedFile(context: Context, uri: Uri) {
    try {
        // 파일 정보를 가져옵니다
        val fileName = getFileName(context, uri)
        val fileSize = getFileSize(context, uri)
        
        // 여기서 파일을 분석하거나 처리하는 로직을 추가할 수 있습니다
        // 예: 파일 내용 읽기, 분석 시작 등
        
        // 임시로 파일 정보를 로그에 출력
        println("선택된 파일: $fileName, 크기: $fileSize bytes")
        
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * URI에서 파일명을 가져오는 함수
 */
private fun getFileName(context: Context, uri: Uri): String {
    var fileName = "알 수 없는 파일"
    try {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex) ?: fileName
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return fileName
}

/**
 * URI에서 파일 크기를 가져오는 함수
 */
private fun getFileSize(context: Context, uri: Uri): Long {
    var fileSize = 0L
    try {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    fileSize = it.getLong(sizeIndex)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return fileSize
}

/**
 * 카카오톡 채팅 파일인지 검증하는 함수
 */
private fun isValidKakaoChatFile(context: Context, uri: Uri): Boolean {
    return try {
        val fileName = getFileName(context, uri)
        val fileContent = readFileContent(context, uri)
        
        // 파일명이 .txt로 끝나는지 확인
        val isTextFile = fileName.endsWith(".txt", ignoreCase = true)
        
        // 카카오톡 채팅 파일의 특징적인 패턴 확인
        val hasKakaoPattern = fileContent.contains("카카오톡") || 
                             fileContent.contains("채팅방") ||
                             fileContent.contains("년") && fileContent.contains("월") && fileContent.contains("일") ||
                             fileContent.contains("오전") || fileContent.contains("오후")
        
        isTextFile && hasKakaoPattern
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

/**
 * 파일 내용을 읽는 함수
 */
private fun readFileContent(context: Context, uri: Uri): String {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().use { reader ->
                reader.readText()
            }
        } ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

