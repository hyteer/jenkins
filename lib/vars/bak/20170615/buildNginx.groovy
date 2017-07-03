
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
    node (config.NODE) {
      	def WORKDIR = "${WORKSPACE}/${config.IMG_NAME}"
        def CODE_DIR = "${WORKDIR}/_code_"
      	echo "Work Directory: ${WORKDIR}"
        def CONF = config.ENV_CONF
        echo "ENV CONF: ${CONF}"

        stage ('构建镜像Nginx') {
          echo "=== Build: ${config.IMG_NAME} ==="
          //ut.sh "ls ${WORKDIR}"
          dir(WORKDIR) {
              // some block
              sh 'ls'
              sh 'chmod +x scripts/*'
              echo "Build Env: ${config.BUILD_ENV}"
              sh "sed -i 's/conf/conf\\/${config.BUILD_ENV}/g' Dockerfile"
              sh "sed -i 's/DOMAIN=\"ci\"/DOMAIN=\"${config.BUILD_ENV}\"/g' scripts/deploy.sh"
              //ut.sh """sed -i 's/^IMG_NAME=\".*\"\$/IMG_NAME=\"${config.IMG_NAME}\"/g' Dockerfile"""
              ut.sh "docker build -t ${CONF.registryServer}/opt/${config.VERSION} ."
              ut.sh "docker push ${CONF.registryServer}/opt/${config.VERSION}"
          }
        echo "=== Finished: ${config.IMG_NAME} ==="
      }
    }
}
