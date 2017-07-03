
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    //def buildNode = config.node
    println "Build Node: ${config.NODE}"
    ut = config.UT


    // now build, based on the configuration provided
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
}
