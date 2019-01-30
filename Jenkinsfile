pipeline {
  
  agent any
    stages {
  
    stage('Maven install & Docker Build') {
      agent any      
      steps {
         sh 'mvn clean install'
        sh 'docker build --build-arg ENVIRONMENT -t roadtomoon/exrates-core-service:$ENVIRONMENT .'
      }
    } 
    stage('Docker pull') {
      agent any
      steps {
        sh 'docker tag roadtomoon/exrates-core-service:$ENVIRONMENT 172.50.50.7:5000/coreservice:$ENVIRONMENT'
        sh 'docker push 172.50.50.7:5000/coreservice:$ENVIRONMENT'
      }
    } 
    stage('Deploy container') {
      steps {
        sh 'docker -H tcp://172.50.50.7:2376 service update --image localhost:5000/coreservice:$ENVIRONMENT $ENVIRONMENT-core-service'
      }
    }
  }  
}
