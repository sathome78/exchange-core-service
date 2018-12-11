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
        sh 'docker tag roadtomoon/exrates-core-service:$ENVIRONMENT localhost:5000/coreservice:$ENVIRONMENT'
        sh 'docker push localhost:5000/coreservice:$ENVIRONMENT'
      }
    } 
    stage('Deploy container') {
      steps {
        sh 'docker -H tcp://localhost:2375 service update --image localhost:5000/coreservice:$ENVIRONMENT $ENVIRONMENT-core-service'
      }
    }
  }  
}
