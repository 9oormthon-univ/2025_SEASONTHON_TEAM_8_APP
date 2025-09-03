/**
 * 코멘토 앱의 드로어 메뉴 컴포넌트
 * 
 * 이 컴포넌트는 앱의 왼쪽에서 슬라이드하여 열리는 사이드바 메뉴를 구현합니다.
 * 
 * 주요 기능:
 * - 네비게이션 메뉴 아이템들 (홈, 설정, 키보드 테스트, 도움말)
 * - 현재 선택된 화면 하이라이트
 * - 각 메뉴 아이템 클릭 시 화면 전환 및 드로어 자동 닫기
 * - 재사용 가능한 독립적인 컴포넌트
 * 
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    selectedScreen: String,           // 현재 선택된 화면 식별자
    onScreenSelected: (String) -> Unit,  // 화면 선택 시 호출되는 콜백
    onDrawerClose: () -> Unit        // 드로어 닫기 시 호출되는 콜백
) {
    // 코루틴 스코프 생성 (드로어 애니메이션을 위한 비동기 작업)
    val scope = rememberCoroutineScope()
    
    // 드로어 시트 (모달 형태의 드로어)
    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(12.dp))
        
        // 드로어 헤더 (앱 브랜드명)
        Text(
            text = "코멘토",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Divider()
        
        // 홈 메뉴 아이템
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "홈") },
            label = { Text("홈") },
            selected = selectedScreen == "home",
            onClick = { 
                onScreenSelected("home")
                scope.launch { onDrawerClose() }
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        
        // 설정 메뉴 아이템
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "설정") },
            label = { Text("설정") },
            selected = selectedScreen == "settings",
            onClick = { 
                onScreenSelected("settings")
                scope.launch { onDrawerClose() }
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        
        // 키보드 테스트 메뉴 아이템
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "키보드 테스트") },
            label = { Text("키보드 테스트") },
            selected = selectedScreen == "keyboard_test",
            onClick = { 
                onScreenSelected("keyboard_test")
                scope.launch { onDrawerClose() }
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        
        // 도움말 메뉴 아이템
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "도움말") },
            label = { Text("도움말") },
            selected = selectedScreen == "help",
            onClick = { 
                onScreenSelected("help")
                scope.launch { onDrawerClose() }
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
