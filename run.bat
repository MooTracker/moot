@echo off
echo ====================================
echo    MOODTRACKER APPLICATION
echo ====================================
echo.
echo Checking requirements...
echo.

:: Check if XAMPP MySQL is running
echo [1/3] Checking MySQL connection...
ping -n 1 localhost > nul
if errorlevel 1 (
    echo ERROR: Cannot connect to localhost!
    echo Please make sure XAMPP is running.
    pause
    exit /b 1
)
echo ✓ Localhost is reachable

:: Check if port 8080 is available
echo [2/3] Checking if port 8080 is available...
netstat -an | find "8080" | find "LISTENING" > nul
if not errorlevel 1 (
    echo WARNING: Port 8080 is already in use!
    echo Please stop other applications using port 8080.
    pause
)

:: Compile and run
echo [3/3] Starting application...
echo.
echo Compiling project...
mvn clean compile
if errorlevel 1 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo Starting Spring Boot application...
echo Open browser: http://localhost:8080
echo Press Ctrl+C to stop the application
echo.
mvn spring-boot:run
pause
