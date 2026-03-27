@echo off
:: SiteBlocker - Derle ve Yonetici olarak calistir
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

where javac >nul 2>&1
if errorlevel 1 (
    echo [HATA] javac bulunamadi. JDK kurulu degil.
    echo https://adoptium.net
    pause
    exit /b 1
)

:: Derlenmemisse derle
if not exist SiteBlocker.class (
    echo Derleniyor...
    javac SiteBlocker.java
    if errorlevel 1 (
        echo [HATA] Derleme basarisiz.
        pause
        exit /b 1
    )
    echo Derleme tamamlandi.
)

:: Yonetici olarak calistir (UAC istegi)
echo Yonetici yetkisi isteniyor...
powershell -Command "Start-Process java -ArgumentList 'SiteBlocker' -WorkingDirectory '%cd%' -Verb RunAs"
