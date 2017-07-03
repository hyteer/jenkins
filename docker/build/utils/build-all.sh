#!/bin/sh
echo "batch build images..."
    for dirname in ./*
    do
        if test -d ${dirname}
        then
            srvname=`echo ${dirname}|cut -c 2-`
            docker build -t reg.ci.snsshop.net/opt/dev/${srvname}:001 ${dirname}
            docker push reg.ci.snsshop.net/opt/dev/${srvname}:001
        else
            echo "${dirname} is not a directory..."
        fi
    done
