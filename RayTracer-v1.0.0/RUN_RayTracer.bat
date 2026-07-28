@echo off
cd /d "%~dp0"

where java >nul 2>&1
if errorlevel 1 (
    echo Java 21 or newer was not found.
    echo.
    echo Please install Java and make sure it is added to PATH.
    pause
    exit /b 1
)

java -jar RayTracer.jar

pause