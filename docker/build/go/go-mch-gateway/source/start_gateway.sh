#! /bin/sh
basepath=$(cd `dirname $0`; pwd)
cd $basepath
nohup ./go_mch_gateway -D -p 9090 -config_file $1 >nohup.out 2>&1 &
