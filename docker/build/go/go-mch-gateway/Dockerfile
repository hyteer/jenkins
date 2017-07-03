FROM reg.ci.snsshop.net/opt/ubuntu:20170523_105511
RUN mkdir -p /data/go-app
ADD source /data/go-app
WORKDIR /data/go-app
RUN chmod 755 go_mch_gateway *.sh \
    && rm -rf *.log nohup.out
#    && sed -i 's/127.0.0.1:8500/consul:8500/g' gateway.json
EXPOSE 8081
CMD ./go_mch_gateway -p 8081 -config_file gateway.json -D true

#CMD ["/data/go-app/go_mch_gateway","-D","-config_file","gateway.json"]
