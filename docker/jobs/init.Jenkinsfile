#!groovy
@Library('cilib')_
//def WORK_PATH = "/root/opt/test"
def GIT_BASE_URL = "https://git.snsshop.net/OptPrime"
def SERVICE_MAP = [
    frontend:[
      [name:'frontend-h5-rest-shop', flag:'true'],
      [name:'frontend-pc-merchant', flag:'true'],
    ],
    go:[
      [name:'go-gateway', repos:'go-gateway-h5', flag:'true'],
      [name:'go-mch-gateway', repos:'go-gateway-pc-merchant', flag:'true'],
      [name:'go-pay-service', flag:'true'],
    ],
    service:[
      [name:'service-base', flag:'true'],
      [name:'service-qrcode', flag:'true'],
      //[name:'service-third-party', flag:'true'],
      //[name:'service-third-party-interface', flag:'true'],
      /*
      [name:'service-shop', flag:'true'],
      [name:'service-order', flag:'true'],
      [name:'service-product', flag:'true'],
      [name:'service-message', flag:'true'],
      [name:'service-merchant', flag:'true'],
      */
      //[name:'service-pay', flag:'true'],
      //[name:'service-user', flag:'true'],
    ],
   gateway:[
     [name:'gateway-app-merchant', flag:'true'],
     //[name:'gateway-boss', flag:'true'],
     //[name:'gateway-app-salesman', flag:'true'],
    ]
  ]


def initModules = MODULES.split(",")
echo "Modules: ${initModules}"
def CONF = optConf()
def SRV_MAP = CONF.serviceMap



node (NODE) {
    def USER_NAME = getUser()
    currentBuild.displayName = "#${BUILD_NUMBER} ${INIT_ENV} ${NODE}"
    currentBuild.description = "工作目录:${WORK_PATH}"
    echo "Env: ${INIT_ENV}, WorkPath: ${WORK_PATH}"

        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '22329e74-d7e1-48da-a0a9-a71f18799491', url: 'http://10.100.100.54/sctek/opt-docker.git']]])
        sh "mkdir -p ${WORK_PATH}"
        sh "cp -rf build/utils ${WORK_PATH}"
        sh "chmod +x ${WORK_PATH}/utils/*.sh"

