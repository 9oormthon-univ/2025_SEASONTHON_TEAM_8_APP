/**
 * 코멘토 키보드 테스트 화면
 * 
 * 이 화면은 사용자가 코멘토 키보드가 제대로 작동하는지 테스트할 수 있는 화면입니다.
 * 
 * 주요 기능:
 * - 텍스트 입력 필드를 통한 키보드 테스트
 * - 입력된 텍스트 실시간 표시
 * - 키보드 활성화 상태 확인
 * - 사용자에게 키보드 테스트 방법 안내
 * 
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KeyboardTestScreen() {
    // 테스트용 텍스트 입력 상태 관리
    var testText by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 화면 제목
        Text(
            text = "키보드 테스트",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // 키보드 테스트 카드
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
                
                // 텍스트 입력 필드 (키보드 테스트용)
                OutlinedTextField(
                    value = testText,
                    onValueChange = { testText = it },  // 입력된 텍스트를 상태에 저장
                    label = { Text("여기에 텍스트를 입력해보세요") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),  // 충분한 높이로 여러 줄 입력 가능
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                )
                
                // 입력된 텍스트 실시간 표시
                Text(
                    text = "입력된 텍스트: $testText",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // 키보드 테스트 안내 메시지
        Text(
            text = "키보드를 활성화하고 위 텍스트 필드에 입력해보세요.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
