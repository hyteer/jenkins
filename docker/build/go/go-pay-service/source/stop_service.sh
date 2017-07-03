#! /bin/sh
pid=$(ps -ef|grep go_pay_service |grep -v grep |grep -v 8095 | awk '{print $2}')
kill -9 $pid
#basepath=$(cd `dirname $0`; pwd)
#cd $basepath
#nohup ./go_pay_service -D -config_file $1 >nohup.out 2>&1 &
