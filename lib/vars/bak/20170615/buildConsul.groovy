
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    //def buildNode = config.node
    println "Build Node: ${config.NODE}"
    ut = config.UT
    //def WORKDIR = "${pwd}/${config.IMG_NAME}"

    // now build, based on the configuration provided
    def deployOnly(config){
      echo "Action: ${config.ACTION}"
    }

    node (config.NODE) {
      	def WORKDIR = "${WORKSPACE}/${config.IMG_NAME}"
        def CODE_DIR = "${WORKDIR}/_code_"
      	echo "Work Directory: ${WORKDIR}"
        def CONF = config.ENV_CONF
        echo "ENV CONF: ${CONF}"
        if(config.ACTION=="deploy"){
          deployOnly(config)
        }

        stage ('构建镜像Consul') {
          echo "=== Build: ${config.IMG_NAME} ==="
          //ut.sh "ls ${WORKDIR}"
          dir(WORKDIR) {
              // some block
              sh 'ls'
              //sh 'chmod +x scripts/*'
              sh "docker build -t ${CONF.registryServer}/opt/${config.VERSION} ."
              echo 'push image...'
              sh "docker push ${CONF.registryServer}/opt/${config.VERSION}"
          }
        echo "=== Finished: ${config.IMG_NAME} ==="
      }
    }
}
