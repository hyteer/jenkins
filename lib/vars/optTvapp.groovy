def deploy(config){
  echo "=== deploy: ${config.IMG_NAME} ==="
  node ("manager-132") {
    def CONF = config.ENV_CONF
    def srvName = CONF.boss[config.IMG_NAME].name
    sh "docker service update --image reg.ci.snsshop.net/opt/${config.VERSION} Fix_${srvName}"
  }
}

def build(config){
  node (config.NODE) {
      def WORKDIR = "${WORKSPACE}/boss/${config.IMG_NAME}"
      def CODE_DIR = "${WORKDIR}/_code_"
      def UTILS_DIR = "${WORKSPACE}/utils"
      echo "Current Dir: ${PWD}"
      echo "Work Directory: ${WORKDIR}"
      def CONF = config.ENV_CONF
      echo "ENV CONF: ${CONF}"
      def BASE_DIR = CONF['WorkDir']
      echo "Build env: ${CONF.env}"
      def REPOS = CONF.boss[config.IMG_NAME].name
      echo "Repos: ${REPOS}"

      sh "chmod +x ${UTILS_DIR}/*"

      stage ('拉取代码') {
        echo "===拉取代码: ${config.IMG_NAME}==="
        //sh 'date && ls ${PWD}'
        ut.sh "ls ${WORKDIR}"
        echo "ServiceName: ${config.IMG_NAME}"
        echo "ServiceDir: ${config.IMG_NAME}"
        echo "BuildEnv: ${config.BUILD_ENV}"
        dir (WORKDIR){
            sh "mkdir -p _code_"
          }
        dir (WORKDIR) {
            echo "===Checkout service ${config.IMG_NAME}==="
            def GitUrl = "${CONF.gitBossBaseUrl}/${REPOS}.git"
            echo "GitUrl: ${GitUrl}"
            echo "JOB ENV: ${CONF.env}"
            def credentialsId = CONF[CONF.env].credentialsId
            echo "credentialsId: ${credentialsId}"
            checkout([$class: 'GitSCM', branches: [[name: config.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "_code_"], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: credentialsId, url: GitUrl]]])
            sh "mkdir _code_/runtime"
            sh "pwd && ls"
          }
        }

      stage ('服务配置') {

        dir ("${CODE_DIR}/config") {
          sh """echo '替换环境配置文件...' &&
          mv test test_org &&
          cp ../../config/env.php ./ &&
          mv ../../config/${config.BUILD_ENV} test"""
        }
      }
      stage ('构建镜像') {
        dir (WORKDIR) {
          echo "构建镜像..."
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

    return "true"

}
