@echo off
setlocal

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
