
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
        def CONF = config.ENV_CONF
        echo "ENV CONF: ${CONF}"

        stage ('拉取GoMch程序') {
          echo "===拉取Go包: ${config.IMG_NAME}==="
          //dir("${WORKSPACE}/${config.SRV_DIR}") {
          //sh 'date && ls ${PWD}'
          ut.sh "ls ${WORKDIR}"
          ut.sh "rm -rf ${WORKDIR}/_code_"
          echo "ServiceName: ${config.IMG_NAME}"
          echo "ServiceDir: ${config.IMG_NAME}"
          sh "echo ${CONF.resDomain}"
          //ut.sh "echo ${config.RES_IP} ${config.RES_DOMAIN}>>/etc/hosts;"
      	  dir (WORKDIR) {
              sh "mkdir -p ${CODE_DIR}"
              sh "cp -rf app/* ${CODE_DIR}"
              sh "cp conf/gateway.${config.BUILD_ENV}.json ${CODE_DIR}/gateway.json"
              sh "wget http://downloads.vikduo.com/auto/go/go_mch/go_mch_gateway"
              sh "mv go_mch_gateway ${CODE_DIR}/"

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

          stage ('构建GoMch镜像') {
            echo "===Build Image: ${config.IMG_NAME}==="
            dir(WORKDIR) {
                // some block
                sh 'ls ${PWD}'
                sh 'chmod +x scripts/*'
                ut.sh "docker build -t ${CONF.registryServer}/opt/${config.IMG_NAME}:${config.VERSION} ."
                ut.sh "docker push ${CONF.registryServer}/opt/${config.IMG_NAME}:${config.VERSION}"
            }
        }

    }
}
