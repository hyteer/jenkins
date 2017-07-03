#!/bin/sh
SERVICE='service-third-party'
ACTION='update'
DEFAULT_TAG='debug'
REMARK='remote test'
USER='devops'
PASSWORD='123456'
#示例: ./update.sh 001 just\ a\ test

if [ ! -n "$1" ] ;then
TAG=$DEFAULT_TAG
echo "use default tag:${TAG}..."
else
TAG=$1
echo "tag: ${TAG}"
fi


build () {
  echo "==>build镜像..."
  docker build -t reg.ci.snsshop.net/opt/dev/${SERVICE}:${TAG} .
  sleep 2
}

push () {
  echo "==>Push镜像..."
  docker push reg.ci.snsshop.net/opt/dev/${SERVICE}:${TAG}
  sleep 2
}

trigger_jenkins () {
  echo "==>触发Jenkins任务..."
  curl -X POST http://jenkins.ci.snsshop.net/job/Dev/job/opt-dev-update/build \
  --user tonyhu:1a1781c03b2345691e7ebcb59d012210 -d token=AKqKaliHjaiwq82lh3 \
  --data-urlencode json='{"parameter": [{"name":"service", "value":"'"${SERVICE}"'"},{"name":"action", "value":"'"${ACTION}"'"},{"name":"tag", "value":"'"${TAG}"'"},{"name":"remark", "value":"'"${REMARK}"'"}]}'
  echo "成功触发Jenkins任务..."
}

get_result () {
  sleep 8
  curl -X POST http://jenkins.ci.snsshop.net/job/Dev/job/opt-dev-update/27/api/json?pretty=true\&tree=result --user devops:123456
}

build
push
trigger_jenkins
#get_result
