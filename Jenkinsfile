pipeline {
    agent any

    stages {
        stage('Compile') {
            steps {
                gradlew('clean', 'classes')
            }
        }
        stage('Unit Tests') {
            steps {
                gradlew('test')
            }
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                }
            }
        }
        stage('Docker Image') {
            steps {
                gradlew('dockerPush')
            }
        }
    }

     stage('Deploy') {
          steps {
            script {
                kubernetesDeploy(kubeconfigId: 'estcluster',
                    configs: '.kubernetes/deployment.yaml',
                    secretName: 'api',
                    dockerCredentials: [
                        [credentialsId: 'f2e16677-d6f8-41b4-a6ba-2c798bdc37c8']
                    ]
                )
            }
          }
        }
      }
}

def gradlew(String... args) {
    sh "./gradlew ${args.join(' ')} -s"
}