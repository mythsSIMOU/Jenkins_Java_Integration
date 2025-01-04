pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://197.140.142.82:9000/'
        SONAR_AUTH_TOKEN = '31506ababc12919cbd806fafe389c7f005c105a3'
        MAVEN_REPO_URL = 'https://mymavenrepo.com/repo/2uH666PIedOzsAI77gey/'
        MAVEN_REPO_USERNAME = 'myMavenRepo'
        MAVEN_REPO_PASSWORD = '123456789'
    }

    stages {
        // Phase 1 : Test
        stage('Test') {
            steps {
                script {
                    bat 'gradlew test'
                    junit 'build/test-results/test/**/*.xml'
                    cucumber buildDir: 'build/cucumber-reports', fileIncludePattern: '**/*.json'
                }
            }
        }

        // Phase 2 : Code Analysis
        stage('Code Analysis') {
            steps {
                script {
                    bat 'gradlew sonarqube'
                }
            }
        }

       // Phase 3 : Code Quality
       stage('Code Quality') {
           steps {
               script {
                   def response = bat(script: "curl -u ${SONAR_AUTH_TOKEN}: ${SONAR_HOST_URL}/api/qualitygates/project_status?projectKey=com.example:mon-projet", returnStdout: true).trim()
                   if (response.contains('"status":"ERROR"')) {
                       error "Quality gate failed. Stopping pipeline."
                   }
               }
           }
       }


        // Phase 4 : Build
        stage('Build') {
            steps {
                script {
                    bat 'gradlew build'
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                    bat 'gradlew javadoc'
                    archiveArtifacts artifacts: 'build/docs/javadoc/**/*', fingerprint: true
                }
            }
        }

        // Phase 5 : Deploy
        stage('Deploy') {
            steps {
                script {
                    bat "gradlew publish -PmavenRepoUsername=${MAVEN_REPO_USERNAME} -PmavenRepoPassword=${MAVEN_REPO_PASSWORD}"
                }
            }
        }

        // Phase 6 : Notification
        stage('Notification') {
            steps {
                script {
                    def statusMessage = currentBuild.result == 'SUCCESS' ? 'success' : 'failure'
                    emailext (
                        subject: "Pipeline Status: ${statusMessage}",
                        body: "Le pipeline a terminé avec le statut : ${statusMessage}",
                        to: 'lw_beldjoudi@esi.dz'
                    )
                    slackSend (
                        channel: '#dev-team',
                        message: "Le pipeline a terminé avec le statut : ${statusMessage}"
                    )
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
