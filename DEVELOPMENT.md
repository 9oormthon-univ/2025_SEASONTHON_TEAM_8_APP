# 👥 팀 개발 환경 설정 가이드

## 🚀 빠른 시작

### 1. **자동 설정 스크립트 실행**

#### Windows (PowerShell)
```powershell
.\setup-dev-env.ps1
```

#### macOS/Linux (Bash)
```bash
chmod +x setup-dev-env.sh && ./setup-dev-env.sh
```

### 2. **수동 설정** (스크립트 실행이 안 되는 경우)

#### 필수 프로그램 설치
1. **JDK 17 이상** 설치
2. **Android Studio** 설치 (SDK 포함)
3. **VS Code** + 확장 프로그램들 설치

#### VS Code 설정
1. `.vscode/settings.example.json`을 복사하여 `.vscode/settings.json` 생성
2. 본인 환경에 맞게 Java/Android SDK 경로 수정

## 🔄 개발 워크플로우

### **VS Code에서 개발하기**

#### 빠른 명령어 (터미널)
```bash
# 빌드 + 설치 + 실행 (한 번에)
./gradlew installDebug && adb shell am start -n com.example.myapplication/.MainActivity

# 실시간 로그 확인 (별도 터미널)
adb logcat | grep "myapplication"

# 앱 강제 종료 (필요시)
adb shell am force-stop com.example.myapplication
```

#### VS Code Tasks 사용
1. **Ctrl + Shift + P** → `Tasks: Run Task`
2. **Install Debug APK** 선택 (가장 자주 사용)

### **Android Studio에서 개발하기**

#### 장점
- Compose 실시간 미리보기
- 완벽한 디버깅 지원
- 에뮬레이터 자동 관리

#### 사용 시기
- UI 디자인 작업
- 복잡한 디버깅 필요시
- Compose 미리보기 필요시

## 🐛 디버깅 가이드

### 1. **로그 기반 디버깅** (추천)
```kotlin
// 코드에 로그 추가
import android.util.Log

class AIKeyboardService : InputMethodService() {
    companion object {
        private const val TAG = "AIKeyboard"
    }
    
    override fun onCreateInputView(): View {
        Log.d(TAG, "키보드 생성: ${System.currentTimeMillis()}")
        return rootLayout
    }
}
```

```bash
# 실시간 로그 확인
adb logcat -s AIKeyboard:D MainActivity:D
```

### 2. **단계별 디버깅**
1. **문제 재현**: 에뮬레이터에서 버그 재현
2. **로그 확인**: 관련 로그 메시지 확인
3. **코드 수정**: 문제 지점 수정
4. **테스트**: `./gradlew installDebug`로 재테스트

### 3. **성능 디버깅**
```bash
# 메모리 사용량 확인
adb shell dumpsys meminfo com.example.myapplication

# CPU 사용량 확인  
adb shell top -p $(adb shell pidof com.example.myapplication)
```

## 🔀 Git 브랜치 전략

### **브랜치 규칙**
```bash
# 새 기능 개발
git checkout develop
git pull origin develop
git checkout -b feature/키보드-UI-개선

# 개발 완료 후
git add .
git commit -m "feat: 키보드 UI 개선"
git push origin feature/키보드-UI-개선
```

### **커밋 메시지 규칙**
```
타입: 간단한 설명

상세 설명 (필요시)

타입:
- feat: 새로운 기능
- fix: 버그 수정  
- refactor: 코드 리팩토링
- style: 코드 포맷팅
- docs: 문서 수정
```

## 🛠️ 트러블슈팅

### **빌드 오류**
```bash
# Gradle 캐시 클리어
./gradlew clean

# 의존성 새로고침
./gradlew --refresh-dependencies
```

### **에뮬레이터 문제**
```bash
# 에뮬레이터 재시작
adb kill-server && adb start-server

# 에뮬레이터 목록 확인
emulator -list-avds
```

### **VS Code 문제**
1. **Ctrl + Shift + P** → `Java: Reload Projects`
2. **Ctrl + Shift + P** → `Developer: Reload Window`

## 📱 키보드 테스트 가이드

### **키보드 활성화**
1. 에뮬레이터 **Settings** → **System** → **Languages & input**
2. **Virtual keyboard** → **Manage keyboards**
3. **코멘토** 키보드 활성화

### **기능별 테스트**
1. **한글 입력**: 기본 자모 + 쉬프트로 쌍자음
2. **영어 입력**: QWERTY + 대소문자 전환
3. **AI 기능**: 리라이팅, 맞춤법, 일정추가 버튼

## 🎯 개발 팁

### **효율적인 개발**
1. **UI 작업**: VS Code로 Compose 코드 편집
2. **미리보기**: Android Studio로 Compose Preview 확인
3. **키보드 로직**: VS Code + 로그 디버깅

### **코드 품질**
- **자동 포맷팅**: 저장 시 자동 적용 (설정됨)
- **Import 정리**: 저장 시 자동 정리 (설정됨)
- **린트 검사**: `./gradlew lint` 정기 실행

### **팀 협업**
- 개인 설정 파일은 Git에 커밋하지 않음
- 공통 설정은 `.example` 파일로 공유
- 환경 설정 스크립트로 빠른 셋업 지원 