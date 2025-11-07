@REM @echo off
@REM title Library Management System
@REM cd /d "%~dp0"
@REM echo ========================================
@REM echo    LIBRARY MANAGEMENT SYSTEM BUILDER
@REM echo ========================================
@REM echo.

@REM echo Cleaning previous build...
@REM if exist bin rmdir /s /q bin
@REM mkdir bin
@REM mkdir data 2>nul

@REM echo.
@REM echo Compiling all source files at once...
@REM javac -d bin -cp . src\utils\*.java src\models\*.java src\dao\*.java src\services\*.java src\ui\components\*.java src\ui\*.java
@REM if errorlevel 1 goto error

@REM echo.
@REM echo ========================================
@REM echo    COMPILATION SUCCESSFUL!
@REM echo ========================================
@REM echo.
@REM echo Starting Library Management System...
@REM echo.
@REM java -cp bin ui.MainFrame
@REM goto end

@REM :error
@REM echo.
@REM echo ========================================
@REM echo    COMPILATION FAILED!
@REM echo ========================================
@REM echo Please check the Java files for errors.
@REM echo.
@REM echo Java files found:
@REM dir src /s /b *.java
@REM echo.
@REM pause
@REM exit /b 1

@REM :end
@REM pause


@echo off
title Library Management System
cd /d "%~dp0"
echo ========================================
echo    LIBRARY MANAGEMENT SYSTEM BUILDER
echo ========================================
echo.

echo Cleaning previous build...
if exist bin rmdir /s /q bin
mkdir bin
mkdir data 2>nul

echo.
echo Compiling all source files at once...
rem Added -encoding UTF-8 to handle special characters correctly
javac -encoding UTF-8 -d bin -cp . src\utils\*.java src\models\*.java src\dao\*.java src\services\*.java src\ui\components\*.java src\ui\*.java
if errorlevel 1 goto error

echo.
echo ========================================
echo    COMPILATION SUCCESSFUL!
echo ========================================
echo.
echo Starting Library Management System...
echo.
java -cp bin ui.MainFrame
goto end

:error
echo.
echo ========================================
echo    COMPILATION FAILED!
echo ========================================
echo Please check the Java files for errors.
echo.
echo Java files found:
dir src /s /b *.java
echo.
pause
exit /b 1

:end
pause
