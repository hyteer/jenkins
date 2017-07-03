
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

        stage ('环境部署') {
          echo "===Build Image: ${config.SRV_NAME}==="
          dir("${WORKSPACE}/${config.SRV_DIR}") {
              // some block
              sh 'ls ${PWD}'
              sh 'chmod +x build.sh'
              sh './build.sh'
              echo "In ServiceDir: ${config.SRV_DIR}"
          }
          echo "Finished..."
        }
        stage ('XXXX') {
          echo "===Deploy: ${config.SRV_NAME}==="
        }
    }
}
