#!/bin/sh

USAGE="Usage: $0 processId"

if [ $# -ne 1 ]; then
   echo $USAGE
   echo $*
   exit 1
fi

PROCESS_PID=$1

LOG_FILE="memusage.csv"

echo "ElapsedTime(sec),VmSize(kB allocated),VmRSS(kB used)" > $LOG_FILE

ELAPSED_TIME=0
PERIOD=2        # seconds

while :
do
 if [ -d /proc/$PROCESS_PID ] ; then
   VM_SIZE=`awk '/VmSize/ {print $2}' < /proc/$PROCESS_PID/status`
   VM_RSS=`awk '/VmRSS/ {print $2}' < /proc/$PROCESS_PID/status`
   echo "$ELAPSED_TIME,$VM_SIZE,$VM_RSS" >> $LOG_FILE
   sleep $PERIOD
   ELAPSED_TIME=`expr $ELAPSED_TIME + $PERIOD`
 else
   exit 0
 fi
done