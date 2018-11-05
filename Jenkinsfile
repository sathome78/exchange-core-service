pipeline {
  
  agent any
  
    tools{
        maven 'maven 3.5.2'
          jdk 'java 8'
    }
 
      stages {
      stage ("initialize") {
       steps {
      sh '''
      echo "PATH = ${PATH}"
      echo "M2_HOME = ${M2_HOME}"
      '''
      }
      }
      }
   stage ('Build project') {
    steps {
    dir("project_templates/java_project_template"){
    sh 'mvn clean install'
    }
      }
    }

    
    stage('Docker Build') {
      agent any
      steps {
        sh 'docker build --build-arg ENVIRONMENT -t roadtomoon/exrates-core-service:latest .'
      }
    } 
    stage('Docker pull') {
      agent any
      steps {
        sh 'docker tag roadtomoon/exrates-core-service:latest localhost:5000/coreservice:latest'
        sh 'docker push localhost:5000/coreservice:latest'
      }
    } 
    stage('Deploy container') {
      steps {
        sh 'docker -H tcp://localhost:2375 service update --image localhost:5000/coreservice:latest core-service'
      }
    }
  }  
}
