
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

        stage('拉取前端代码') {
            echo "=== 拉取前端代码:${config.REPOS_NAME} and ${config.REPOS2_NAME} ==="
            echo "GitUrl: ${CONF.gitBaseUrl}, Branch: ${config.BRANCH}"
            dir (WORKDIR){
              checkout([$class: 'GitSCM', branches: [[name: config.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${WORKDIR}/${config.REPOS_NAME}"], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: CONF.credentialsId, url: "${CONF.gitBaseUrl}/${config.REPOS_NAME}.git"]]])
              checkout([$class: 'GitSCM', branches: [[name: config.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "frontend-pc-merchant"], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: CONF.credentialsId, url: "${CONF.gitBaseUrl}/frontend-pc-merchant.git"]]])
              //checkout([$class: 'GitSCM', branches: [[name: config.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${WORKDIR}/${config.REPOS_NAME2}"], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: CONF.credentialsId, url: "${CONF.gitBaseUrl}/${config.REPOS2_NAME}.git"]]])
              sh "pwd && ls"
            }
            //checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'xxxxdir'], [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'bee70a17-6e2f-472b-ba14-285b77af3e38', url: config.GIT_URL]]])
        }

        stage ('前端Build') {
          echo "==== 前端Build ===="
          echo "build ${config.REPOS_NAME}..."
          dir(WORKDIR) {
            sh "cp res/build.js res/Gruntfile.js  ${config.REPOS_NAME}/"
            sh "cp res/common.js  ${config.REPOS_NAME}/app/scripts/services/common/"
            //sh "sed -i 's/: \'//go.\'/: \'//mgw.\'/g' ${config.REPOS_NAME}/app/scripts/services/common/common.js"
            ut.sh "pwd && ls && mkdir -p ${CODE_DIR}/${config.REPOS_NAME}"

            //*** build ***
            sh "cd ${config.REPOS_NAME} && \
            cp -rf app/bower_components ./ && \
            cnpm install && grunt build:test"
            //sh "mv ${config.REPOS_NAME}/node_modules ./"
            //cnpm install && grunt build:dist"


            ut.sh "cp -rf ${config.REPOS_NAME}/dist ${CODE_DIR}/${config.REPOS_NAME}"
            sh "sed -i 's/testmopt.snsshop.net/${config.BUILD_ENV}.snsshop.net/g' ${CODE_DIR}/${config.REPOS_NAME}/dist/scripts/scripts.*.js"

          }
          echo "build ${config.REPOS2_NAME}..."
          dir(WORKDIR) {
            ut.sh "pwd && ls && mkdir -p ${CODE_DIR}/${config.REPOS2_NAME}"
            //*** build ***
            sh "cd ${config.REPOS2_NAME} && \
            cnpm install && npm run build"
            sh "cp -rf ${config.REPOS2_NAME}/dist ${CODE_DIR}/${config.REPOS2_NAME}"
            //sh "sed -i 's/testmopt.snsshop.net/${config.BUILD_ENV}.snsshop.net/g' ${CODE_DIR}/dist/scripts/scripts.*.js"

          }

        }

        stage ('构建镜像Nginx') {
          echo "=== Build: ${config.IMG_NAME} ==="
          //ut.sh "ls ${WORKDIR}"
          dir(WORKDIR) {
              // some block
              sh 'ls'
              sh 'chmod +x scripts/*'
              echo "Build Env: ${config.BUILD_ENV}"
              sh "sed -i 's/conf/conf\\/${config.BUILD_ENV}/g' Dockerfile"
              sh "sed -i 's/DOMAIN=\"ci\"/DOMAIN=\"${config.BUILD_ENV}\"/g' scripts/deploy.sh"
              //ut.sh """sed -i 's/^IMG_NAME=\".*\"\$/IMG_NAME=\"${config.IMG_NAME}\"/g' Dockerfile"""
              ut.sh "docker build -t ${CONF.registryServer}/opt/${config.IMG_NAME}:${config.VERSION} ."
              ut.sh "docker push ${CONF.registryServer}/opt/${config.IMG_NAME}:${config.VERSION}"
          }
        echo "=== Finished: ${config.IMG_NAME} ==="
      }
    }
}
