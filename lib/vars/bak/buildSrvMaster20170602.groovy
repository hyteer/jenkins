
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    //def buildNode = config.node
    println "Build Node: ${config.NODE}"
    ut = config.UT
    servicesMap = config.SRV_MAP
    /*
    def servicesMap = [
        [name:'service-base', flag:'true'],
        [name:'service-third-party-interface', flag:'true'],
        [name:'service-third-party', flag:'true'],
        [name:'service-qrcode', flag:'true'],
        //[name:'service-pay', flag:'true'],
        //[name:'service-third-party-', dir: 'gate-go', flag:'false'],
       ]
       */

    for (int i = 0; i < servicesMap.size(); ++i) {
       println "Service: ${servicesMap[i]}"
    }



    // Call service job
    node (config.NODE) {
        def BASE_DIR = WORKSPACE
        echo "Call Workspace: ${BASE_DIR}"

        stage("构建服务镜像") {
            echo '===Scripts Pipeline...'
            //def services = ['pay', 'qrcode']
            for (int i = 0; i < servicesMap.size(); ++i) {
              echo "Pull SCM service ${servicesMap[i]}..."
              build job: 'srv-debug', parameters: [string(name: 'base_dir', value: BASE_DIR), string(name: 'service_name', value: servicesMap[i]['name']), string(name: 'branch', value: config.BRANCH),string(name: 'version', value: config.VERSION), string(name: 'run_mode', value: '1')]
            }

        }
        /*
        stage("测试环境部署") {
            echo "===Build docker images..."
        }
        stage("自动化测试") {
            echo "===Build docker images..."
        }
        */

    }

}
