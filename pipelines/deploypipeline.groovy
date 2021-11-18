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
          if (!"${NAMESPACE}"?.trim()) {
            currentBuild.result = 'ABORTED'
            error('NAMESPACE is empty')
          }
          if (!"${ARTIFACT}"?.trim()) {
            currentBuild.result = 'ABORTED'
            error('ARTIFACT is empty')
          }
          if (!"${CHARTPATH}"?.trim()) {
            currentBuild.result = 'ABORTED'
            error('CHARTPATH is empty')
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
    stage('Kubelogin') {
      steps {
        script {
           sh 'kubectl config set-context --current --namespace=${NAMESPACE}'
        }
      }
    }

    stage('Helm Install') {
      steps {
        script {
           sh 'cd ${CHARTPATH} && helm upgrade -i ${ARTIFACT} .'
        }
      }
    }

  }
}
