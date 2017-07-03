
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    //def buildNode = config.node
    println "Build Node: ${config.NODE}"
    ut = config.UT

    def servicesMap = [
        [name:'service-third-party', dir: 'third-party', flag:'true'],
        [name:'gate-go', dir: 'gate-go', flag:'false'],
       ]

    for (int i = 0; i < servicesMap.size(); ++i) {
       println "Service: ${servicesMap[i]}"
    }

    // Workdir Preparation
    node (config.NODE) {
        stage ('环境准备') {
          echo "===环境准备==="
          sh 'date && ls ${PWD}'
          ut.sh "rm -rf *"
          //checkout([$class: 'GitSCM', branches: [[name: '*/dev']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '9984ed7d-57af-45b9-b473-2e6eeafdfa7d', url: config.GIT_OPS_URL]]])
          checkout([$class: 'GitSCM', branches: [[name: '*/dev']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '9984ed7d-57af-45b9-b473-2e6eeafdfa7d', url: 'http://10.100.100.54/sctek/workflow.git']]])
          echo "Finished..."
        }
    }

    // Call service job
    node (config.NODE) {
        def BASE_DIR = WORKSPACE

        stage("服务镜像构建") {
            echo '===Scripts Pipeline...'
            def services = ['pay', 'qrcode']
            for (int i = 0; i < servicesMap.size(); ++i) {
              echo "Pull SCM service ${servicesMap[i]}..."
              build job: 'srv-debug', parameters: [string(name: 'base_dir', value: BASE_DIR), string(name: 'service_name', value: servicesMap[i]['name']), string(name: 'run_mode', value: '1')]
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
