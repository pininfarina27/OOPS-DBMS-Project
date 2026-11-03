@echo off
echo ========================================
echo   Compiling Car Rental System...
echo ========================================
echo.

if not exist "out" mkdir out

javac -cp "lib/*" -d out src/*.java

if %errorlevel% neq 0 (
    echo.
    echo   Compilation FAILED!
    echo.
    pause
    exit /b 1
)

echo.
echo   Compilation successful!
echo.
pause
