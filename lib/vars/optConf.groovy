def call(String name = 'human') {
    // Any valid steps can be called from this code, just like in other
    // Scripted Pipeline
    def OPT_MAP = [
        common:[
          opsGitUrl:'http://10.100.100.54/sctek/workflow.git',
          gitBaseUrl: "https://git.snsshop.net/OptPrime",
          gitBossBaseUrl: "https://git.snsshop.net/newboss",
          resIP: "10.100.100.31",
          resDomain: "downloads.vikduo.com",
          registryServer: "reg.ci.snsshop.net",
        ],
        frontend:[
          [name:'frontend-h5-rest-shop', flag:'true'],
          [name:'frontend-pc-merchant', flag:'true'],
        ],
        go:[
          [name:'go-gateway', repos:'go-gateway-h5', flag:'true'],
          [name:'go-mch-gateway', repos:'go-gateway-pc-merchant', flag:'true'],
          [name:'go-pay-service', repos:'go-pay-service', flag:'true'],
        ],
        service:[
          [name:'service-base', flag:'true'],
          [name:'service-qrcode', flag:'true'],
          //[name:'service-third-party', flag:'true'],
          //[name:'service-third-party-interface', flag:'true'],
          //[name:'service-pay', flag:'true'],
          /*[name:'service-shop', flag:'true'],
          [name:'service-order', flag:'true'],
          [name:'service-product', flag:'true'],
          [name:'service-message', flag:'true'],
          [name:'service-merchant', flag:'true'],
          [name:'gateway-app-merchant', flag:'true'],
          [name:'gateway-boss', flag:'true'],
          [name:'gateway-app-salesman', flag:'true'],*/
          //[name:'service-user', flag:'true'],
         ],
         serviceMap:[
            'go-gateway':[repos:'go-gateway-h5', appName:'go_gateway', type:'go'],
            'go-mch-gateway':[repos:'go-gateway-pc-merchant', appName:'go_mch_gateway', type:'go'],
            'go-pay-service':[repos:'go-pay-service', appName:'go_pay_service', type:'go'],
            'service-base':[name:'Base', type:'php'],
            'service-qrcode':[name:'Qrcode', type:'php'],
            //'service-third-party':[name:'service-third-party', type:'php'],
            //'service-third-party-interface':[name:'service-third-party-interface', type:'php'],
            //'service-pay':[name:'service-pay', type:'php'],
            /*'service-shop':[name:'service-shop', type:'php'],
            'service-order':[name:'service-order', type:'php'],
            'service-product':[name:'service-product', type:'php'],
            'service-message':[name:'service-message', type:'php'],
            'service-merchant':[name:'service-merchant', type:'php'],
            'gateway-app-merchant':[name:'gateway-app-merchant', type:'php'],
            'gateway-app-salesman':[name:'gateway-app-salesman', type:'php'],
            'gateway-boss':[name:'gateway-boss', type:'php'],*/
            //'service-base':[name:'service-user', type:'php'],
         ]
        ]

    return OPT_MAP
}
