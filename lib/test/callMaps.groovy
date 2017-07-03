#!groovy
@Library('cilib')_
sayHello("silly...")
node{
    echo "debug..."
    def CONF_MAP = optConf()
    echo "Conf Map: ${CONF_MAP}"
    def SERVICE_MAP = CONF_MAP.serviceMap
    echo "Service Map: ${SERVICE_MAP}"
    echo "Total Services: ${SERVICE_MAP.size()}"
    for(srv in SERVICE_MAP){
        echo "Service: ${srv.key}, Type: ${srv.value.type}"
        if (srv.value.type=='php') {
            echo "Php Service: ${srv.value.name}"
        }else if (srv.value.type=='go') {
            echo "Go Repos: ${srv.value.repos}"
        }else{
            echo "Service: ${srv.key}"
        }
    }

    /*
    SERVICE_MAP.each { it ->
        echo "Key: ${it.key}"
    }

    def srvMap = [
        'go-gateway':'go-gateway-h5',
        'go-mch-gateway':'go-gateway-pc-merchant',
     ]
    echo "srvMap Size: ${srvMap.size()}"
    */

}