/*********************************** PHP ***********************************/

        if (initModules.contains('php')) {
          stage('初始化PHP') {
            echo "==>初始化PHP..."
            def PHP_SOURCE = "${WORKSPACE}/build/php"
            def PHP_SERVICE_PATH = "${WORK_PATH}/php/service"
            def PHP_GATEWAY_PATH = "${WORK_PATH}/php/gateway"

            echo '-->初始化PHP服务目录...'

            sh "mkdir -p ${PHP_SERVICE_PATH}"
            for(int i = 0; i < SERVICE_MAP.service.size(); ++i){
              sh "mkdir -p ${PHP_SERVICE_PATH}/${SERVICE_MAP.service[i].name}"
              dir ("${PHP_SERVICE_PATH}/${SERVICE_MAP.service[i].name}") {
                  sh "cp ${PHP_SOURCE}/build.${INIT_ENV}.sh ./build.sh"
                  sh "chmod +x build.sh"
                  sh "sed -i 's/service-name/${SERVICE_MAP.service[i].name}/g' build.sh"
                  sh "cp ${PHP_SOURCE}/service/Dockerfile.${INIT_ENV} ./Dockerfile"
                  //sh "sed -i 's/--env=Test/--env=${INIT_ENV}/g' Dockerfile"
              }
            }

            if (INIT_CODE=='true') {
              echo "-->初始化PHP服务代码..."
              dir ("${WORK_PATH}/php/service") {
                for(int i = 0; i < SERVICE_MAP.service.size(); ++i){
                  println "Service: ${SERVICE_MAP.service[i]}"
                  def GIT_URL = "${GIT_BASE_URL}/${SERVICE_MAP.service[i].name}.git"
                  echo "Git Url: ${GIT_URL}"
                  checkout([$class: 'GitSCM', branches: [[name: '*/develop']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false], [$class: 'RelativeTargetDirectory', relativeTargetDir: "${SERVICE_MAP.service[i].name}/source"]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '03f9be09-c25f-44cf-8ecc-0b78de5a7044', url: GIT_URL]]])
                  sh "cp ../../utils/gen-thirft.sh ${SERVICE_MAP.service[i].name}/source/"
                }
              }
            }

            // 初始化PHP网关
            echo '-->初始化PHP网关...'
            echo '-->初始化PHP网关目录...'
            sh "mkdir -p ${PHP_GATEWAY_PATH}"
            for(int i = 0; i < SERVICE_MAP.gateway.size(); ++i){
              sh "mkdir -p ${PHP_GATEWAY_PATH}/${SERVICE_MAP.gateway[i].name}"
              dir ("${PHP_GATEWAY_PATH}/${SERVICE_MAP.gateway[i].name}") {
                  sh "cp ${PHP_SOURCE}/build.${INIT_ENV}.sh ./build.sh"
                  sh "chmod +x build.sh"
                  sh "sed -i 's/service-name/${SERVICE_MAP.gateway[i].name}/g' build.sh"
                  sh "cp ${PHP_SOURCE}/gateway/Dockerfile.${INIT_ENV} ./Dockerfile"
                  //sh "sed -i 's/--env=Test/--env=${INIT_ENV}/g' Dockerfile"
              }
            }
            if (INIT_CODE=='true') {
              echo "-->初始化PHP网关代码..."
              dir (PHP_GATEWAY_PATH) {
                for(int i = 0; i < SERVICE_MAP.gateway.size(); ++i){
                  println "Gateway: ${SERVICE_MAP.gateway[i]}"
                  def GIT_URL = "${GIT_BASE_URL}/${SERVICE_MAP.gateway[i].name}.git"
                  echo "Git Url: ${GIT_URL}"
                  checkout([$class: 'GitSCM', branches: [[name: '*/develop']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false], [$class: 'RelativeTargetDirectory', relativeTargetDir: "${SERVICE_MAP.gateway[i].name}/source"]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '03f9be09-c25f-44cf-8ecc-0b78de5a7044', url: GIT_URL]]])
                  sh "cp ../../utils/gen-thirft.sh ${SERVICE_MAP.gateway[i].name}/source/"
                }
              }
            }

          }
        }

/*********************************** 前端 ***********************************/

        if (initModules.contains('frontend')) {
          stage('初始化前端') {
            echo "==>初始化前端..."
            echo "-->初始化前端目录..."
            def FRONTEND_SOURCE = "${WORKSPACE}/build/frontend"
            def FRONTEND_PATH = "${WORK_PATH}/frontend"
            sh "mkdir -p ${FRONTEND_PATH}"
            for(int i = 0; i < SERVICE_MAP.frontend.size(); ++i){
              sh "mkdir -p ${FRONTEND_PATH}/${SERVICE_MAP.frontend[i].name}"
              dir ("${FRONTEND_PATH}/${SERVICE_MAP.frontend[i].name}") {
                sh "cp ${FRONTEND_SOURCE}/build.${INIT_ENV}.sh ./build.sh"
                sh "chmod +x build.sh"
                sh "sed -i 's/service-name/${SERVICE_MAP.frontend[i].name}/g' build.sh"
                sh "cp ${FRONTEND_SOURCE}/Dockerfile ./Dockerfile"
                sh "mkdir -p conf"
                sh "cp ${FRONTEND_SOURCE}/${SERVICE_MAP.frontend[i].name}/env/default.${INIT_ENV}.conf ./conf/default.conf"
              }
            }

            if (INIT_CODE=='true') {
              echo "-->初始化前端代码..."
              dir (FRONTEND_PATH) {
                for(int i = 0; i < SERVICE_MAP.frontend.size(); ++i){
                  println "Frontend: ${SERVICE_MAP.frontend[i]}"
                  def GIT_URL = "${GIT_BASE_URL}/${SERVICE_MAP.frontend[i].name}.git"
                  echo "Git Url: ${GIT_URL}"
                  checkout([$class: 'GitSCM', branches: [[name: '*/develop']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false], [$class: 'RelativeTargetDirectory', relativeTargetDir: "${SERVICE_MAP.service[i].name}/source"]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '03f9be09-c25f-44cf-8ecc-0b78de5a7044', url: GIT_URL]]])
                }
              }
            }
          }

        }


