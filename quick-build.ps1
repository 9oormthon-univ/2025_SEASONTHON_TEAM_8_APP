Write-Host "Building and running app..." -ForegroundColor Green

Write-Host "Building APK..." -ForegroundColor Yellow
& .\gradlew.bat installDebug

if ($LASTEXITCODE -eq 0) {
    Write-Host "Build successful! Starting app..." -ForegroundColor Green
    adb shell am start -n com.example.myapplication/.MainActivity
    Write-Host "Done! Check your emulator." -ForegroundColor Green
}
else {
    Write-Host "Build failed! Check the errors above." -ForegroundColor Red
} 