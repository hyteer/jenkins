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
      def CODE_DIR = "${WORKDIR}/_code_"
      echo "Work Directory: ${WORKDIR}"
      def CONF = config.ENV_CONF
      echo "ENV CONF: ${CONF}"

      stage ('拉取Go程序') {
        echo "===拉取Go包: ${config.IMG_NAME}==="
        sh "ls ${WORKDIR}"
        sh "rm -rf ${WORKDIR}/_code_"
        echo "ServiceName: ${config.IMG_NAME}"
        echo "ServiceDir: ${config.IMG_NAME}"
        sh "echo ${CONF.resDomain}"
        //sh "echo ${config.RES_IP} ${config.RES_DOMAIN}>>/etc/hosts;"
        dir (WORKDIR){
            sh "mkdir -p _code_ && cd _code_ && rm -rf *"
          }
        dir (CODE_DIR) {
            sh "wget http://${CONF.resDomain}/go/${config.GO_PACKAGE}"
            sh "ls && mkdir -p go && tar -xf ${config.GO_PACKAGE} -C go"
            //sh "wget http://downloads.vikduo.com/go/go_gateway_gray.tar.xz"
            //sh "ls && mkdir -p go_gray && tar -xf go_gateway_gray.tar.xz -C go_gray"
            //sh "cp -rf go_gray/* go/"
            sh "ls ${WORKDIR}"
            sh "cp ../conf/gateway.${config.BUILD_ENV}.json go/gateway.json && ls go"
            //sh "rm go/go_gateway"
            //sh "wget http://downloads.vikduo.com/auto/go/go_gateway_debug_20170601"
            //sh "mv go_gateway_debug_20170601 go/go_gateway"

          }
        }

        stage ('构建Go镜像') {
          echo "===Build Image: ${config.IMG_NAME}==="
          dir(WORKDIR) {
              // some block
              sh 'ls ${PWD}'
              sh 'chmod +x scripts/*'
              sh "docker build -t ${CONF.registryServer}/opt/${config.VERSION} ."
              sh "docker push ${CONF.registryServer}/opt/${config.VERSION}"
          }
      }

  }
}


def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    ut = config.UT
    echo "CONF: ${config.ENV_CONF}"

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
