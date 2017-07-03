// vars/buildPlugin.groovy
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    def buildNode = config.node
    def GIT_OPS_URL = config.gitUrl
    def GIT_SRV_URL =  "https://github.com/hyteer/${config.repoName}.git"
    println "Build Node: ${buildNode}"


    // now build, based on the configuration provided
    node ('node-5') {

        //checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: srvName]], submoduleCfg: [], userRemoteConfigs: [[url: gitUrl]]])
        //git url: "https://github.com/jenkinsci/${config.name}-plugin.git"
        stage ('Preparation') {
          sh 'rm -rf *'
          echo "==Checkout Ops repo..."
          checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: GIT_OPS_URL]]])
        }

        def testName = config.repoName
        println "TestName: ${testName}"
        sh 'printenv'
        sh 'echo "Currdir:${PWD}"'
        sh 'echo "YT-srv:${srvName}"'
        echo "MyService: ${config.srvName}"
        echo "MyRepo: ${config.repoName}"
        //sh "ls"
        //git url: "https://github.com/hyteer/${config.repoName}.git"
        echo "===Checkout service repo..."
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${srvName}"]], submoduleCfg: [], userRemoteConfigs: [[url: GIT_SRV_URL]]])
        //checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: config.srvName]], submoduleCfg: [], userRemoteConfigs: [[url: GIT_SRV_URL]]])

        //mail to: "...", subject: "${config.name} plugin build", body: "..."
    }
}
