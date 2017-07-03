package org.foo
class Tools implements Serializable {
  def steps
  Tools(steps) {this.steps = steps}
  def sh(message) {
    steps.sh "echo ${message}"
  }
  def echo(message) {
    steps.echo "Your Msg: ${message}"
  }
  def getSCM(args) {

  }
}
