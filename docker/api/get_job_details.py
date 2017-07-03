from jenkinsapi.jenkins import Jenkins

def get_server_instance():
    jenkins_url = 'http://jenkins.ci.snsshop.net'
    server = Jenkins(jenkins_url, username='devops', password='123456')
    return server

"""Get job details of each job that is running on the Jenkins instance"""
def get_job_details():
    # Refer Example #1 for definition of function 'get_server_instance'
    server = get_server_instance()
    for job_name, job_instance in server.get_jobs():
        print('Job Name:%s' % (job_instance.name))
        print('Job Description:%s' % (job_instance.get_description()))
        print('Is Job running:%s' % (job_instance.is_running()))
        print('Is Job enabled:%s' % (job_instance.is_enabled()))

def get_keys():
    JK = get_server_instance()
    print('Job Num: %s' % JK.keys())
    print('Job: %s' % JK[JK.keys()[0]])
    print('Job: %s' % dir(JK[JK.keys()[0]]))

def get_job_num():
    server = get_server_instance()
    print('Job Num: %d' % len(server.get_jobs()))

if __name__ == '__main__':
    #print(get_server_instance().version)
    get_keys()
