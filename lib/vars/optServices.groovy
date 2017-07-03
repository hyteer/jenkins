def deploy(config){
  echo "=== deploy all services ==="
  node ("manager-132") {
    def CONF = config.ENV_CONF
    echo "ENV CONF: ${CONF}"
    def WORKDIR = CONF['WorkDir']
    dir(WORKDIR){
      sh "sed -i 's/_version/${config.IMG_TAG}/g' stack/${config.STACK_NAME}.services.yml"
      sh "docker stack deploy -c stack/fixopt.services.yml ${config.STACK_NAME}"
    }
  }
}

def build(config){
  node (config.NODE) {

      def CONF = config.ENV_CONF
      echo "ENV CONF: ${CONF}"
      def BASE_DIR = CONF['WorkDir']
      echo "Build env: ${CONF.env}"
      def servicesMap = config.SERVICES

      for (int i = 0; i < servicesMap.size(); ++i) {
        println "Service: ${servicesMap[i]}"
        def imgName = servicesMap[i]['name']
        def imgVersion = "${config.BUILD_ENV}/${imgName}:${tag}"
        def WORKDIR = "${WORKSPACE}/services/${imgName}"
        echo "Current Dir: ${PWD}"
        echo "Work Directory: ${WORKDIR}"
        def CODE_DIR = "${WORKDIR}/_code_"
        def UTILS_DIR = "${WORKSPACE}/utils"
        sh "chmod +x ${UTILS_DIR}/*"


        stage (imgName) {
          echo "===服务: ${imgName}==="
          //sh 'date && ls ${PWD}'
          ut.sh "ls ${WORKDIR}"
          echo "ServiceName: ${imgName}"
          echo "ServiceDir: ${imgName}"
          echo "BuildEnv: ${config.BUILD_ENV}"
          dir (WORKDIR){
              sh "mkdir -p _code_"
            }
          dir (WORKDIR) {
            echo "===Checkout service ${imgName}==="
            def GitUrl = "${CONF.gitBaseUrl}/${imgName}.git"
            echo "GitUrl: ${GitUrl}"
            echo "JOB ENV: ${CONF.env}"
            def credentialsId = CONF[CONF.env].credentialsId
            echo "credentialsId: ${credentialsId}"
            checkout([$class: 'GitSCM', branches: [[name: config.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "_code_"], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: credentialsId, url: GitUrl]]])
            sh "pwd && ls"
            }


          echo "服务配置..."
          dir (WORKDIR) {
            sh "echo '生成thirft...' && \
            chmod +x scripts/* && \
            cp scripts/gen-thirft.sh _code_ && \
            cd _code_ && \
            echo 'gen thirft files...' && \
            ./gen-thirft.sh"
          }
          dir (CODE_DIR) {
            sh "echo '替换环境配置文件...' && \
            rm -rf environments/test/* && \
            cp -rf ../config/${config.BUILD_ENV}/* environments/test/ && \
            sed -i 's/127.0.0.1:8500/consul:8500/g' config/global.php"
          }

          echo "构建镜像..."
          dir (WORKDIR) {
            echo "构建镜像..."
            sh "docker build -t ${CONF.registryServer}/opt/${imgVersion} ."
            sh "docker push ${CONF.registryServer}/opt/${imgVersion}"
            echo "finished..."
          }
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

    if(config.ACTION.contains("build")){
      build(config)
    }
    if(config.ACTION=="deploy_services"){
      deploy(config)
    }
    if(config.ACTION=="update_services"){
      build(config)
      deploy(config)
    }

    return "true"

}
