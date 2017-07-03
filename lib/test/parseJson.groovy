#!groovy
@Library('cilib')_
//library 'mylib'
import sctek.opt.Utils
import sctek.opt.json
//import groovy.json.JsonSlurperClassic
//def jsonSlurper = new JsonSlurperClassic()
def ut = new Utils(steps)

echo "env: ${job_env}"
def jobEnv = job_env
def jstr = '{"dev":"aa","grey":"bb"}'

//def parsed = json.jsonParse(jstr)
def parsed = parseJson(jstr)
echo "Parsed Json: ${parsed}"
echo "dev: ${parsed['dev']}"

node {
    echo "just a test..."
}
