
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
        def UTILS_DIR
        echo "Current Dir: ${PWD}"
      	echo "Work Directory: ${WORKDIR}"
        if (config.BASE_DIR=="None") {
          echo "Base dir is None..."
          UTILS_DIR = "${WORKSPACE}/utils"
        }else{
          echo "Base Dir: ${config.BASE_DIR}"
          UTILS_DIR = "${WORKSPACE}/utils"
        }
        sh "chmod +x ${UTILS_DIR}/*"

        stage ('拉取代码') {
          echo "===拉取代码: ${config.IMG_NAME}==="
          //sh 'date && ls ${PWD}'
          ut.sh "ls ${WORKDIR}"
          echo "ServiceName: ${config.IMG_NAME}"
          echo "ServiceDir: ${config.IMG_NAME}"
          dir (WORKDIR){
              sh "mkdir -p _code_"
            }
      	  dir (WORKDIR) {
              echo "===Checkout service repo..."
              checkout([$class: 'GitSCM', branches: [[name: config.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "_code_"], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: config.CREDENTIALS_ID, url: config.GIT_URL]]])
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

        stage ('构建镜像') {
          dir (WORKDIR) {
            echo "构建镜像..."
            sh "docker build -t ${config.REG_SERVER}/opt/${config.IMG_NAME}:${config.VERSION} ."
            sh "docker push ${config.REG_SERVER}/opt/${config.IMG_NAME}:${config.VERSION}"
            echo "finished..."
          }
        }

        }


/*
          stage ('构建镜像') {
            echo "===Build Image: ${config.IMG_NAME}==="
            dir(WORKDIR) {
                // some block
                sh 'ls ${PWD}'
                sh 'chmod +x scripts/*'
                ut.sh "docker build -t ${config.REG_SERVER}/opt/${config.IMG_NAME}:${config.VERSION} ."
                ut.sh "docker push ${config.REG_SERVER}/opt/${config.IMG_NAME}:${config.VERSION}"
            }
        }
        */

    }
}
