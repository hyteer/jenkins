def deploy(config){
  echo "=== deploy: ${config.IMG_NAME} ==="
  node ("manager-132") {
    currentBuild.displayName = "#${BUILD_NUMBER} 服务:${config.IMG_NAME}"
    currentBuild.description = "标签:${config.IMG_TAG} 环境:${config.BUILD_ENV} 备注:${config.REMARK}"
    def CONF = config.ENV_CONF
    def srvName = CONF.services[config.IMG_NAME].name
    sh "docker service update --image reg.ci.snsshop.net/opt/${config.VERSION} ${CONF.stackName[config.BUILD_ENV]}_${srvName}"
  }
}

def build(config){
  node (config.NODE) {
      def CONF = config.ENV_CONF
      echo "ENV CONF: ${CONF}"
      if(config.ACTION.contains("build")){
        currentBuild.displayName = "#${BUILD_NUMBER} 服务:${config.IMG_NAME} ${config.IMG_TAG}"
        currentBuild.description = "执行人:${CONF.userName} 环境:${config.BUILD_ENV} 备注:${config.REMARK}"
      }
      def WORKDIR = "${WORKSPACE}/services/${config.IMG_NAME}"
      def CODE_DIR = "${WORKDIR}/_code_"
      def UTILS_DIR = "${WORKSPACE}/utils"
      echo "Current Dir: ${PWD}"
      echo "Work Directory: ${WORKDIR}"
      echo "Jenkins Env: ${JENKINS_ENV}"
      echo "Build Env: ${config.BUILD_ENV}"

      def STACK_NAME = CONF.stackName[config.BUILD_ENV]
      echo "Stack Name: ${STACK_NAME}"

      sh "chmod +x ${UTILS_DIR}/*"

      stage ('拉取代码') {
        echo "===拉取代码: ${config.IMG_NAME}==="
        //sh 'date && ls ${PWD}'
        sh "ls ${WORKDIR}"
        echo "ServiceName: ${config.IMG_NAME}"
        echo "ServiceDir: ${config.IMG_NAME}"
        echo "BuildEnv: ${config.BUILD_ENV}"
        dir (WORKDIR){
            sh "mkdir -p _code_"
          }
        dir (WORKDIR) {
            echo "===Checkout service ${config.IMG_NAME}==="
            def GitUrl = "${CONF.gitBaseUrl}/${config.IMG_NAME}.git"
            echo "GitUrl: ${GitUrl}"

            def credentialsId = CONF[JENKINS_ENV].credentialsId
            echo "credentialsId: ${credentialsId}"
            checkout([$class: 'GitSCM', branches: [[name: config.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "_code_"], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: credentialsId, url: GitUrl]]])
            sh "pwd && ls"
          }
        }

      stage ('服务配置') {
        dir (WORKDIR) {
          sh "echo '生成thirft...' && \
          chmod +x scripts/* && \
          cp scripts/gen-thirft.sh _code_ && \
          cd _code_ && \
          echo 'gen thirft files...' && \
          ./gen-thirft.sh"
        }
        dir (CODE_DIR) {
          if(config.BUILD_ENV!='test'&&config.BUILD_ENV!='gray'){
            sh "echo '替换环境配置文件...' && \
            rm -rf environments/test/* && \
            cp -rf ../config/${config.BUILD_ENV}/* environments/test/"
          }
          sh "sed -i 's/127.0.0.1:8500/consul:8500/g' config/global.php"
        }
      }
      stage ('构建镜像') {
        dir (WORKDIR) {
          echo "构建镜像...${config.IMG_NAME}"
          if(config.IMG_NAME.contains('service')){
            echo "use common Dockerfile..."
            sh "cp ../common/Dockerfile ./"
          }
          sh "docker build -t ${CONF.registryServer}/opt/${config.VERSION} ."
          sh "docker push ${CONF.registryServer}/opt/${config.VERSION}"
          echo "finished..."
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
    ut = config.UT
    if(config.ACTION.contains("build")||config.ACTION.contains("update")){
      build(config)
    }
    if(config.ACTION=="deploy_only"){
      deploy(config)
    }
    if(config.ACTION=="update_only"){
      deploy(config)
    }

    return "true"

}
