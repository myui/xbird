#!/bin/sh
#
#GetMemUsage launch script

cmd="$1"

if [ -n $cmd ] ; then  
  $cmd &
  ppid=$!
#  ps -H --ppid $ppid
#  ps --ppid $ppid | awk 'NR>1{print $1}'
  pid=`ps --ppid $ppid | awk 'NR>1{print $1}'`
#  echo "pid=$pid"
  ./GetMemUsage.sh $pid &
  wait
fi

if ps x | perl -ne 'print if /\b'$cmd'\b/' > /dev/null 2>&1 ; then
 wait
else
 exit 0
fi
