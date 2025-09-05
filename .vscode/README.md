# 🛠️ VS Code 개발 환경 설정 가이드

## 📋 필수 확장 프로그램

VS Code에서 다음 확장 프로그램들을 설치해주세요:

```bash
# 명령어로 일괄 설치
code --install-extension vscjava.vscode-java-pack
code --install-extension mathiasfrohlich.Kotlin
code --install-extension adelphes.android-dev-ext
code --install-extension ms-vscode.gradle-language
```

또는 VS Code Extensions 탭에서 검색하여 설치:
- **Extension Pack for Java** (vscjava.vscode-java-pack)
- **Kotlin Language** (mathiasfrohlich.Kotlin)
- **Android iOS Emulator** (adelphes.android-dev-ext)
- **Gradle Language Support** (ms-vscode.gradle-language)

## ⚙️ 개인 설정 파일 생성

1. `settings.example.json` 파일을 복사하여 `settings.json` 생성:
```bash
cp .vscode/settings.example.json .vscode/settings.json
```

2. `settings.json`에서 다음 경로들을 본인 환경에 맞게 수정:
   - **JAVA_HOME_경로**: Java 설치 경로
   - **ANDROID_SDK_경로**: Android SDK 설치 경로

### Windows 경로 예시:
```json
"JAVA_HOME": "C:\\Program Files\\Java\\jdk-21"
"ANDROID_HOME": "C:\\Users\\사용자명\\AppData\\Local\\Android\\Sdk"
```

### macOS 경로 예시:
```json
"JAVA_HOME": "/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"
"ANDROID_HOME": "/Users/사용자명/Library/Android/sdk"
```

## 🚀 빠른 개발 명령어

### VS Code Tasks (Ctrl + Shift + P → Tasks: Run Task)
- **Install Debug APK**: 빌드 + 에뮬레이터 설치
- **Build Debug APK**: 빌드만
- **Clean Build**: 문제 발생 시
- **Run Tests**: 테스트 실행

### 터미널 명령어
```bash
# 빌드 + 설치 + 실행
./gradlew installDebug && adb shell am start -n com.example.myapplication/.MainActivity

# 실시간 로그 확인
adb logcat | grep "myapplication"

# 에뮬레이터 상태 확인
adb devices
```

## 🐛 디버깅 팁

1. **로그 기반 디버깅**: 코드에 `Log.d(TAG, "메시지")` 추가
2. **실시간 로그**: 별도 터미널에서 `adb logcat` 실행
3. **앱 재시작**: `adb shell am force-stop com.example.myapplication` 