
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
      	def WORKDIR = "${WORKSPACE}/services/${config.IMG_NAME}"
        def CODE_DIR = "${WORKDIR}/_code_"
        def UTILS_DIR = "${WORKSPACE}/utils"
        echo "Current Dir: ${PWD}"
      	echo "Work Directory: ${WORKDIR}"
        def CONF = config.ENV_CONF
        echo "ENV CONF: ${CONF}"
        def BASE_DIR = CONF['WorkDir']
        echo "Build env: ${CONF.env}"

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
              def GitUrl = "${CONF.gitBaseUrl}/${config.IMG_NAME}.git"
              echo "GitUrl: ${GitUrl}"
              echo "JOB ENV: ${CONF.env}"
              def credentialsId = CONF[CONF.env].credentialsId
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
            sh "echo '替换环境配置文件...' && \
            rm -rf environments/test/* && \
            cp -rf ../config/${config.BUILD_ENV}/* environments/test/ && \
            sed -i 's/127.0.0.1:8500/consul:8500/g' config/global.php"
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
    return "true"

}
