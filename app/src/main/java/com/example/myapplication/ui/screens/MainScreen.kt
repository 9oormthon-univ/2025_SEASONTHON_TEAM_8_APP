/**
 * 코멘토 앱의 메인 화면
 *
 * 이 화면은 앱의 핵심 !!!!네비게이션 구조!!!!!를 제공합니다.
 *
 * 주요 기능:
 * - 드로어 메뉴 (사이드바) 구현
 * - 상단 앱바와 햄버거 메뉴 버튼
 * - 여러 하위 화면들 간의 전환 관리
 * - 화면 상태 관리 및 드로어 열기/닫기
 *
 * @author SEASONTHON TEAM 8
 * @version 1.0.0
 */
package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.auth.TokenManager
import com.example.myapplication.ui.components.AppDrawer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // Context 가져오기
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // 드로어의 열림/닫힘 상태를 관리
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // 코루틴 스코프 생성 (드로어 애니메이션을 위한 비동기 작업)
    val scope = rememberCoroutineScope()

    // 현재 선택된 화면을 추적하는 상태 변수
    var selectedScreen by remember { mutableStateOf("onboarding") }

    // 온보딩 완료 상태
    var isOnboardingCompleted by remember { mutableStateOf(false) }

    // 로그인 상태 확인
    val isLoggedIn by remember { mutableStateOf(tokenManager.isLoggedIn()) }

    // 온보딩과 웰컴 화면에서는 드로어 없이 풀스크린
    if (selectedScreen == "onboarding") {
        // 온보딩 화면만 표시
        OnboardingScreen(
                onGetStarted = { selectedScreen = "welcome" },
                onAuthSuccess = { email, name, profileUrl ->
                    // Google 인증 성공 시 사용자 정보 처리
                    println("Google 인증 성공!")
                    println("이메일: $email")
                    println("이름: $name")
                    println("프로필 사진: $profileUrl")

                    // 로컬 사용자 정보는 OnboardingScreen에서 이미 저장됨
                    // 여기서는 추가적인 처리가 필요한 경우에만 구현

                    // 웰컴 화면으로 이동
                    selectedScreen = "welcome"
                }
        )
    } else if (selectedScreen == "welcome") {
        // 웰컴 화면만 표시
        WelcomScreen(
                onGetStarted = {
                    selectedScreen = "home"
                    isOnboardingCompleted = true
                }
        )
    } else {
        // 일반 화면들 - 드로어와 함께
        ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    // 드로어 내용 (AppDrawer 컴포넌트 사용)
                    AppDrawer(
                            selectedScreen = selectedScreen,
                            onScreenSelected = { screen -> selectedScreen = screen },
                            onDrawerClose = { scope.launch { drawerState.close() } }
                    )
                }
        ) {
            // 메인 콘텐츠 영역
            Scaffold { paddingValues ->
                // 콘텐츠 영역 (상단 앱바의 패딩 고려)
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    // 선택된 화면에 따라 적절한 컴포넌트 렌더링
                    when (selectedScreen) {
                        "home" ->
                                HomeScreen(
                                        onNavigateToConversationAnalysis = {
                                            selectedScreen = "conversation_analysis"
                                        },
                                        onNavigateToKeyboardSettings = {
                                            selectedScreen = "keyboard_settings"
                                        },
                                        onNavigateToGroupAnalysis = {
                                            selectedScreen = "group_analysis"
                                        },
                                        onNavigateToPersonalAnalysis = {
                                            selectedScreen = "personal_analysis"
                                        }
                                ) // 홈 화면
                        "conversation_analysis" ->
                                ConversationAnalysisScreen(
                                        onBackClick = { selectedScreen = "home" }
                                ) // 대화 분석 화면
                        "group_analysis" ->
                                GroupConversationAnalysisScreen(
                                        onNavigateBack = { selectedScreen = "home" }
                                ) // 단체 톡방 분석 화면
                        "personal_analysis" ->
                                PersonalConversationAnalysisScreen(
                                        onNavigateBack = { selectedScreen = "home" }
                                ) // 개인 톡방 분석 화면
                        "keyboard_settings" ->
                                KeyboardSettingsScreen(
                                        onBackClick = { selectedScreen = "home" }
                                ) // 키보드 설정 화면
                        "settings" -> SettingsScreen() // 설정 화면
                        "keyboard_test" -> KeyboardTestScreen() // 키보드 테스트 화면
                        "help" -> HelpScreen() // 도움말 화면
                    }
                }
            }
        }
    }
}
