// vars/buildPlugin.groovy
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    def GIT_OPS_URL = config.gitUrl
    def GIT_SRV_URL =  "https://github.com/hyteer/${config.repoName}.git"
    println "Config: ${config}"
    def goMap = [
        'go-gateway':'go_gateway',
        'go-mch-gateway':'go_mch_gateway',
        'go-pay-service':'go_pay_service'
    ]
    def BASE_PATH = config.BASE_PATH
    def goAppName = goMap[config.GO_PACKAGE]
    def GO_PATH = "${BASE_PATH}/${goAppName}"
    def SRC_PATH = "${BASE_PATH}/${goAppName}/src/${goAppName}"
    def BIN_PATH = "${BASE_PATH}/${goAppName}/bin"
    def respMap


    node ('node-147') {
      //stage ('编译打包'){
        echo "Build No: ${BUILD_NUMBER}"
        echo "Go Package: ${config.GO_PACKAGE}"
        def CONF = optConf()
        def SRV_MAP = CONF.serviceMap
        //def BASE_PATH = config.GO_BASE_PATH
        echo "Service Map: ${SRV_MAP}"
        def GO_GIT_URLS=['go-gateway':'https://git.snsshop.net/OptPrime/go-gateway-h5.git','go-mch-gateway':'https://git.snsshop.net/OptPrime/go-gateway-pc-merchant.git']
        def GIT_URL = "${CONF.common.gitBaseUrl}/${SRV_MAP[config.GO_PACKAGE].repos}.git"
        echo "Git Url: ${GIT_URL}"
        //def GO_PATH = config.GO_PATH
        //def SRC_PATH = config.SRC_PATH
        //def BIN_PATH = config.BIN_PATH

        checkout([$class: 'GitSCM', branches: [[name: '*/develop']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: SRC_PATH]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '03f9be09-c25f-44cf-8ecc-0b78de5a7044', url:GIT_URL]]])
        withEnv(['PATH+goroot=/usr/local/go/bin',"PATH+gobin=${BIN_PATH}","GOPATH=${GO_PATH}"]) {
            //sh 'export'
            sh "echo ${PATH}"
            sh "go env GOPATH"
            dir ("${SRC_PATH}"){
             sh "go install"
            }
          }
      //}
      //stage('Artifact') {
        sh "mkdir -p package"
        sh "cp ${BIN_PATH}/${goAppName} ./package/"
        //archiveArtifacts goAppName
        stash name: "go-packages", includes: "package/*"
      //}
      respMap = ['BUILD_NUMBER':BUILD_NUMBER,'JOB_NAME':JOB_NAME]
    }
/*
    node('node-148') {
      dir("first-stash") {
        unstash "first-stash"
      }
    }
*/
    return respMap
}
