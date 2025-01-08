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
                    jsonReportDirectory: 'reports'  // Répertoire des fichiers JSON (reports)
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
                    currentBuild.result = currentBuild.result ?: 'SUCCESS'

                    // Slack notification for successful builds
                    if (currentBuild.result == 'SUCCESS') {
                        slackSend(channel: '#jenkinscicd', message: "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}")
                    } else {
                        slackSend(channel: '#jenkinscicd', message: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}")
                    }

                    // Email notifications
                    if (currentBuild.result == 'SUCCESS') {
                        echo 'Sending success notifications...'
                        mail to: 'wassimbeldjoudi.wb@gmail.com',
                             subject: "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                             body: "The build and deployment for ${env.JOB_NAME} #${env.BUILD_NUMBER} was successful."
                    } else {
                        echo 'Sending failure notifications...'
                        mail to: 'wassimbeldjoudi.wb@gmail.com',
                             subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                             body: "The build for ${env.JOB_NAME} #${env.BUILD_NUMBER} failed. Check the logs for details."
                    }
                }
            }
        }
    }
}
