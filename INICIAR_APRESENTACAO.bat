@echo off
setlocal

set "PROJECT_DIR=%~dp0"
set "ANDROID_SDK=%LOCALAPPDATA%\Android\Sdk"
set "ADB=%ANDROID_SDK%\platform-tools\adb.exe"
set "EMULATOR=%ANDROID_SDK%\emulator\emulator.exe"
set "AVD=Pixel_7"
set "PACKAGE=com.MatheusFoganholi.marquesautodetail"
set "APK=%PROJECT_DIR%app\build\outputs\apk\debug\app-debug.apk"

if exist "D:\Android Studio\jbr" set "JAVA_HOME=D:\Android Studio\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo [1/6] Acordando a API no Render...
start "" "https://marques-autodetail-api.onrender.com/api/empresas"

if not exist "%ADB%" (
  echo ERRO: adb nao encontrado em %ADB%
  pause
  exit /b 1
)

if not exist "%EMULATOR%" (
  echo ERRO: emulator nao encontrado em %EMULATOR%
  pause
  exit /b 1
)

echo [2/6] Verificando o emulador...
"%ADB%" start-server >nul 2>&1
"%ADB%" get-state 2>nul | findstr /I "device" >nul
if errorlevel 1 (
  echo Iniciando o emulador %AVD% em cold boot...
  start "" "%EMULATOR%" "@%AVD%" -no-snapshot-load
)

echo [3/6] Aguardando o Android iniciar...
"%ADB%" wait-for-device
:wait_boot
for /f "delims=" %%A in ('"%ADB%" shell getprop sys.boot_completed 2^>nul') do set "BOOT=%%A"
if not "%BOOT%"=="1" (
  timeout /t 3 /nobreak >nul
  goto wait_boot
)

echo [4/6] Compilando o aplicativo...
cd /d "%PROJECT_DIR%"
call gradlew.bat :app:assembleDebug
if errorlevel 1 (
  echo ERRO: a compilacao falhou.
  pause
  exit /b 1
)

echo [5/6] Instalando o APK...
"%ADB%" install -r "%APK%"
if errorlevel 1 (
  echo ERRO: nao foi possivel instalar o APK.
  pause
  exit /b 1
)

echo [6/6] Abrindo o Marques AutoDetail...
"%ADB%" shell monkey -p %PACKAGE% -c android.intent.category.LAUNCHER 1 >nul

echo.
echo Aplicativo aberto. Aguarde o JSON aparecer no navegador antes de demonstrar os dados online.
pause
