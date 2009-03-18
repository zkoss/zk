@echo off
if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem ZK Test Project Creator
rem
rem Date : 2009/03/18
rem
rem Author : Ryan Wu
rem 
rem Copyright (c) 2009 Potix Corporation. All rights reserved.
rem ---------------------------------------------------------------------------
if "%1"=="" goto DefaultProject

if not exist "%JAVA_HOME%\jre\bin\server\jvm.dll" goto noJDK
if not exist "%ANT_HOME%\lib\ant-junit.jar" goto noAnt
goto NameProject

:noJDK
echo ^>    [Error] Cannot found enviroument attribute : JAVA_HOME
echo               Must point at your Java Development Kit installation.
goto end
:noAnt
echo ^>    [Error] Cannot found enviroument attribute : ANT_HOME
echo               Must point at your Java Development Kit installation.
goto end

:DefaultProject
echo ^>    [INFO] Use default name : Test
set Name=Test
goto create

:NameProject
set Name=%1
goto create

:create
rem if exist "%Name%" goto alreadyexist
mkdir %Name%
xcopy "resource\*.*" "%Name%" /s /C /I /Q /Y  >NUL 2>NUL
cd %Name%
md report
md src
md build
md build\classes
goto end

:alreadyexist
echo ^>    [WARN] Folder already existed
goto end

:end
cd %CURRENT_DIR% 
