@echo off
echo ========================================
echo     HuertoPlan APK Builder
echo ========================================
echo.

echo [1/3] Cleaning project...
call gradlew clean

echo.
echo [2/3] Building Debug APK...
call gradlew assembleDebug

echo.
echo [3/3] Building Release APK...
call gradlew assembleRelease

echo.
echo ========================================
echo        BUILD COMPLETED!
echo ========================================
echo.
echo APK files generated:
echo.
echo Debug APK (with debugging enabled):
echo   Location: app\build\outputs\apk\debug\app-debug.apk
echo   App ID: com.wyllyw.huertoplan.debug
echo   Version: 1.0-DEBUG
echo.
echo Release APK (optimized for production):
echo   Location: app\build\outputs\apk\release\app-release.apk
echo   App ID: com.wyllyw.huertoplan
echo   Version: 1.0
echo.
echo You can install these APK files on your Android device.
echo The Debug version can coexist with the Release version.
echo.
pause