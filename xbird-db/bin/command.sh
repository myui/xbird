#!/bin/sh

VMARGS="-Xms1024m -Xmx1024m -ea"
XBIRD_HOME="D:\workspace\xbird"

LCP="$XBIRD_HOME\lib\optional\args4j-2.0.4.jar;$XBIRD_HOME\lib\commons-logging-1.0.4.jar"

java $VMARGS -cp "$LCP;$XBIRD_HOME\target\classes" xbird.client.tools.CommandInvoker $@
