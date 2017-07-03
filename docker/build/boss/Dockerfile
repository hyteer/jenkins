FROM reg.ci.snsshop.net/opt/boss-alpine:v1
ENV WEB_PATH="/data/www"
ADD source ${WEB_PATH}
WORKDIR ${WEB_PATH}
RUN pwd && ls && \
    chown www-data:www-data runtime
CMD /var/www/html/run.sh

