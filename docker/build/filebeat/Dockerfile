FROM alpine
COPY ./filebeat /data/filebeat/
RUN cd /data/filebeat && mkdir conf
COPY filebeat.yml /data/filebeat/conf/
CMD ["/data/filebeat/filebeat", "-e", "-c", "/data/filebeat/conf/filebeat.yml", ">", "/data/filebeat/filebeat.log"]
