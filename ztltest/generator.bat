@echo off
if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem Zul Testing Language Generator
rem
rem Date : 2010/03/09
rem
rem Author : jumperchen
rem 
rem Copyright (c) 2010 Potix Corporation. All rights reserved.
rem ---------------------------------------------------------------------------
set CURRENT_DIR=%cd%
if not exist "%JAVA_HOME%\jre\bin\server\jvm.dll" goto noJDK
if not exist "%ANT_HOME%\lib\ant-junit.jar" goto noAnt
goto run

:noJDK
echo ^>     [Error] Cannot found enviroument attribute : JAVA_HOME
echo               Must point at your Java Development Kit installation.
goto end
:noAnt
echo ^>     [Error] Cannot found enviroument attribute : ANT_HOME
echo               Must point at your Java Development Kit installation.
goto end
:run
@cmd /K ant generate

:end
cd %CURRENT_DIR% 