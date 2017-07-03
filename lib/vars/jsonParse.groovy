import groovy.json.JsonSlurperClassic
@NonCPS
def call(def json) {
    // Any valid steps can be called from this code, just like in other
    // Scripted Pipeline
    return new groovy.json.JsonSlurperClassic().parseText(json)

}
