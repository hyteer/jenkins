#!groovy
@Library('cilib')_

node {
    def USER_NAME = getUser()
    currentBuild.displayName = "#${BUILD_NUMBER} 标签:${tag} ${USER_NAME} ${ACTION}"
    currentBuild.description = "服务:${SERVICES} 备注:${REMARK}"
    echo "Action:${ACTION}, Tag: ${TAG}, Remark: ${REMARK}"
    echo "Service: ${SERVICES}"

    def srvMap = SERVICES
    def servicesMap = SERVICES.split(",")
    echo "Services: ${servicesMap}"


    for(int i=0; i<servicesMap.size(); ++i){
       echo "Service: ${servicesMap[i]}"
       if (servicesMap[i]=='go-gateway') {
           echo "deploy go-gateway..."
       }else if (servicesMap[i]=='go-mch-gateway') {
           echo "deploy go-mch-gateway..."
       }else{
         echo "==>调用子任务 update ..."
         build job: 'update', parameters: [string(name: 'service', value: servicesMap[i]), string(name: 'action', value: ACTION), string(name: 'tag', value: TAG), text(name: 'remark', value: REMARK)]
       }
    }


}
