
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    //def buildNode = config.node
    println "Build Node: ${config.NODE}"
    ut = config.UT
    def EnvConf = [
      opsGitUrl:'http://10.100.100.54/sctek/workflow.git',
      resIP: "10.100.100.31",
      resDomain: "downloads.vikduo.com",
      gitBaseUrl: "https://git.snsshop.net/OptPrime",
      registryServer: "reg.ci.snsshop.net",
      dev:[
        credentialsId: "bee70a17-6e2f-472b-ba14-285b77af3e38",
        opsCredentialsId:'9984ed7d-57af-45b9-b473-2e6eeafdfa7d'
      ],
      master:[
        credentialsId: "03f9be09-c25f-44cf-8ecc-0b78de5a7044",
        opsCredentialsId:'22329e74-d7e1-48da-a0a9-a71f18799491'
      ],
      services:[
        'service-base':[name:'Base',repos:'service-base'],
        'service-qrcode':[name:'Qrcode'],
        'service-third-party':[name:'ThirdParty'],
        'service-third-party-interface':[name:'ThirdPartyInterface'],
        'service-pay':[name:'Pay'],
        'service-order':[name:'Order'],
        'service-shop':[name:'Shop'],
        'service-product':[name:'Product'],
        'service-message':[name:'Message'],
        'service-user':[name:'Users'],
        'service-merchant':[name:'Qrcode'],
        'service-shopping-cart':[name:'ShoppingCart'],
        'gateway-app-merchant':[name:'gateway-app-merchant'],
        'gateway-app-salesman':[name:'gateway-app-salesman'],
        'gateway-boss':[name:'Boss']
      ]

    ]
    def GIT_OPS_URL = "http://10.100.100.54/sctek/workflow.git"
    def OPS_CREDENTIALS_ID = "9984ed7d-57af-45b9-b473-2e6eeafdfa7d"
    echo "JobEnv: ${config.JOB_ENV}"
    //import groovy.json.JsonSlurper
    //import groovy.json.JsonSlurper
    //def jstr = '''{"dev":"aa","grey":"bb"}'''
    //def slurped = config.JSON.parseText(jstr)
    //def slurped = ut.json(jstr)
    //echo "Parsed json string: ${slurped}"
    def WORK_DIR
    echo "Env Config: ${EnvConf}"


    // Workdir Preparation
    node (config.NODE) {
        def UTILS_DIR = "${WORKSPACE}/utils"
        WORK_DIR = WORKSPACE
        stage ('环境准备') {
          echo "=== Start: 环境准备 ===>"
          sh 'date && ls'
          ut.sh "rm -rf *"
          //checkout([$class: 'GitSCM', branches: [[name: '*/dev']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '9984ed7d-57af-45b9-b473-2e6eeafdfa7d', url: config.GIT_OPS_URL]]])
          //checkout([$class: 'GitSCM', branches: [[name: '*/fix']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: config.OPS_CREDENTIALS_ID, url: config.GIT_OPS_URL]]])
          checkout([$class: 'GitSCM', branches: [[name: "*/${config.JOB_ENV}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: EnvConf[config.JOB_ENV]['opsCredentialsId'], url: EnvConf['opsGitUrl']]]])
          sh 'ls'
          /*
          dir ('config'){
            def conf =  jsonParse(readFile("${config.JOB_ENV}.json"))
            echo "json conf: ${conf}"
          }
          */
          ut.sh "chmod +x ${UTILS_DIR}/*"
          echo "=== Finished ==="
          //echo "debug..."
          //sh "cat gate-go/gateway.fixopt.json"
        }
    }
    echo "Work Directory: ${WORK_DIR}"
    EnvConf['env'] = config.JOB_ENV
    EnvConf['WorkDir'] = WORK_DIR

    return EnvConf
}
