/**
 * AI 자동완성 키보드 메인 액티비티
 * 
 * 이 액티비티는 AI 키보드 앱의 메인 화면을 제공합니다.
 * 사용자가 앱을 처음 실행했을 때 보게 되는 시작 화면으로,
 * 키보드 테스트, 설정, 시스템 설정 연동 등의 기능을 포함합니다.
 * 
 * 주요 기능:
 * - 앱 소개 및 로고 표시
 * - 키보드 테스트를 위한 텍스트 입력 필드
 * - 키보드 설정 화면으로 이동하는 버튼
 * - 시스템 설정으로 직접 이동하는 버튼
 * - 주요 기능 설명 및 가이드
 * - Material Design 3 기반 UI
 * 
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var testText by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 앱 아이콘 또는 로고
        Text(
            text = "⌨️",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 앱 제목
        Text(
            text = "AI 자동완성 키보드",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // 앱 설명
        Text(
            text = "AI가 학습한 당신만의 키보드",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // 키보드 테스트 영역
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "키보드 테스트",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                OutlinedTextField(
                    value = testText,
                    onValueChange = { testText = it },
                    label = { Text("여기에 텍스트를 입력해보세요") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                )
                
                Text(
                    text = "입력된 텍스트: $testText",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // 키보드 설정 버튼
        Button(
            onClick = {
                val intent = Intent(context, com.example.myapplication.keyboard.KeyboardSettingsActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "키보드 설정",
                fontSize = 18.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 시스템 설정으로 이동
        OutlinedButton(
            onClick = {
                val intent = Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "시스템 설정",
                fontSize = 18.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 기능 설명
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "주요 기능",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Text(
                    text = "• AI 기반 자동완성\n" +
                           "• 개인화된 페르소나\n" +
                           "• 문맥 기반 예측\n" +
                           "• 이모티콘 추천",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
