def deploy(config){
  echo "=== deploy: ${config.IMG_NAME} ==="
  node ("manager-132") {
    def CONF = config.ENV_CONF
    def srvName = CONF.services[config.IMG_NAME].name
    //def srvName = CONF.services[config.IMG_NAME].name
    sh "docker service update --image reg.ci.snsshop.net/opt/${config.VERSION} Test_${srvName}"
  }
}

def build(config){
  node (config.NODE) {
      def WORKDIR = "${WORKSPACE}/${config.IMG_NAME}"
      def CODE_DIR = "${WORKDIR}/_code_"
      echo "Work Directory: ${WORKDIR}"
      def CONF = config.ENV_CONF
      echo "ENV CONF: ${CONF}"

      stage ("拉取${config.IMG_NAME}程序") {
        echo "===拉取Go包: ${config.IMG_NAME}==="
        //dir("${WORKSPACE}/${config.SRV_DIR}") {
        //sh 'date && ls ${PWD}'
        sh "ls ${WORKDIR}"
        sh "rm -rf ${WORKDIR}/_code_"
        echo "ServiceName: ${config.IMG_NAME}"
        echo "ServiceDir: ${config.IMG_NAME}"
        sh "echo ${CONF.resDomain}"
        def STACK_NAME = CONF.stackName[config.BUILD_ENV]
        echo "Stack Name: ${STACK_NAME}"

        //ut.sh "echo ${config.RES_IP} ${config.RES_DOMAIN}>>/etc/hosts;"
        dir (WORKDIR) {
            sh "mkdir -p ${CODE_DIR}"
            sh "cp -rf app/* ${CODE_DIR}"
            sh "cp conf/service.${STACK_NAME}.json ${CODE_DIR}/service.json"
            sh "wget http://downloads.vikduo.com/auto/go/go_pay/go_pay_service"
            sh "mv go_pay_service ${CODE_DIR}/"

          }
        }

        stage ('构建GoMch镜像') {
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
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    //def buildNode = config.node
    println "Build Node: ${config.NODE}"
    println "Action: ${config.ACTION}"
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
    if(config.ACTION=="deploy_only"){
      deploy(config)
    }else if(config.ACTION=="build_only"){
      build(config)
    }else if(config.ACTION=="update"){
      build(config)
      deploy(config)
    }
    */

}
