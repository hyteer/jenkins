# Gateway by Golang



> 使用说明

```
Usage of ./go_gateway, Version: 1.0.11
Gateway-Apis by K.o.s[longw@sctek.com]!
  -D    Enable Debug mode!
  -config_file string
        Specify the profile path (default "gateway.json")
  -gracefully
        enable shutdonw gracefully!
  -p int
        Set gateway listen port! (default 8080)
```



> 默认的 `gateway.json` 配置如下

```json
{
   "alidev" : {
      "appid" : "2017030706088836",
      "private_key" : "MIICWwIBAAKBgQDT4srv1WwNRNgGQD5og9hbBKKU/eenMh8vK/4w2jzrTaHEL52UlwmnHaalIq0iYnOQqyHOX1kf2K33xiSPEOg88qYC8rgi5dvWNHgnvOtIgo8z3rMDjYHHID/bkfHH3USB8KoINYUQP43tNNcTJd3e+iwY2dlnDeoT1vp828BTAQIDAQABAoGAHfKtOk7REeMMIX4NFBqmht7V1/c3OdOEwtcV+3OtLpjvZ1pPjIP4kk1Hk4meLQD7UpRNl+y0HSM0G5Q+8JW0LT9B3J7Oen8CTOHuh8te8hG3wXFVXF0oOWSXCFxbfBHzYTWF4ARUUmf29pZrUOGbAv8GCYE4BBAwAVPNkrXSSn0CQQDxOxBy2ESPyrEU6EBEPh1BXrdgsu3LdX/flY4FmE7w5b5Q69m1v1Q8piTuHiD0K5O93RXrnmsNCTjUkLI9jLNnAkEA4NvKti2aQNHjJdMDlK4Dd4c73/NhYr1qTR4hWJ4cRcpgqMALyBawpQpeTlpWPdwQbGVT0QgRzaNEM1VMY7/tVwJAKfRByhd+5Rs1bRNvie7bm7DjZ7f2z3niAXq7NpHkuNTcIqrCNG/QMLQcnCU1SgICMfUviMkGiT/fWGjJ9xZvZQJAQY2SIV5WFAOcdxB3gMbiV9xjBj0L7R9PeEAKdK1TjL0dRNUIGkMTKGpHaAxKDCq11wQLVWtG1W59mYGKjaIMbQJABVhGZkA2U2yr7gciKwAhRz1kx94QVwYpkPFe308pAXUT7ilvKId/8/xIMNiHAhv7EqOCYbrxmuFYmTk6n6thrw=="
   },
   "auth_redis" : {
      "hostname" : "10.100.100.70",
      "port" : 7000,
      "pwd" : ""
   },
   "consul" : {
      "addr" : "127.0.0.1:8500"
   },
   "cors" : {
      "allowedorigins" : [ "*" ],
      "cookiedomain" : ".testmopt.snsshop.net"
   },
   "qrcode_redis" : {
      "hostname" : "10.100.100.70",
      "port" : 7700,
      "pwd" : ""
   },
   "redis" : {
      "hostname" : "10.100.100.70",
      "port" : 7400,
      "pwd" : ""
   },
   "svr_list" : [ 0 ],
   "tls" : {
      "cert_file" : "mycert.cert",
      "enable" : false,
      "key_file" : "mykey.key"
   },
   "url" : {
      "callback_url" : "http://go.testmopt.snsshop.net/",
      "gateway_user" : "http://go.testmopt.snsshop.net",
      "notify_url" : "http://go.testmopt.snsshop.net/api/pay/order-notice"
   },
   "wxdev" : {
      "appid" : "wx0c4ea49260ea3008",
      "componentappid" : "wxb6ce1bdb8951b75a",
      "componentappsecret" : "270c0d8139bda2d5dae9cd1dcb2721c4",
      "componenttoken" : "sdlkfjlkdsjfsdfjl",
      "encodingaeskey" : "wx6788a73ef2f8c5a1wx6788a73ef2f8c5a1aaaaaaa"
   },
   "zipkin_kafka" : {
      "addr" : "10.100.100.112:9092"
   }
}
```

> 配置说明

- redis: (用于Get/Set 程序的session的缓存信息,及openid与userid的缓存信息)
- qrcode_redis: (用于网关从指定的redis中Get 后台生成的qrcode的缓存信息)
- tls: (用于开启tls的监听)
- url: (网关业务需配置的几个用到的几个url配置信息，可参考PHP的配置)
- cors: (配置cookie中的域名值)
- zipkin_kafka: (用于配置zipkin的kafka的addr)
- consul:(配置本机consul的addr)
- svr_list:(配置使用的consul的微服务列表数组的对象索引，从0开始)
- auth_redis:(配置微信access_token的a3的redis连接信息)
- wxdev: 配置微信授权需要的开发者信息
- alidev: 配置阿里支付宝授权需要的开发者信息

> 附加说明
- `_unx` 环境支持 `SIGUSR1`,`SIGUSR2`

```sh
pid: go网关的进程id

kill -SIGUSR1 [pid]
reload 配置文件

kill -SIGUSR2 [pid]
开启/关闭 debug
```