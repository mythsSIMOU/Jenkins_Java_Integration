pipeline {
    agent any

    environment {
        SONAR_HOST_URL = 'http://197.140.142.82:9000/'
        MAVEN_REPO_URL = 'https://mymavenrepo.com/repo/2uH666PIedOzsAI77gey/'
        MAVEN_REPO_USERNAME = 'myMavenRepo'
        MAVEN_REPO_PASSWORD = '123456789'
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
                    bat """
                        gradlew publish -PmavenRepoUsername=${MAVEN_REPO_USERNAME} -PmavenRepoPassword=${MAVEN_REPO_PASSWORD}
                    """
                }
            }
        }

        stage('Notifications') {
            steps {
                script {
                    def buildStatus = currentBuild.result ?: 'FAILURE'
                    def slackColor = buildStatus == 'SUCCESS' ? 'good' : 'danger'
                    def slackMessage = buildStatus == 'SUCCESS' ?
                        "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}" :
                        "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}"

                    // Envoi de la notification Slack avec couleur
                    slackSend(channel: '#jenkinscicd', message: slackMessage, color: slackColor)

                    // Envoi de la notification par email
                    mail to: 'wassimbeldjoudi.wb@gmail.com',
                         subject: "${buildStatus} - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                         body: "Le build et le déploiement pour ${env.JOB_NAME} #${env.BUILD_NUMBER} a ${buildStatus == 'SUCCESS' ? 'réussi' : 'échoué'}."
                }
            }
        }
    }
}
