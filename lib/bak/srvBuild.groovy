// vars/buildPlugin.groovy
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
    ut = config.UI


    // now build, based on the configuration provided
    node (config.NODE) {
        stage ('Pull SCMs') {
          echo "===Pull SCM: ${config.SRV_NAME}==="
          //sh 'rm -rf *'
          ut.sh "rm -rf ${config.SRV_DIR}"
          //ut.echo 'Debug...'
          checkout([$class: 'GitSCM', branches: [[name: '*/dev']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '9984ed7d-57af-45b9-b473-2e6eeafdfa7d', url: config.GIT_OPS_URL]]])
          //Home checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: config.GIT_OPS_URL]]])
          //sh 'printenv'
          //sh 'echo "Currdir:${PWD}"'
          echo "Service Name: ${config.SRV_NAME}"
          echo "Repos: ${config.REPOS}"
          //git url: "https://github.com/hyteer/${config.repoName}.git"
          echo "===Checkout service repo..."
          checkout([$class: 'GitSCM', branches: [[name: config.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: config.SRC_DIR], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: config.CREDENTIALS_ID, url: config.GIT_SRV_URL]]])
          //mail to: "...", subject: "${config.name} plugin build", body: "..."
        }
        stage ('Build Images') {
          echo "===D: ${config.SRV_NAME}==="
          dir("${WORKSPACE}/${config.SRV_DIR}") {
              // some block
              sh 'ls ${PWD}'
              echo "In ServiceDir: ${config.SRV_DIR}"
          }
          echo "Build Images"
        }
    }
}
