
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    //def buildNode = config.node
    println "Build Node: ${config.NODE}"
    ut = config.UT
    //def WORKDIR = "${pwd}/${config.SRV_NAME}"

    // now build, based on the configuration provided
    node (config.NODE) {
      	def WORKDIR = "${WORKSPACE}/${config.SRV_NAME}"
        def CODE_DIR = "${WORKDIR}/_code_/${config.REPOS_NAME}"
      	echo "Work Directory: ${WORKDIR}"

        stage('拉取前端代码') {
            echo "=== 拉取前端代码:${config.SRV_NAME} ==="
            echo "GitUrl: ${config.GIT_URL}, Branch: ${config.BRANCH}"
            checkout([$class: 'GitSCM', branches: [[name: config.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${WORKDIR}/${config.REPOS_NAME}"], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: config.CREDENTIALS_ID, url: config.GIT_URL]]])

            //checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'xxxxdir'], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'bee70a17-6e2f-472b-ba14-285b77af3e38', url: config.GIT_URL]]])
        }

        stage ('前端Build') {
          echo "==== 前端Build ===="
          dir(WORKDIR) {
            ut.sh "pwd && ls && mkdir -p ${CODE_DIR}"

            /*** build ***
            ut.sh "cd ${config.REPOS_NAME} && \
            cp -rf app/bower_components ./ && \
            cnpm install && grunt build"
            *****/

            ut.sh "cp -rf ${config.REPOS_NAME}/dist ${CODE_DIR}/"
            sh "sed -i 's/testmopt.snsshop.net/${config.DOMAIN}.snsshop.net/g' ${CODE_DIR}/dist/scripts/scripts.*.js"

          }
        }

        stage ('构建镜像Nginx') {
          echo "=== Build: ${config.SRV_NAME} ==="
          //ut.sh "ls ${WORKDIR}"
          dir(WORKDIR) {
              // some block
              sh 'ls'
              sh 'chmod +x scripts/*'
              echo "ConfDir: ${config.CONF_DIR}"
              sh "sed -i 's/conf/conf\\/${config.CONF_DIR}/g' Dockerfile"
              //ut.sh """sed -i 's/^IMG_NAME=\".*\"\$/IMG_NAME=\"${config.IMG_NAME}\"/g' Dockerfile"""
              ut.sh "docker build -t ${config.REG_SERVER}/opt/${config.SRV_NAME}:${config.VERSION} ."
              ut.sh "docker push ${config.REG_SERVER}/opt/${config.SRV_NAME}:${config.VERSION}"
          }
        echo "=== Finished: ${config.SRV_NAME} ==="
      }
    }
}
