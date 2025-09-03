/**
 * 코멘토 앱의 도움말 화면
 * 
 * 이 화면은 사용자에게 앱 사용 방법과 지원 정보를 제공합니다.
 * 
 * 주요 기능:
 * - 단계별 설정 가이드 제공
 * - 코멘토 키보드 활성화 방법 설명
 * - 사용자 지원 및 문의 안내
 * - 문제 해결을 위한 기본 정보 제공
 * 
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HelpScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 화면 제목
        Text(
            text = "도움말",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // 사용 방법 안내 카드
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "사용 방법",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // 단계별 설정 가이드
                Text(
                    text = "1. 키보드 설정에서 코멘토를 활성화하세요\n" +
                           "2. 시스템 설정에서 코멘토를 기본 입력 방법으로 설정하세요\n" +
                           "3. 텍스트 입력 시 AI가 자동완성을 제안합니다\n" +
                           "4. 설정에서 개인화 옵션을 조정할 수 있습니다",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 지원 및 문의 안내 카드
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "지원",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // 지원 문의 안내
                Text(
                    text = "문제가 발생하거나 질문이 있으시면 개발팀에 문의하세요.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
