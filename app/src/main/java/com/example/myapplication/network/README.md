# Google OAuth API 사용법

이 프로젝트는 `https://textmate.zapto.org` API를 사용하여 Google OAuth 인증을 처리합니다.

## API 엔드포인트

### GET /auth/google

Google OAuth 인증을 처리합니다.

**Parameters:**
- `code` (string, required): Google OAuth 인증 코드

**Response:**
```json
{
  "accessToken": "string",
  "refreshToken": "string"
}
```

## 사용법

### 1. ViewModel 사용

```kotlin
class MyViewModel : ViewModel() {
    private val authViewModel: AuthViewModel = viewModel()
    
    fun authenticateUser(code: String) {
        authViewModel.authenticateWithGoogle(code)
    }
}
```

### 2. Composable에서 사용

```kotlin
@Composable
fun LoginScreen() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    
    when (authState) {
        is AuthState.Loading -> {
            // 로딩 UI
        }
        is AuthState.Success -> {
            // 성공 시 토큰 저장
            val accessToken = authState.authResponse.accessToken
            val refreshToken = authState.authResponse.refreshToken
        }
        is AuthState.Error -> {
            // 에러 처리
        }
        else -> {
            // 기본 상태
        }
    }
}
```

### 3. GoogleAuthButton 컴포넌트 사용

```kotlin
@Composable
fun LoginScreen() {
    GoogleAuthButton(
        onAuthSuccess = { accessToken, refreshToken ->
            // 인증 성공 처리
        },
        onAuthError = { errorMessage ->
            // 에러 처리
        }
    )
}
```

## 파일 구조

```
app/src/main/java/com/example/myapplication/
├── model/
│   └── AuthResponse.kt          # API 응답 모델
├── network/
│   ├── ApiService.kt            # API 서비스 인터페이스
│   └── NetworkModule.kt         # 네트워크 설정
├── repository/
│   └── AuthRepository.kt        # 데이터 레이어
├── viewmodel/
│   └── AuthViewModel.kt         # UI 상태 관리
└── ui/components/
    └── GoogleAuthButton.kt      # UI 컴포넌트
```

## 주의사항

1. 실제 Google OAuth 플로우를 구현할 때는 Google Sign-In SDK를 사용해야 합니다.
2. 현재 구현은 예시용이며, 실제 프로덕션에서는 보안을 고려한 구현이 필요합니다.
3. 토큰은 안전하게 저장해야 합니다 (SharedPreferences, EncryptedSharedPreferences 등).