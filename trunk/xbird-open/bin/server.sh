#!/bin/sh

# ---------------------------------------------------------
# server.sh - Start script for XBird server 
#  $$Id$$
#
#  Copyright (C) 2006-2008, Makoto YUI and Project XBird
# ---------------------------------------------------------

if [ "$JAVA_HOME" = "" ]; then
   echo Java environment not found. Please set
   echo your JAVA_HOME environment variable to
   echo the home of your JDK.
   exit 1
fi

libdir=../lib
distdir=../target

# You can customize the option for JavaVM externally via setting VMOPTS
VMOPTS="-Xmx256m -da -server $VMOPTS"

JARS=$distdir/xbird-open-1.0.jar
#JARS=../build

# minimum jars
JARS=$JARS:$libdir/commons-logging-1.0.4.jar:$libdir/xbird-db-1.0.jar

# optional jars
optlib=$libdir/optional
JARS=$JARS:$optlib/args4j-2.0.4.jar:$optlib/high-scale-lib-0.8.1.jar:$optlib/nekohtml-1.9.7.jar:$optlib/resolver.jar:$optlib/stax-api-1.0.jar:$optlib/xercesImpl-2.9.1.jar

# run it
echo "java -classpath $JARS $VMOPTS xbird.server.Server"
java -classpath $JARS $VMOPTS xbird.server.Server
