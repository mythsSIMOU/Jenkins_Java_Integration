pipeline {
    agent none  // Désactive l'agent global

    stages {
        stage('Test') {
            agent { label 'master' }  // Spécifie explicitement l'agent
            steps {
                bat 'gradlew compileJava'
                bat 'gradlew clean test'
                junit '**/build/test-results/test/*.xml'
                cucumber(
                    fileIncludePattern: '**/cucumber.json',
                    jsonReportDirectory: 'build/reports/cucumber'
                )
            }
        }

        stage('Code Analysis') {
            agent { label 'master' }  // Spécifie explicitement l'agent
            steps {
                withSonarQubeEnv('sonarqube') {
                    bat """
                        gradlew sonar \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.gradle.skipCompile=true
                    """
                }
            }
        }

        stage('Quality Gate') {
            agent { label 'master' }
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
            agent { label 'master' }
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
            agent { label 'master' }
            steps {
                script {
                    bat """
                        gradlew publish -PmavenRepoUsername=${MAVEN_REPO_USERNAME} -PmavenRepoPassword=${MAVEN_REPO_PASSWORD}
                    """
                }
            }
        }

        stage('Notifications') {
            agent { label 'master' }
            steps {
                script {
                    currentBuild.result = currentBuild.result ?: 'SUCCESS'

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
