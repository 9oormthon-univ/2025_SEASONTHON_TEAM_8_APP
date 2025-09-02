package com.example.myapplication.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log

@Composable
fun AIKeyboardView(
    onTextInput: (String) -> Unit,
    onBackspace: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit
) {
    var aiSuggestions by remember { mutableStateOf(listOf("안녕하세요!", "감사합니다", "좋은 하루 되세요")) }

    Log.d("AIKeyboardView", "Rendering keyboard view")

    LaunchedEffect(Unit) {
        Log.d("AIKeyboardView", "Keyboard view launched")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        // AI 예측 제안 영역
        if (aiSuggestions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                aiSuggestions.forEach { suggestion ->
                    Text(
                        text = suggestion,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { 
                                Log.d("AIKeyboardView", "Suggestion clicked: $suggestion")
                                onTextInput(suggestion) 
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // 키보드 키들
        val rows = listOf(
            listOf("ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ", "ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ"),
            listOf("ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ"),
            listOf("ㅋ", "ㅌ", "ㅊ", "ㅍ", "ㅠ", "ㅜ", "ㅡ", "ㅒ", "ㅖ")
        )

        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { key ->
                    KeyboardKey(
                        text = key,
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            Log.d("AIKeyboardView", "Key pressed: $key")
                            onTextInput(key) 
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 하단 기능 키들
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 숫자/기호 전환
            KeyboardKey(
                text = "123",
                modifier = Modifier.weight(1.5f),
                onClick = { 
                    Log.d("AIKeyboardView", "123 key pressed")
                    /* 숫자 키보드로 전환 */ 
                }
            )
            
            // 스페이스
            KeyboardKey(
                text = "스페이스",
                modifier = Modifier.weight(3f),
                onClick = { 
                    Log.d("AIKeyboardView", "Space pressed")
                    onSpace() 
                }
            )
            
            // 백스페이스
            KeyboardKey(
                text = "←",
                modifier = Modifier.weight(1.5f),
                onClick = { 
                    Log.d("AIKeyboardView", "Backspace pressed")
                    onBackspace() 
                }
            )
            
            // 엔터
            KeyboardKey(
                text = "↵",
                modifier = Modifier.weight(1.5f),
                onClick = { 
                    Log.d("AIKeyboardView", "Enter pressed")
                    onEnter() 
                }
            )
        }
    }
}

@Composable
fun KeyboardKey(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
