@echo off

if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem Build script for xbird_util_Primitives
rem
rem $Id$
rem ---------------------------------------------------------------------------

call vcvars32.bat

set INCLUDE_COMMON="%JAVA_HOME%\include"
set INCLUDE_W32="%JAVA_HOME%\include\win32"
set OPT="-O2"

cl %OPT% -I%INCLUDE_COMMON% -I%INCLUDE_W32% -LD -Fexbird_util_Primitives.dll xbird_util_Primitives.cpp
cl %OPT% -I%INCLUDE_COMMON% -I%INCLUDE_W32% -LD -Fexbird_util_lang_SystemUtils.dll xbird_util_lang_SystemUtils.cpp

del *.exp *.lib *.obj

pause

:end