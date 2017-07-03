#! /bin/sh
#killall -9 go_mch_gateway
pid=$(ps -ef|grep go_mch_gateway |grep -v grep |grep 8081 | awk '{print $2}')
if [ $pid ]; then
  kill -9 $pid
else
  echo "the gateway is already stop!!!"
fi
if [ "-$1" == "1" ]; then
rm -f nohup.out *.log
fi
