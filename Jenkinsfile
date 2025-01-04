pipeline {
    agent any

    stages {
        stage('Test') {
            steps {
                script {
                    try {
                        // Run unit tests
                        bat 'gradle test'

                        // Archive test results
                        junit '**/build/test-results/test/*.xml'

                        // Generate Cucumber reports
                        cucumber buildStatus: 'UNSTABLE',
                                reportTitle: 'Cucumber Report',
                                fileIncludePattern: '**/cucumber.json',
                                trendsLimit: 10
                    } catch (Exception e) {
                        notifyFailure('Test')
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
                            bat 'gradle sonar'
                        }
                    } catch (Exception e) {
                        notifyFailure('Code Analysis')
                        throw e
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    try {
                        timeout(time: 1, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    } catch (Exception e) {
                        notifyFailure('Quality Gate')
                        throw e
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    try {
                        // Generate JAR
                        bat 'gradle build -x test'

                        // Generate JavaDoc
                        bat 'gradle javadoc'

                        // Archive artifacts
                        archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                        archiveArtifacts artifacts: 'build/docs/**', fingerprint: true
                    } catch (Exception e) {
                        notifyFailure('Build')
                        throw e
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    try {
                        bat 'gradle publish'
                    } catch (Exception e) {
                        notifyFailure('Deploy')
                        throw e
                    }
                }
            }
        }
    }

    post {
        success {
            script {
                // Send success notification to email
                emailext (
                    subject: "Pipeline Successful: ${currentBuild.fullDisplayName}",
                    body: "The pipeline has completed successfully.",
                    to: 'lw_beldjoudi@esi.dz',
                    mimeType: 'text/html'
                )

                // Send success notification to Slack
                slackSend (
                    color: 'good',
                    message: "Pipeline successful: ${currentBuild.fullDisplayName}"
                )
            }
        }
    }
}

def notifyFailure(String stageName) {
    // Send failure notification to email
    emailext (
        subject: "Pipeline Failed at ${stageName}: ${currentBuild.fullDisplayName}",
        body: "The pipeline has failed at stage ${stageName}.",
        to: 'lw_beldjoudi@esi.dz',
        mimeType: 'text/html'
    )

    // Send failure notification to Slack
    slackSend (
        color: 'danger',
        message: "Pipeline failed at stage ${stageName}: ${currentBuild.fullDisplayName}"
    )
}