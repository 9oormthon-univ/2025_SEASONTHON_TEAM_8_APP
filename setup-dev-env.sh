#!/bin/bash

# 🛠️ macOS/Linux 개발 환경 자동 설정 스크립트
# 실행: chmod +x setup-dev-env.sh && ./setup-dev-env.sh

echo "🚀 안드로이드 개발 환경 설정을 시작합니다..."

# Java 설치 확인
echo ""
echo "📋 Java 설치 상태 확인..."
if command -v java &> /dev/null; then
    JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
    echo "✅ Java 발견: $JAVA_HOME"
else
    echo "❌ Java가 설치되지 않았습니다. JDK 17 이상을 설치해주세요."
    exit 1
fi

# Android SDK 경로 확인
echo ""
echo "📋 Android SDK 확인..."
ANDROID_SDK_PATHS=(
    "$HOME/Library/Android/sdk"
    "$HOME/Android/Sdk"
    "$ANDROID_HOME"
)

ANDROID_HOME_PATH=""
for path in "${ANDROID_SDK_PATHS[@]}"; do
    if [ -d "$path" ]; then
        ANDROID_HOME_PATH="$path"
        echo "✅ Android SDK 발견: $ANDROID_HOME_PATH"
        break
    fi
done

if [ -z "$ANDROID_HOME_PATH" ]; then
    echo "❌ Android SDK를 찾을 수 없습니다. Android Studio를 설치해주세요."
    exit 1
fi

# VS Code 설정 파일 생성
echo ""
echo "📋 VS Code 설정 파일 생성..."

mkdir -p .vscode

# settings.json 생성 (개인용)
cat > .vscode/settings.json << EOF
{
    "java.configuration.updateBuildConfiguration": "interactive",
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-21",
            "path": "$JAVA_HOME"
        }
    ],
    "android.home": "$ANDROID_HOME_PATH",
    "gradle.nestedProjects": true,
    "kotlin.languageServer.enabled": true,
    "files.associations": {
        "*.kts": "kotlin"
    },
    "terminal.integrated.env.osx": {
        "ANDROID_HOME": "$ANDROID_HOME_PATH",
        "JAVA_HOME": "$JAVA_HOME"
    },
    "terminal.integrated.env.linux": {
        "ANDROID_HOME": "$ANDROID_HOME_PATH",
        "JAVA_HOME": "$JAVA_HOME"
    },
    "files.exclude": {
        "**/build": true,
        "**/.gradle": true
    },
    "kotlin.formatter": "ktlint",
    "editor.formatOnSave": true,
    "editor.codeActionsOnSave": {
        "source.organizeImports": "explicit"
    }
}
EOF

echo "✅ .vscode/settings.json 생성 완료"

# gradle.properties 수정
echo ""
echo "📋 Gradle 설정 업데이트..."
if [ -f "gradle.properties" ]; then
    # macOS/Linux에서 sed 사용
    sed -i.bak "s|org\.gradle\.java\.home=.*|org.gradle.java.home=$JAVA_HOME|" gradle.properties
    echo "✅ gradle.properties 업데이트 완료"
fi

# 에뮬레이터 확인
echo ""
echo "📋 에뮬레이터 상태 확인..."
if command -v adb &> /dev/null; then
    DEVICES=$(adb devices | grep "emulator")
    if [ -n "$DEVICES" ]; then
        echo "✅ 에뮬레이터가 실행 중입니다"
    else
        echo "⚠️ 에뮬레이터를 실행해주세요"
    fi
else
    echo "⚠️ ADB를 찾을 수 없습니다. Android SDK 설치를 확인해주세요."
fi

echo ""
echo "🎉 개발 환경 설정이 완료되었습니다!"
echo "이제 다음 명령어로 앱을 빌드하고 실행할 수 있습니다:"
echo "  ./gradlew installDebug" 