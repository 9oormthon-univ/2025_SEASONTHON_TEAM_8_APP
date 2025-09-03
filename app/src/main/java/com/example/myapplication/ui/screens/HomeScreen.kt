/**
 * 코멘토 앱의 홈 화면
 * 
 * 이 화면은 사용자가 앱을 처음 실행했을 때 보게 되는 메인 화면입니다.
 * 
 * 주요 기능:
 * - 앱 소개 및 브랜딩 (로고, 제목, 설명)
 * - 주요 기능 목록 표시
 * - 빠른 액세스 버튼들 (키보드 설정, 시스템 설정)
 * - 사용자 친화적인 첫인상 제공
 * 
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun HomeScreen() {
    // 현재 컨텍스트 가져오기 (Intent 실행을 위해)
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 앱 아이콘 또는 로고 (이모지 사용)
        Text(
            text = "⌨️",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 앱 제목 (브랜드명)
        Text(
            text = "코멘토",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // 앱 설명 (간단한 소개)
        Text(
            text = "이곳은 메인화면",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
       
        
    }
}
