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
                    bat 'gradlew test' // Exécution des tests unitaires (Windows)
                    junit 'build/test-results/test/**/*.xml' // Archivage des résultats des tests
                    cucumber reportFiles: '**/build/cucumber-reports/*.json' // Génération des rapports Cucumber
                }
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

        // Phase 3 : Code Quality
        stage('Code Quality') {
            steps {
                script {
                    bat """
                        curl -u ${SONAR_AUTH_TOKEN}: ${SONAR_HOST_URL}/api/qualitygates/project_status?projectKey=com.example:mon-projet
                    """
                }
            }
        }

        // Phase 4 : Build
        stage('Build') {
            steps {
                script {
                    bat 'gradlew build' // Construction du projet
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true // Archivage du fichier JAR
                    bat 'gradlew javadoc' // Génération de la documentation
                    archiveArtifacts artifacts: 'build/docs/javadoc/**/*', fingerprint: true // Archivage de la documentation
                }
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

        // Phase 6 : Notification
        stage('Notification') {
            steps {
                script {
                    emailext (
                        subject: 'Pipeline Status success',
                        body: 'Le pipeline a terminé avec le statut : success',
                        to: 'lw_beldjoudi@esi.dz'
                    )
                    slackSend (
                        channel: '#dev-team',
                        message: 'Le pipeline a terminé avec le statut : ${currentBuild.currentResult}'
                    )
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline réussi !'
        }
        failure {
            echo 'Pipeline en échec !'
        }
    }
}
