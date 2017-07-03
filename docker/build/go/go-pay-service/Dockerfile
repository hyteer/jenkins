FROM reg.ci.snsshop.net/opt/ubuntu:20170523_105511
RUN mkdir -p /data/go-app
ADD source /data/go-app
WORKDIR /data/go-app
RUN chmod 755 go_pay_service *.sh \
    && rm -rf *.log nohup.out
EXPOSE 9095
CMD ["/data/go-app/go_pay_service","-D","-config_file","service.json"]
