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
                bat 'gradlew compileJava'
                bat 'gradlew clean test'
                junit '**/build/test-results/test/*.xml'
                cucumber(
                    fileIncludePattern: '**/cucumber.json',
                    jsonReportDirectory: 'build/reports/cucumber'
                )
            }
        }

        // Phase 2 : Code Analysis
                stage('Code Analysis') {
                    steps {
                        script {
                            bat 'gradlew sonar' // Analyse du code avec SonarQube
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

        // Phase 5 : Deploy
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
