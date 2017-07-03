
def deploy(config){
  echo "=== deploy: ${config.IMG_NAME} ==="
  node ("manager-132") {
    def CONF = config.ENV_CONF
    //def srvName = CONF.services[config.IMG_NAME].name
    def STACK_NAME = CONF.stackName[config.BUILD_ENV]
    sh "docker service update --image reg.ci.snsshop.net/opt/${config.VERSION} ${STACK_NAME}_${config.IMG_NAME}"
  }
}

def build(config){
    node (config.NODE) {
      def WORKDIR = "${WORKSPACE}/${config.IMG_NAME}"
      def CONF = config.ENV_CONF

      stage ('构建镜像Consul') {
        echo "=== Build: ${config.IMG_NAME} ==="
        //ut.sh "ls ${WORKDIR}"
        dir(WORKDIR) {
            // some block
            sh 'ls'
            //sh 'chmod +x scripts/*'
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


    /*
    node (config.NODE) {
      	def WORKDIR = "${WORKSPACE}/${config.IMG_NAME}"
        def CODE_DIR = "${WORKDIR}/_code_"
      	echo "Work Directory: ${WORKDIR}"
        def CONF = config.ENV_CONF
        echo "ENV CONF: ${CONF}"
        echo "Action: ${config.ACTION}"

        if(config.ACTION=="deploy_only"){
          deploy(config)
        }else if(config.ACTION=="build_only"){
          build(config,WORKDIR)
        }else if(config.ACTION=="update"){
          build(config,WORKDIR)
          deploy(config)
        }
    }
    */

}
