#!/bin/bash
#docker="/usr/bin/docker"
echo "生成thrift code..."
    for file in ./thriftFiles/*.thrift
    do
        if test -f ${file}
        then
                file2=`echo ${file}|cut -c 3-`
            docker run --rm -v "$PWD:/data" thrift:0.9.3 thrift -o /data --gen php:server /data/${file2}
        else
            echo "${file} is not a file..."
        fi
    done
cp -rf gen-php/thriftServices/* thriftServices/
ls thriftServices
