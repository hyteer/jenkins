from jenkinsapi.jenkins import Jenkins

def get_server_instance():
    jenkins_url = 'http://jenkins.ci.snsshop.net'
    server = Jenkins(jenkins_url, username='devops', password='123456')
    return server

if __name__ == '__main__':
    print(get_server_instance().version)
