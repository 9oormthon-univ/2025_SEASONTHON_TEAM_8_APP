/**
 * 코멘토 앱의 설정 화면
 * 
 * 이 화면은 사용자가 앱의 주요 설정에 접근할 수 있는 중앙 집중식 설정 메뉴입니다.
 * 
 * 주요 기능:
 * - 키보드 설정으로의 빠른 이동
 * - 시스템 설정으로의 빠른 이동
 * - 각 설정 항목에 대한 설명 제공
 * - 설정 화면으로의 편리한 접근
 * 
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen() {
    // 현재 컨텍스트 가져오기 (Intent 실행을 위해)
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 화면 제목
        Text(
            text = "설정",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // 키보드 설정 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "키보드 설정",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // 키보드 설정 설명
                Text(
                    text = "AI 키보드의 개인화 설정을 관리합니다.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 키보드 설정 열기 버튼
                Button(
                    onClick = {
                        // KeyboardSettingsActivity로 이동
                        val intent = Intent(context, com.example.myapplication.keyboard.KeyboardSettingsActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("키보드 설정 열기")
                }
            }
        }
        
        // 시스템 설정 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "시스템 설정",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // 시스템 설정 설명
                Text(
                    text = "안드로이드 시스템의 입력 방법 설정으로 이동합니다.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 시스템 설정 열기 버튼
                OutlinedButton(
                    onClick = {
                        // 안드로이드 시스템 입력 방법 설정으로 이동
                        val intent = Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("시스템 설정 열기")
                }
            }
        }
    }
}
