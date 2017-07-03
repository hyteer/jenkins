def call(String name = 'human') {
    // Any valid steps can be called from this code, just like in other
    // Scripted Pipeline
    def user_name
    def user_id
    wrap([$class: 'BuildUser']) {
      user_id = env.BUILD_USER_ID
      user_name = env.BUILD_USER
      //echo "USER: ${user_name}"
    }
    return user_name
}
