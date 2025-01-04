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
        stage('Test') {
            steps {
                script {
                    try {
                        bat './gradlew clean test'
                        junit '**/build/test-results/test/*.xml'
                        cucumber buildStatus: 'UNSTABLE',
                                fileIncludePattern: '**/build/reports/cucumber/cucumber-report.json',
                                reportTitle: 'Cucumber Report',
                                classificationsFilePattern: '',
                                trendsLimit: 10
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        notifyBuildStatus('FAILURE', 'Test stage failed')
                        throw e
                    }
                }
            }
        }

        stage('Code Analysis') {
            steps {
                script {
                    try {
                        withSonarQubeEnv('SonarQube') {
                            bat './gradlew sonar'
                        }
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        notifyBuildStatus('FAILURE', 'Code Analysis failed')
                        throw e
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    try {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        notifyBuildStatus('FAILURE', 'Quality Gate check failed')
                        throw e
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    try {
                        bat './gradlew clean build -x test'
                        archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                        bat './gradlew javadoc'
                        archiveArtifacts artifacts: 'build/docs/javadoc/**', fingerprint: true
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        notifyBuildStatus('FAILURE', 'Build stage failed')
                        throw e
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    try {
                        bat './gradlew publish'
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        notifyBuildStatus('FAILURE', 'Deploy stage failed')
                        throw e
                    }
                }
            }
        }
    }

    post {
        success {
            script {
                notifyBuildStatus('SUCCESS', 'Pipeline completed successfully')
            }
        }
        failure {
            script {
                notifyBuildStatus('FAILURE', 'Pipeline failed')
            }
        }
    }
}

def notifyBuildStatus(String status, String message) {
    // Email notification
    emailext (
        subject: "Pipeline ${status}: ${currentBuild.fullDisplayName}",
        body: """<p>Build Status: ${status}</p>
                <p>Message: ${message}</p>
                <p>Build URL: ${env.BUILD_URL}</p>""",
        to: 'lw_beldjoudi@esi.dz',
        mimeType: 'text/html',
        recipientProviders: [[$class: 'DevelopersRecipientProvider']]
    )

    // Slack notification
    def color = status == 'SUCCESS' ? 'good' : 'danger'
    slackSend(
        channel: '#dev-team',
        color: color,
        message: "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})\n${message}"
    )
}