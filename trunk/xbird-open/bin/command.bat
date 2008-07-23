@echo off
setlocal

REM ---------------------------------------------------------
REM command.bat - a script to execute administrative commands
REM  $$Id$$
REM 
REM	 Usage:
REM		-- import a document 'auction.xml' into the collection 'test1'
REM		$ command.bat -col test1 import document /share/benchmark/xmark/auction.xml
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
set optlib=%libdir%\optional

REM You can customize the option for JavaVM externally via setting VMOPTS
set VMOPTS=-Xmx256m -da -server %VMOPTS%
REM set VMOPTS=%VMOPTS% -Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl

set JARS=%distdir%\xbird-open-1.0.jar
REM set JARS=..\build

REM minimum jars
set JARS=%JARS%;%libdir%\commons-logging-1.0.4.jar;%libdir%\xbird-db-1.0.jar;%optlib%\args4j-2.0.4.jar

REM optional jars
set JARS=%JARS%;%optlib%\high-scale-lib-0.8.1.jar;%optlib%\nekohtml-1.9.7.jar;%optlib%\resolver.jar;%optlib%\stax-api-1.0.jar;%optlib%\xercesImpl-2.9.1.jar

REM run it
echo "%JAVA_HOME%\bin\java" -classpath "%JARS%" %VMOPTS% xbird.client.command.CommandInvoker %*
"%JAVA_HOME%\bin\java" -classpath "%JARS%" %VMOPTS% xbird.client.command.CommandInvoker %*

endlocal