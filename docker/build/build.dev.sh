#!/bin/sh
SERVICE='service-name'
ACTION='update'
DEFAULT_TAG='debug'
DEFAULT_REMARK='remote build'
USER='tonyhu'
TOKEN='59a5cb369b04a01a65f1612fb57b923b'
REGISTRY='reg.ci.snsshop.net'
JENKINS_JOB_URL='http://jenkins.ci.snsshop.net/job/Dev/job/update'
JOB_TOKEN='AKqKaliHjaiwq82lh3'
#示例: ./build.sh v001 增加XX功能

if [ ! -n "$1" ] ;then
TAG=$DEFAULT_TAG
echo "use default tag:${TAG}..."
else
TAG=$1
echo "tag: ${TAG}"
fi

if [ ! -n "$2" ] ;then
REMARK=$DEFAULT_REMARK
echo "use default remark:${REMARK}..."
else
REMARK=$2
echo "remark: ${REMARK}"
fi


build () {
  echo "==>build镜像..."
  docker build -t ${REGISTRY}/opt/dev/${SERVICE}:${TAG} .
  sleep 2
}

push () {
  echo "==>Push镜像..."
  docker push ${REGISTRY}/opt/dev/${SERVICE}:${TAG}
  sleep 2
}

trigger_jenkins () {
  echo "==>触发Jenkins任务..."
  curl -X POST ${JENKINS_JOB_URL}/build \
  --user ${USER}:${TOKEN} -d token=${JOB_TOKEN} \
  --data-urlencode json='{"parameter": [{"name":"service", "value":"'"${SERVICE}"'"},{"name":"action", "value":"'"${ACTION}"'"},{"name":"tag", "value":"'"${TAG}"'"},{"name":"remark", "value":"'"${REMARK}"'"}]}'
  echo "成功触发Jenkins任务..."
}

get_result () {
  sleep 8
  curl -X POST ${JENKINS_JOB}/27/api/json?pretty=true\&tree=result --user ${USER}:${TOKEN}
}

build
push
trigger_jenkins
#get_result
