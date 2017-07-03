// vars/buildPlugin.groovy
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    // now build, based on the configuration provided
    node {

        //checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: srvName]], submoduleCfg: [], userRemoteConfigs: [[url: gitUrl]]])
        //git url: "https://github.com/jenkinsci/${config.name}-plugin.git"
        def testName = config.repoName
        println "TestName: ${testName}"
        sh 'printenv'
        sh 'echo "Currdir:${PWD}"'
        sh 'echo "YT-srv:${srvName}"'
        echo "MyService: ${config.srvName}"
        echo "MyRepo: ${config.repoName}"
        //sh "ls"
        //git url: "https://github.com/hyteer/${config.repoName}.git"
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: config.srvName]], submoduleCfg: [], userRemoteConfigs: [[url: config.GIT_URL]]])

        //mail to: "...", subject: "${config.name} plugin build", body: "..."
    }
}
