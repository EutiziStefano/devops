import java.text.SimpleDateFormat
pipeline {
  agent {label 'master'}
  options {
      buildDiscarder(logRotator(numToKeepStr: '5'))
  }
  stages {
    stage('validation') {
      steps {
        script {
          if (!"${ARTIFACT}"?.trim()) {
            currentBuild.result = 'ABORTED'
            error('ARTIFACT is empty')
          }
          if (!"${BUILDPATH}"?.trim()) {
            currentBuild.result = 'ABORTED'
            error('BUILDPATH is empty')
          }
          if (!"${VERSION_TAG}"?.trim()) {
            currentBuild.result = 'ABORTED'
            error('TEMPLATE_VERSION_TAG is empty')
          }
          if (!"${GIT_URL}"?.trim()) {
            currentBuild.result = 'ABORTED'
            error('TEMPLATE_CONF_GIT_URL is empty')
          }
          if (!"${GIT_CREDENTIAL_ID}"?.trim()) {
            currentBuild.result = 'ABORTED'
            error('GIT_CREDENTIAL_ID is empty')
          }
        }
      }
    }
    stage('CleanWS') {
     steps{
      script {
      deleteDir()
      }
     }
   }
   stage('Git checkout') {
      steps {
        checkout(
          [$class           : 'GitSCM', branches: [[name: "${VERSION_TAG}"]], doGenerateSubmoduleConfigurations: false,
          extensions       : [[$class: 'RelativeTargetDirectory', relativeTargetDir: '']],
          submoduleCfg     : [],
          userRemoteConfigs: [[credentialsId: "${GIT_CREDENTIAL_ID}", url: "${CONF_GIT_URL}"]]]
        )
      }
    }
    stage('Docker build') {
      steps {
        script {
           sh 'cd ${BUILDPATH} && sudo docker build -t ${ARTIFACT}:${VERSION_TAG} .'
        }
      }
    }

    stage('Docker login') {
      steps {
        script {
           sh 'cat /var/lib/jenkins/keyfile.json | sudo docker login -u _json_key --password-stdin https://eu.gcr.io'
        }
      }
    }

    stage('Docker tag') {
      steps {
        script {
           sh 'sudo docker tag ${ARTIFACT}:${VERSION_TAG} eu.gcr.io/di-devops-lab/${ARTIFACT}:${VERSION_TAG}'
        }
      }
    }

    stage('Docker push') {
      steps {
        script {
           sh 'sudo docker push eu.gcr.io/di-devops-lab/${ARTIFACT}:${VERSION_TAG}'    
        }
      }
    }
  }
}
