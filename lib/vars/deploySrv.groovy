
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    println "Build Node: ${config.NODE}"
    ut = config.UT


    // now build, based on the configuration provided
    node ("manager-132") {
        def WORKDIR = "${WORKSPACE}/services/${config.IMG_NAME}"
        echo "Work Directory: ${WORKDIR}"
        def CONF = config.ENV_CONF
        echo "ENV CONF: ${CONF}"
        def BASE_DIR = CONF['WorkDir']
        echo "Build env: ${CONF.env}"

        def deployFile = """
          version: \'3\'\\n
          services:\\n
            _serviceName:\\n
              image: \"reg.ci.snsshop.net/opt/${config.BUILD_ENV}/${config.IMG_NAME}:${config.IMG_TAG}\"\\n
              networks:\\n
                - opt-net\\n
          networks:\\n
            opt-net:
        """



        stage ('部署服务') {
          echo "===部署服务: ${config.IMG_NAME}==="
          def srvName = CONF.services[config.IMG_NAME].name
          echo "Service Name: ${srvName}"

          sh """
          echo "version: \'3\'" >> deploy.yml
          echo "services:" >> deploy.yml
          echo "  ${srvName}:" >> deploy.yml
          echo "    image: \"reg.ci.snsshop.net/opt/${config.BUILD_ENV}/${config.IMG_NAME}:${config.IMG_TAG}\"" >> deploy.yml
          echo "    networks:" >> deploy.yml
          echo "      - opt-net" >> deploy.yml
          echo "networks:" >> deploy.yml
          echo "  opt-net:" >> deploy.yml
          cat deploy.yml
          """
          sh "docker stack deploy -c deploy.yml Fix"

          /*
          dir(WORKDIR) {
              echo "deploy..."
              sh "pwd && ls ../"
              sh "echo ${deployFile} > deploy.yml"
              sh "echo "
              //sh "sed -i 's/_serviceName/${srvName}/g' ../deploy.yml"
              //sh "sed -i 's/_buildEnv/${config.BUILD_ENV}/g' ../deploy.yml"
              //sh "sed -i 's/_imageName/${config.IMG_NAME}/g' ../deploy.yml"
              //sh "sed -i 's/_imgTag/${config.IMG_TAG}/g' ../deploy.yml"
              //sh "cat ../deploy.yml"
              sh "docker stack deploy -c deploy.yml Fix"
          }
          */
          echo "Finished..."
        }
    }
    return "true"
}
