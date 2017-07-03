FROM reg.ci.snsshop.net/opt/ubuntu:20170523_105511
RUN mkdir -p /data/go-app
ADD source /data/go-app
WORKDIR /data/go-app
RUN chmod 755 go_gateway *.sh \
    && rm -rf *.log nohup.out
#    && sed -i 's/127.0.0.1:8500/consul:8500/g' gateway.json
EXPOSE 8080
CMD ["/data/go-app/go_gateway","-D","-config_file","gateway.json"]
