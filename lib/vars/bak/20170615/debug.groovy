
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
    myMap = config.MAP
    echo "Map: ${myMap}"
    echo "Test Param: ${config.PARAM}"
    echo "Map detail: ${myMap[0]['name']}"
    for (int i = 0; i < myMap.size(); ++i) {
      echo "Map Service: ${myMap[i]}..."
    }


    // now build, based on the configuration provided
    node (config.NODE) {
      	def WORKDIR = "${WORKSPACE}/${config.SRV_NAME}"
        def CODE_DIR = "${WORKDIR}/_code_"
      	echo "Work Directory: ${WORKDIR}"

        stage ('构建镜像Consul') {
          //echo "=== Build: ${config.SRV_NAME} ==="
          //ut.sh "ls ${WORKDIR}"
          echo "shared lib debug..."


      }
    }
}
