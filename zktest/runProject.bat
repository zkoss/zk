@echo off
if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem ZK Test Project Runner
rem
rem Date : 2009/03/18
rem
rem Author : Ryan Wu
rem 
rem Copyright (c) 2009 Potix Corporation. All rights reserved.
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
@cmd /K ant junit reports

:end
cd %CURRENT_DIR% 