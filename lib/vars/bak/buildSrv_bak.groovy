
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    //def buildNode = config.node
    //def GIT_OPS_URL = config.gitUrl
    //def GIT_SRV_URL =  "https://github.com/hyteer/${config.repoName}.git"
    println "Build Node: ${config.NODE}"
    ut = config.UT


    // now build, based on the configuration provided
    node (config.NODE) {

        echo "CONFIG BASE_DIR: ${config.BASE_DIR}"
        echo "WORKSPACE: ${WORKSPACE}"
        def BASE_DIR
        def UTILS_DIR

        if (config.BASE_DIR == "None") {
          BASE_DIR = WORKSPACE
          echo "NewBaseDir: ${BASE_DIR}"
          UTILS_DIR = "${WORKSPACE}/utils"
          echo "=== Start: 环境准备 ===>"
          sh 'date && ls'
          ut.sh "rm -rf *"
          //checkout([$class: 'GitSCM', branches: [[name: '*/dev']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '9984ed7d-57af-45b9-b473-2e6eeafdfa7d', url: config.GIT_OPS_URL]]])
          checkout([$class: 'GitSCM', branches: [[name: '*/dev']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '9984ed7d-57af-45b9-b473-2e6eeafdfa7d', url: 'http://10.100.100.54/sctek/workflow.git']]])
          sh 'ls'
          ut.sh "chmod +x ${UTILS_DIR}/*"
          echo "=== Finished: 环境准备 ==="
        } else {
          BASE_DIR = config.BASE_DIR
          UTILS_DIR = "${BASE_DIR}/utils"
          echo "YT: BaseDir: ${BASE_DIR}"
        }
        echo "Out BaseDir: ${BASE_DIR}"
        def WORKDIR = "${BASE_DIR}/services/generic"
        def CODE_DIR = "${WORKDIR}/_code_"
        echo "Base dir: ${BASE_DIR}"
        echo "Work Directory: ${WORKDIR}"
        echo "Code Dir: ${CODE_DIR}"

        stage ('拉取服务代码') {
          //sh 'rm -rf ${service_name}'
          echo "===Pull SCM: ${config.SRV_NAME}==="

          dir (WORKDIR) {
            ut.sh "ls ${WORKDIR}"
            ut.sh "rm -rf ${WORKDIR}/_code_"
            //ut.echo 'just a test...'
            ut.sh "echo ${config.SRV_NAME}"
            sh 'echo "YT service_name: ${service_name}"'
            sh 'printenv'
            //echo "==Checkout Ops repo..."
            //checkout([$class: 'GitSCM', branches: [[name: '*/dev']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '9984ed7d-57af-45b9-b473-2e6eeafdfa7d', url: config.GIT_OPS_URL]]])
            //Home checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: config.GIT_OPS_URL]]])
            //sh 'printenv'
            //sh 'echo "Currdir:${PWD}"'
            echo "Service Name: ${config.SRV_NAME}"
            //git url: "https://github.com/hyteer/${config.repoName}.git"
            echo "===Checkout service repo..."
            checkout([$class: 'GitSCM', branches: [[name: config.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${CODE_DIR}"], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: config.CREDENTIALS_ID, url: config.GIT_SRV_URL]]])
            ut.sh "ls ${WORKDIR}"
          }

          //checkout([$class: 'GitSCM', branches: [[name: config.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: config.SRC_DIR], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: config.CREDENTIALS_ID, url: config.GIT_SRV_URL]]])
          //mail to: "...", subject: "${config.name} plugin build", body: "..."
        }
        stage ('镜像构建') {
          echo "=== Start: 构建${config.SRV_NAME}服务镜像 ==="
          dir(WORKDIR) {
              // some block
              sh 'ls ${PWD}'
              ut.sh "cp ${UTILS_DIR}/thrift-gen.sh ${CODE_DIR}/"
              ut.sh """sed -i 's/^SRV_NAME=\".*\"\$/SRV_NAME=\"${config.SRV_NAME}\"/g' scripts/deploy.sh"""
              ut.sh """sed -i 's/^SRV_NAME=\".*\"\$/SRV_NAME=\"${config.SRV_NAME}\"/g' scripts/start.sh"""
              // 修改config/global.php consul配置
              ut.sh """cd ${CODE_DIR} && sed -i 's/127.0.0.1:8500/consul:8500/g' config/global.php"""
              // Gen thrift
              echo "Gen thrift..."
              ut.sh "cd ${CODE_DIR} && /bin/bash thrift-gen.sh"
              //ut.sh """sed -i 's/service-name/${config.SRV_NAME}/g' scripts/start.sh"""
              sh 'chmod +x scripts/*'
              ut.sh "docker build -t opt/${config.SRV_NAME}:${config.VERSION} ."
          }
          echo "=== Finished: 镜像构建 ==="
        }
    }
}
