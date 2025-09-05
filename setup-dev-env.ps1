# 🛠️ Windows 개발 환경 자동 설정 스크립트
# PowerShell에서 실행: .\setup-dev-env.ps1

Write-Host "🚀 안드로이드 개발 환경 설정을 시작합니다..." -ForegroundColor Green

# Java 설치 확인
Write-Host "`n📋 Java 설치 상태 확인..." -ForegroundColor Yellow
try {
    $javaPath = (Get-Command java).Source
    $javaHome = Split-Path (Split-Path $javaPath)
    Write-Host "✅ Java 발견: $javaHome" -ForegroundColor Green
} catch {
    Write-Host "❌ Java가 설치되지 않았습니다. JDK 17 이상을 설치해주세요." -ForegroundColor Red
    exit 1
}

# Android SDK 경로 확인
Write-Host "`n📋 Android SDK 확인..." -ForegroundColor Yellow
$androidSdkPaths = @(
    "$env:LOCALAPPDATA\Android\Sdk",
    "$env:USERPROFILE\AppData\Local\Android\Sdk",
    "$env:ANDROID_HOME"
)

$androidHome = $null
foreach ($path in $androidSdkPaths) {
    if (Test-Path $path) {
        $androidHome = $path
        Write-Host "✅ Android SDK 발견: $androidHome" -ForegroundColor Green
        break
    }
}

if (-not $androidHome) {
    Write-Host "❌ Android SDK를 찾을 수 없습니다. Android Studio를 설치해주세요." -ForegroundColor Red
    exit 1
}

# VS Code 설정 파일 생성
Write-Host "`n📋 VS Code 설정 파일 생성..." -ForegroundColor Yellow

if (-not (Test-Path ".vscode")) {
    New-Item -ItemType Directory -Path ".vscode"
}

# settings.json 생성 (개인용)
$settingsContent = @"
{
    "java.configuration.updateBuildConfiguration": "interactive",
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-21",
            "path": "$($javaHome -replace '\\', '\\')"
        }
    ],
    "android.home": "$($androidHome -replace '\\', '\\')",
    "gradle.nestedProjects": true,
    "kotlin.languageServer.enabled": true,
    "files.associations": {
        "*.kts": "kotlin"
    },
    "terminal.integrated.env.windows": {
        "ANDROID_HOME": "$($androidHome -replace '\\', '\\')",
        "JAVA_HOME": "$($javaHome -replace '\\', '\\')"
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
"@

$settingsContent | Out-File -FilePath ".vscode\settings.json" -Encoding UTF8
Write-Host "✅ .vscode/settings.json 생성 완료" -ForegroundColor Green

# gradle.properties 수정
Write-Host "`n📋 Gradle 설정 업데이트..." -ForegroundColor Yellow
$gradlePropsPath = "gradle.properties"
if (Test-Path $gradlePropsPath) {
    $content = Get-Content $gradlePropsPath
    $newContent = $content | ForEach-Object {
        if ($_ -match "org\.gradle\.java\.home=") {
            "org.gradle.java.home=$($javaHome -replace '\\', '/')"
        } else {
            $_
        }
    }
    $newContent | Out-File -FilePath $gradlePropsPath -Encoding UTF8
    Write-Host "✅ gradle.properties 업데이트 완료" -ForegroundColor Green
}

# 에뮬레이터 확인
Write-Host "`n📋 에뮬레이터 상태 확인..." -ForegroundColor Yellow
try {
    $devices = adb devices
    if ($devices -match "emulator") {
        Write-Host "✅ 에뮬레이터가 실행 중입니다" -ForegroundColor Green
    } else {
        Write-Host "⚠️ 에뮬레이터를 실행해주세요" -ForegroundColor Yellow
    }
} catch {
    Write-Host "⚠️ ADB를 찾을 수 없습니다. Android SDK 설치를 확인해주세요." -ForegroundColor Yellow
}

Write-Host "`n🎉 개발 환경 설정이 완료되었습니다!" -ForegroundColor Green
Write-Host "이제 다음 명령어로 앱을 빌드하고 실행할 수 있습니다:" -ForegroundColor Cyan
Write-Host "  ./gradlew installDebug" -ForegroundColor White 