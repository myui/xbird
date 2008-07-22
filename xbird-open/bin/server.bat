@echo off
setlocal

REM ---------------------------------------------------------
REM server.sh - Start script for XBird server 
REM  $$Id$$
REM 
REM  Copyright (C) 2006-2008, Makoto YUI and Project XBird
REM ---------------------------------------------------------

if not "%JAVA_HOME%" == "" goto gotJavaHome

echo Java environment not found. Please set
echo your JAVA_HOME environment variable to
echo the home of your JDK.
goto :eof

:gotJavaHome

set libdir=..\lib
set distdir=..\target

REM You can customize the option for JavaVM externally via setting VMOPTS
set VMOPTS=-Xmx256m -da -server %VMOPTS%

set JARS=%distdir%\xbird-open-1.0.jar
REM set JARS=..\build

REM minimum jars
set JARS=%JARS%;%libdir%\commons-logging-1.0.4.jar;%libdir%\xbird-db-1.0.jar

REM optional jars
set optlib=%libdir%\optional
set JARS=%JARS%;%optlib%\args4j-2.0.4.jar;%optlib%\high-scale-lib-0.8.1.jar;%optlib%\nekohtml-1.9.7.jar;%optlib%\resolver.jar;%optlib%\stax-api-1.0.jar;%optlib%\xercesImpl-2.9.1.jar

REM run it
echo "%JAVA_HOME%\bin\java" -classpath "%JARS%" %VMOPTS% xbird.server.Server
"%JAVA_HOME%\bin\java" -classpath "%JARS%" %VMOPTS% xbird.server.Server

endlocal