@echo off
REM ============================================================
REM  Executa o projeto no Windows com dois cliques.
REM  Na primeira vez, baixa o Maven automaticamente (programa que
REM  baixa as bibliotecas do Spring e roda o projeto).
REM ============================================================
cd /d "%~dp0"
set "MVN_VER=3.9.11"
set "MVN_DIR=%USERPROFILE%\.m2\apache-maven-%MVN_VER%"

if not exist "%MVN_DIR%\bin\mvn.cmd" (
    echo Baixando o Maven %MVN_VER% pela primeira vez. Aguarde...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; [Net.ServicePointManager]::SecurityProtocol='Tls12'; New-Item -ItemType Directory -Force -Path \"$env:USERPROFILE\.m2\" | Out-Null; Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MVN_VER%/apache-maven-%MVN_VER%-bin.zip' -OutFile \"$env:TEMP\maven.zip\"; Expand-Archive -Path \"$env:TEMP\maven.zip\" -DestinationPath \"$env:USERPROFILE\.m2\" -Force"
)

if not exist "%MVN_DIR%\bin\mvn.cmd" (
    echo.
    echo ERRO: nao foi possivel baixar o Maven. Verifique sua internet.
    pause
    exit /b 1
)

echo Iniciando o sistema... quando aparecer "Started GaragemApplication",
echo abra no navegador: http://localhost:8080/pessoas
echo Para parar o sistema, aperte Ctrl+C nesta janela.
echo.
call "%MVN_DIR%\bin\mvn.cmd" spring-boot:run
pause