/******************************** Boss *********************************/

        // Boss后台
        if (initModules.contains('boss')) {
          echo "-->init Boss..."
        }

/******************************** Go *********************************/
        // Go
        def GO_PATH = "${WORK_PATH}/go"
        def GO_SOURCE = "${WORKSPACE}/build/go"


        if (initModules.contains('go')) {
          stage('初始化Go') {
            echo "==>初始化Go..."
            echo "-->初始化Go目录..."
            sh "mkdir -p ${GO_PATH}"
            dir (GO_PATH) {
              for(int i = 0; i < CONF.go.size(); ++i){
                println "-->初始化${CONF.go[i].name}"
                def pkgName = SRV_MAP[CONF.go[i].name].appName
                echo "go package name: ${pkgName}"

                // 拷贝目录、文件
                sh "cp -rf ${GO_SOURCE}/${CONF.go[i].name} ./"
                sh "cp ${WORKSPACE}/build/build.${INIT_ENV}.sh ./${CONF.go[i].name}/build.sh"
                def GO_APP_PATH = "${GO_PATH}/${CONF.go[i].name}/source"
                sh "mkdir -p ${GO_APP_PATH}"

                // 编译Go包
                if (INIT_CODE=='true') {
                  echo "-->编译构建Go程序..."
                  def respMap = buildGo {
                      GO_PACKAGE = CONF.go[i].name
                      BASE_PATH = "/go-build/"
                  }
                  unstash "go-packages"
                }

                // 配置
                if (CONF.go[i].name=='go-pay-service') {
                  dir ("${GO_PATH}/${CONF.go[i].name}") {
                    sh "cp env/service.${INIT_ENV}.json source/service.json"
                    sh "chmod +x *.sh"
                    sh "chmod +x source/*.sh"
                    sh "cp ../package/go_pay_service source/"
                  }
                }else{
                  dir ("${GO_PATH}/${CONF.go[i].name}") {
                    sh "cp env/gateway.${INIT_ENV}.json source/gateway.json"
                    sh "chmod +x *.sh"
                    sh "chmod +x source/*.sh"
                    sh "cp ../package/${pkgName} source/"
                  }
                }

              }
            }


          }
        }

