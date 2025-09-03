/**
 * 코멘토 앱의 메인 액티비티
 * 
 * 이 액티비티는 앱이 시작될 때 가장 먼저 실행되는 진입점입니다.
 * 
 * 주요 역할:
 * - 앱의 테마와 기본 설정을 초기화
 * - MainScreen을 호출하여 드로어 메뉴가 있는 메인 화면을 표시
 * - 앱의 생명주기 관리
 * 
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    /**
     * 액티비티가 생성될 때 호출되는 메서드
     * 
     * @param savedInstanceState 이전 상태 정보 (있는 경우)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Jetpack Compose를 사용하여 UI 설정
        setContent {
            // 앱의 테마 적용
            MyApplicationTheme {
                // 전체 화면을 덮는 Surface 생성
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 메인 화면 컴포넌트 호출
                    MainScreen()
                }
            }
        }
    }
}
