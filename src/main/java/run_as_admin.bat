@echo off
:: Bu dosyayi cift tikla, UAC onayi ver, program acilir.

cd /d "%~dp0"

:: Java var mi kontrol et
where java >nul 2>&1
if errorlevel 1 (
    echo [HATA] Java bulunamadi. Lutfen Java yukleyin.
    echo https://www.java.com
    pause
    exit /b 1
)

:: Jar var mi kontrol et
set JAR=build\libs\SiteBlocker-1.0.jar
if not exist "%JAR%" (
    echo [HATA] Jar bulunamadi: %JAR%
    echo Once su komutu calistirin: gradlew shadowJar
    pause
    exit /b 1
)

:: Yonetici olarak calistir (UAC istegi)
echo Yonetici yetkisi isteniyor...
powershell -Command "Start-Process java -ArgumentList '-jar ""%~dp0%JAR%""' -Verb RunAs"