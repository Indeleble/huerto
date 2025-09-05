# HuertoPlan APK Builder Script
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "     HuertoPlan APK Builder" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[1/3] Cleaning project..." -ForegroundColor Yellow
& .\gradlew clean

Write-Host ""
Write-Host "[2/3] Building Debug APK..." -ForegroundColor Yellow
& .\gradlew assembleDebug

Write-Host ""
Write-Host "[3/3] Building Release APK..." -ForegroundColor Yellow  
& .\gradlew assembleRelease

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "        BUILD COMPLETED!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

Write-Host "APK files generated:" -ForegroundColor White
Write-Host ""

if (Test-Path "app\build\outputs\apk\debug\app-debug.apk") {
    $debugSize = [math]::Round((Get-Item "app\build\outputs\apk\debug\app-debug.apk").Length / 1MB, 2)
    Write-Host "✅ Debug APK (with debugging enabled):" -ForegroundColor Green
    Write-Host "   📍 Location: app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor Gray
    Write-Host "   📱 App ID: com.wyllyw.huertoplan.debug" -ForegroundColor Gray
    Write-Host "   📦 Version: 1.0-DEBUG" -ForegroundColor Gray
    Write-Host "   💾 Size: $debugSize MB" -ForegroundColor Gray
} else {
    Write-Host "❌ Debug APK generation failed!" -ForegroundColor Red
}

Write-Host ""

if (Test-Path "app\build\outputs\apk\release\app-release.apk") {
    $releaseSize = [math]::Round((Get-Item "app\build\outputs\apk\release\app-release.apk").Length / 1MB, 2)
    Write-Host "✅ Release APK (optimized for production):" -ForegroundColor Green
    Write-Host "   📍 Location: app\build\outputs\apk\release\app-release.apk" -ForegroundColor Gray
    Write-Host "   📱 App ID: com.wyllyw.huertoplan" -ForegroundColor Gray
    Write-Host "   📦 Version: 1.0" -ForegroundColor Gray
    Write-Host "   💾 Size: $releaseSize MB" -ForegroundColor Gray
} else {
    Write-Host "❌ Release APK generation failed!" -ForegroundColor Red
}

Write-Host ""
Write-Host "📋 Installation Notes:" -ForegroundColor Cyan
Write-Host "• You can install these APK files on your Android device" -ForegroundColor White
Write-Host "• The Debug version can coexist with the Release version" -ForegroundColor White
Write-Host "• Use Debug APK for testing and development" -ForegroundColor White
Write-Host "• Use Release APK for distribution" -ForegroundColor White
Write-Host ""

Read-Host "Press Enter to continue"