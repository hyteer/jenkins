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


    // now build, based on the configuration provided
    node (config.NODE) {
        stage ('Build Images') {
          echo "Build Images"
          
        }
    }
}
