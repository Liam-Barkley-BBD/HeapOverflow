@echo off
setlocal

:: Check for OpenSSL
where openssl >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: OpenSSL is not installed or not in PATH.
    exit /b 1
)

:: Check for Java
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH.
    exit /b 1
)

:: Decrypt .env.enc to .env
openssl enc -d -aes-256-cbc -salt -pbkdf2 -in .env.enc -out .env

:: Load environment variables
for /f "tokens=1,2 delims==" %%a in (.env) do (
    if not "%%a"=="" (
        set "%%a=%%b"
    )
)

:: Export environment variables to PowerShell session
for /f "tokens=1,2 delims==" %%a in (.env) do (
    if not "%%a"=="" (
        powershell -Command "[System.Environment]::SetEnvironmentVariable('%%a', '%%b', [System.EnvironmentVariableTarget]::Process)"
    )
)

:: Run the JAR file
java -jar cli-1.0.0-SNAPSHOT.jar