/*
        if (INIT_CODE=='true') {
          echo "-->编译构建Go程序..."
          for(int i = 0; i < CONF.go.size(); ++i){
            def goName = SRV_MAP[CONF.go[i].name].appName
            echo "go name: ${goName}"

            def respMap = buildGo {
                GO_PACKAGE = CONF.go[i].name
                BASE_PATH = "/go-build/"
            }

            echo "Resp: ${respMap}"
            echo "job name: ${respMap.JOB_NAME}"
            echo "build number: ${respMap.BUILD_NUMBER}"
          }

          dir(WORKSPACE) {
            unstash "go-packages"
          }
        }
*/



        if (initModules.contains('go')) {
          echo "-->init Go..."
          /*
          // 初始化编译Go程序
          if (GET_GO_PACKAGE=='true') {
            echo "-->编译打包最新Go程序..."
            node ('node-147') {
              echo "Build No: ${BUILD_NUMBER}"
              def CONF = optConf()
              def GO_MAP = CONF.go
              echo "Service Map: ${GO_MAP}"

              def GO_GIT_URLS=['go-gateway':'https://git.snsshop.net/OptPrime/go-gateway-h5.git','go-mch-gateway':'https://git.snsshop.net/OptPrime/go-gateway-pc-merchant.git']
              //def GO_TYPE = "go-mch-gateway"

              for(int i = 0; i < GO_MAP.size(); ++i){
                def GIT_URL = "${CONF.common.gitBaseUrl}/${GO_MAP[i].repos}.git"
                echo "Git Url: ${GIT_URL}"
                def GO_TYPE = GO_MAP[i].name
                def GO_PATH = "/go-build/${GO_TYPE}"
                def SRC_PATH="/go-build/${GO_TYPE}/src/${GO_TYPE}"
                def BIN_PATH = "/go-build/${GO_TYPE}/bin"

                if( GO_MAP[i].name=='go-mch-gateway'){
                   stage ('编译打包Go程序'){
                  echo "checkout SCM..."
                  checkout([$class: 'GitSCM', branches: [[name: '*\/develop']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: SRC_PATH]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '03f9be09-c25f-44cf-8ecc-0b78de5a7044', url:GIT_URL]]])
                  sh "ls ${SRC_PATH}"
                  withEnv(['PATH+goroot=/usr/local/go/bin',"PATH+gobin=${BIN_PATH}","GOPATH=${GO_PATH}"]) {
                      //sh 'export'
                      sh "echo ${PATH}"
                      sh "go env GOPATH"
                      dir ("${SRC_PATH}"){
                       sh "go install"
                      }
                     }
                  sh "cp ${BIN_PATH}/${GO_TYPE} ./"
                  sh "pwd && ls -l"
                  //archiveArtifacts GO_TYPE
                  archiveArtifacts artifacts: GO_TYPE
                 }
                }

              }
            }
          }
          */
        }




/*************************** 初始化镜像 ******************************/
  if (INIT_BUILD=='true') {
    if (INIT_BUILD_PHP=='true') {
      stage('构建PHP服务镜像') {
        echo "==>构建PHP服务镜像..."

          for(int i = 0; i < SERVICE_MAP.service.size(); ++i){
            println "Service: ${SERVICE_MAP.service[i]}"
            dir ("${WORK_PATH}/build/services/${SERVICE_MAP.service[i].name}") {
                sh "cd source && ./gen-thirft.sh"
                sh "./build.sh ${TAG} 初始化构建"
              }

          }
      }
    }

    if (INIT_BUILD_FRONTEND=='true') {
      stage('构建前端服务镜像') {
        echo "==>构建前端服务镜像..."

          for(int i = 0; i < SERVICE_MAP.frontend.size(); ++i){
            println "Frontend: ${SERVICE_MAP.frontend[i]}"
            dir ("${WORK_PATH}/build/frontend/${SERVICE_MAP.frontend[i].name}") {
                sh "./build.sh ${TAG} 初始化构建"
              }

          }
      }
    }
  }


// 初始化Go配置
  /*
    stage ('环境配置') {
      echo "==>环境配置..."
      dir ("${WORK_PATH}/build/go/go-gateway") {
        sh "cp env/gateway.${INIT_ENV}.json source/gateway.json"
        sh "chmod +x source/*.sh"
        //sh "chmod 755 source/go_gateway"
      }
      dir ("${WORK_PATH}/build/go/go-mch-gateway") {
        sh "cp env/gateway.${INIT_ENV}.json source/gateway.json"
        //sh "cp env/Dockerfile.${INIT_ENV} ./Dockerfile"
        sh "chmod +x source/*.sh"
        //sh "chmod 755 source/go_mch_gateway"
      }
      dir ("${WORK_PATH}/build/go/go-pay-service") {
        sh "cp env/service.${INIT_ENV}.json source/service.json"
        sh "chmod +x source/*.sh"
        //sh "chmod 755 source/go_pay_service"
      }
      for(int i = 0; i < SERVICE_MAP.go.size(); ++i){
        dir ("${WORK_PATH}/build/go/${SERVICE_MAP.go[i].name}") {
            sh "cp ../../build.${INIT_ENV}.sh ./build.sh"
            sh "chmod +x build.sh"
            sh "sed -i 's/service-name/${SERVICE_MAP.go[i].name}/g' build.sh"
            sh "chmod +x source/*.sh"
        }
      }
    }
    */
    sh "ls"
}
