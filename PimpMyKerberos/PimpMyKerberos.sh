#!/bin/bash


fct_start()
{
 MAX=10
 echo "Logs will be dumped into ./logs under $(pwd)"
 mkdir logs 2>/dev/null
 nohup java -jar build/libs/PimpMyKerberos.jar ./configFile/aCore.xml > ./logs/pimpMyKerberos.log 2>&1 &
 sleep 1
 timeout 1 echo 1 >/dev/null 2>&1
 if [ "$?" = 0 ]
  then
   timeout 3s tail -1000f ./logs/pimpMyKerberos.log
  else
   i=0
   while [ "$i" -lt $MAX ]
    do
     echo "${i}/${MAX}"
     sleep 1
     i=$(expr $i + 1)
    done
    tail -200 ./logs/pimpMyKerberos.log
  fi
  echo " "
  echo " "
  grep "Please open this url in you favorite" ./logs/pimpMyKerberos.log 2>/dev/null
  echo " "
  echo " You can tail log by typing tail -100f ./logs/pimpMyKerberos.log"
  echo " "


}

fct_stop()
{
pkill -9 -f "PimpMyKerberos.jar" 2>/dev/null
echo "PimpMyKerberos stop"
}

case $1 in 
 "start")
  fct_start
 ;;
 "stop")
  fct_stop
 
 ;;
 "restart")
  fct_stop
  fct_start

 ;;
esac
