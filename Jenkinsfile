pipeline {
    agent any

    environment {
        MYMAVENREPO_URL = credentials('MYMAVENREPO_URL')
        MYMAVENREPO_USERNAME = credentials('MYMAVENREPO_USERNAME')
        MYMAVENREPO_PASSWORD = credentials('MYMAVENREPO_PASSWORD')
        SLACK_WEBHOOK_URL = credentials('SLACK_WEBHOOK_URL')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                script {
                    bat './gradlew test'
                }
            }
        }

        stage('Code Analysis') {
            steps {
                script {
                    bat './gradlew jacocoTestReport sonarqube'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    bat './gradlew build'
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    bat './gradlew publish'
                }
            }
        }

        stage('Notify') {
            steps {
                script {
                    bat './gradlew sendSlackNotification'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution complete.'
        }
        success {
            echo 'Pipeline succeeded.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
