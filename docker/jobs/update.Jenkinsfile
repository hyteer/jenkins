#!groovy
@Library('cilib')_
def STACK_NAME = "Test"
def SERVICE_MAP = [
            'consul':[name:'consul'],
            'filebeat':[name:'filebeat'],
            'nginx':[name:'nginx'],
            //go
            'go-gateway':[name:'go-gateway'],
            'go-mch-gateway':[name:'go-mch-gateway'],
            'go-pay-service':[name:'Pay'],
            // app services
            'service-base':[name:'Base',repos:'service-base'],
            'service-qrcode':[name:'Qrcode'],
            'service-third-party':[name:'ThirdParty'],
            'service-third-party-interface':[name:'ThirdPartyInterface'],
            //'service-pay':[name:'Pay'],
            'service-order':[name:'Order'],
            'service-shop':[name:'Shop'],
            'service-product':[name:'Product'],
            'service-message':[name:'Message'],
            'service-user':[name:'Users'],
            'service-merchant':[name:'Merchant'],
            'service-shopping-cart':[name:'ShoppingCart'],
            'gateway-app-merchant':[name:'gateway-app-merchant'],
            'gateway-app-salesman':[name:'gateway-app-salesman'],
            'gateway-boss':[name:'gateway-boss']
          ]

node ('manager-132') {
   def USER_NAME = getUser()
   def SRV_NAME = SERVICE_MAP[service].name
   echo "${action} service: ${service} ..."
   echo "Service: ${service}, Tag: ${tag}"
   currentBuild.displayName = "#${BUILD_NUMBER} ${service}:${tag}"
   currentBuild.description = "执行:${USER_NAME} 操作:${action} 备注:${remark}"
   sh "docker service update --image reg.ci.snsshop.net/opt/test/${service}:${tag} ${STACK_NAME}_${SRV_NAME}"
}
