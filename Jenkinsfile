pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://197.140.142.82:9000/'
    }

    stages {
        stage('Test') {
            steps {
                bat 'gradlew compileJava'
                bat 'gradlew clean test'
                junit '**/build/test-results/test/*.xml'
                cucumber(
                    fileIncludePattern: '**/cucumber-report.json,**/example-report.json',
                    jsonReportDirectory: 'reports'
                )
            }
        }

        stage('Code Analysis') {
            steps {
                withSonarQubeEnv('mySonarQube') {
                    bat """
                        ./gradlew sonar \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.gradle.skipCompile=true
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    timeout(time: 1, unit: 'HOURS') {
                        def qualityGate = waitForQualityGate()
                        if (qualityGate.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qualityGate.status}"
                        }
                    }
                }
            }
        }

        stage('Build') {
            steps {
                bat 'gradlew clean build'
                bat 'gradlew javadoc'
                archiveArtifacts artifacts: [
                    '**/build/libs/*.jar',
                    '**/build/docs/**'
                ].join(','), fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo "Deploying to Maven Repository"
                    echo "Using Repository URL: ${env.MAVEN_REPO_URL}"

                    // Exécution de la commande Gradle sans avoir à passer les credentials manuellement
                    bat './gradlew publish'
                }
            }
        }


        stage('Notifications') {
            steps {
                script {
                    def buildStatus = currentBuild.result ?: 'SUCCESS'
                    def slackColor = buildStatus == 'SUCCESS' ? 'good' : 'danger'
                    def slackMessage = buildStatus == 'SUCCESS' ?
                        "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}" :
                        "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}"

                    // Sending Slack notification with color
                    slackSend(channel: '#jenkinscicd', message: slackMessage, color: slackColor)

                    // Sending email notification
                    mail to: 'wassimbeldjoudi.wb@gmail.com',
                         subject: "${buildStatus} - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                         body: "The build and deployment for ${env.JOB_NAME} #${env.BUILD_NUMBER} has ${buildStatus == 'SUCCESS' ? 'succeeded' : 'failed'}."
                }
            }
        }
    }
}
