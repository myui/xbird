#!/bin/sh

echo "---------------------------------------------------------------------------"
echo "Build script for xbird_util_Primitives"
echo 
echo "$Id$"
echo "---------------------------------------------------------------------------"

export OPT="-O2 -fpic"

g++ ${OPT} -shared -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux \
	xbird_util_Primitives.cpp -o libxbird_util_Primitives.so

g++ ${OPT} -shared -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux \
	xbird_util_lang_SystemUtils.cpp -o libxbird_util_lang_SystemUtils.so



