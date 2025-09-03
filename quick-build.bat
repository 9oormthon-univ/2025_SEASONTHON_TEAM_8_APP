@echo off
echo Building and running app...

echo Building APK...
call gradlew.bat installDebug

if %errorlevel% equ 0 (
    echo Build successful! Starting app...
    adb shell am start -n com.example.myapplication/.MainActivity
    echo Done! Check your emulator.
) else (
    echo Build failed! Check the errors above.
    pause
) 