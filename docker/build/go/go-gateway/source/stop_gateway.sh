#! /bin/sh
#killall -9 go_gateway
pid=$(ps -ef|grep go_gateway |grep -v grep |grep -v 8082 | awk '{print $2}')
if [ $pid ]; then
  kill -9 $pid
else
  echo "the gateway is already stop!!!"
fi
if [ "-$1" == "1" ]; then
rm -f nohup.out *.log
fi
