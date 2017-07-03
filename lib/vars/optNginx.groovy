def deploy(config){
  echo "=== deploy: ${config.IMG_NAME} ==="
  node ("manager-132") {
    def CONF = config.ENV_CONF
    //def srvName = CONF.services[config.IMG_NAME].name
    sh "docker service update --image reg.ci.snsshop.net/opt/${config.VERSION} ${CONF.stackName[config.BUILD_ENV]}_${config.IMG_NAME}"
  }
}

def build(config){
  node (config.NODE) {
      def WORKDIR = "${WORKSPACE}/${config.IMG_NAME}"
      def CODE_DIR = "${WORKDIR}/_code_"
      echo "Work Directory: ${WORKDIR}"
      def CONF = config.ENV_CONF
      echo "ENV CONF: ${CONF}"

      stage ('构建镜像Nginx') {
        echo "=== Build: ${config.IMG_NAME} ==="
        //sh "ls ${WORKDIR}"
        dir(WORKDIR) {
            // some block
            sh 'ls'
            sh 'chmod +x scripts/*'
            echo "Build Env: ${config.BUILD_ENV}"
            if(config.BUILD_ENV=='test'||config.BUILD_ENV=='gray'){
              sh "sed -i 's/conf/conf\\/test/g' Dockerfile"
            }else{
              sh "sed -i 's/conf/conf\\/${config.BUILD_ENV}/g' Dockerfile"
            }

            //sh "sed -i 's/DOMAIN=\"ci\"/DOMAIN=\"${config.BUILD_ENV}\"/g' scripts/deploy.sh"
            //sh """sed -i 's/^IMG_NAME=\".*\"\$/IMG_NAME=\"${config.IMG_NAME}\"/g' Dockerfile"""
            sh "docker build -t ${CONF.registryServer}/opt/${config.VERSION} ."
            sh "docker push ${CONF.registryServer}/opt/${config.VERSION}"
        }
      echo "=== Finished: ${config.IMG_NAME} ==="
    }
  }

}


def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    //def buildNode = config.node
    println "Build Node: ${config.NODE}"
    ut = config.UT
    if(config.ACTION.contains("build")){
      build(config)
    }
    if(config.ACTION=="deploy_only"){
      deploy(config)
    }
    if(config.ACTION=="update_only"){
      build(config)
      deploy(config)
    }

}
