package sctek.opt

class Utils implements Serializable {
  def steps
  Utils(steps) {this.steps = steps}
  def mvn(args) {
    steps.sh "${steps.tool 'Maven'}/bin/mvn -o ${args}"
  }
  def tools(message) {
    steps.sh "echo ${message}"
  }
  def echo(message) {
    steps.echo "Your Msg: ${message}"
  }
  def sh(cmd) {
    steps.sh cmd
  }
  def testVerify() {
          stage ('Verify'){
              try {
                  input id: 'Deploy', message: 'Is Blue node fine? Proceed with Green node deployment?', ok: 'Deploy!'
                  echo "passed..."
              } catch (error) {
                  echo "there is an error..."
              }
          }
      }
  def json(jsonstr) {
    return "json test..."
  }
  /*
  @NonCPS
  def jsonParse(def json) {
      import groovy.json.JsonSlurperClassic
      new groovy.json.JsonSlurperClassic().parseText(json)
  }
  */


}
