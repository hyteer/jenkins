#! /bin/sh
basepath=$(cd `dirname $0`; pwd)
cd $basepath
nohup ./go_gateway -D -config_file $1 >nohup.out 2>&1 &
