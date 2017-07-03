
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
      	def WORKDIR = "${WORKSPACE}/${config.IMG_NAME}"
        def CODE_DIR = "${WORKDIR}/_code_"
      	echo "Work Directory: ${WORKDIR}"

        stage ('拉取Go程序') {
          echo "===拉取Go包: ${config.IMG_NAME}==="
          //dir("${WORKSPACE}/${config.SRV_DIR}") {
          //sh 'date && ls ${PWD}'
          ut.sh "ls ${WORKDIR}"
          ut.sh "rm -rf ${WORKDIR}/_code_"
          echo "ServiceName: ${config.IMG_NAME}"
          echo "ServiceDir: ${config.IMG_NAME}"
          ut.sh "echo ${config.RES_DOMAIN}"
          //ut.sh "echo ${config.RES_IP} ${config.RES_DOMAIN}>>/etc/hosts;"
          dir (WORKDIR){
              ut.sh "mkdir -p _code_ && cd _code_ && rm -rf *"
            }
      	  dir (CODE_DIR) {
              ut.sh "wget http://${config.RES_DOMAIN}/go/${config.GO_PACKAGE}"
              ut.sh "ls && mkdir -p go && tar -xf ${config.GO_PACKAGE} -C go"
              //sh "wget http://downloads.vikduo.com/go/go_gateway_gray.tar.xz"
              //sh "ls && mkdir -p go_gray && tar -xf go_gateway_gray.tar.xz -C go_gray"
              //sh "cp -rf go_gray/* go/"
              sh "ls ${WORKDIR}"
              sh "cp ../gateway.${config.BUILD_ENV}.json go/gateway.json && ls go"
              /****Debug***/
              sh "rm go/go_gateway"
              sh "wget http://downloads.vikduo.com/auto/go/go_gateway_debug_20170601"
              sh "mv go_gateway_debug_20170601 go/go_gateway"
              /****Debug***/

            }
          }

          /*
          stage ('修改配置') {
            echo "===修改Go配置文件..."
            dir(CODE_DIR) {
              sh "sed -i 's/10.100.100.70/10.100.100.141/g' go/gateway.json"
            }
          }
          */

          stage ('构建Go镜像') {
            echo "===Build Image: ${config.IMG_NAME}==="
            dir(WORKDIR) {
                // some block
                sh 'ls ${PWD}'
                sh 'chmod +x scripts/*'
                ut.sh "docker build -t ${config.REG_SERVER}/opt/${config.IMG_NAME}:${config.VERSION} ."
                ut.sh "docker push ${config.REG_SERVER}/opt/${config.IMG_NAME}:${config.VERSION}"
            }
        }

    }
}
