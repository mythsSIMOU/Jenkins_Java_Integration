pipeline {
    agent any

    environment {
        // Configuration des variables d'environnement
        SONAR_HOST_URL = 'http://197.140.142.82:9000/'
        SONAR_AUTH_TOKEN = credentials('sonar-token') // Token SonarQube stocké dans Jenkins
        MAVEN_REPO_URL = 'https://mymavenrepo.com/repo/2uH666PIedOzsAI77gey/'
        MAVEN_REPO_CREDENTIALS = credentials('maven-repo-credentials') // Credentials Maven stockés dans Jenkins
    }

    stages {
        // Phase 1 : Test
        stage('Test') {
            steps {
                script {
                    bat 'gradlew test' // Exécution des tests unitaires (Windows)
                    junit 'build/test-results/test/**/*.xml' // Archivage des résultats des tests
                    cucumber buildDir: 'build/cucumber-reports', fileIncludePattern: '**/*.json' // Génération des rapports Cucumber
                }
            }
        }

        // Phase 2 : Code Analysis
        stage('Code Analysis') {
            steps {
                script {
                    bat 'gradlew sonarqube' // Analyse du code avec SonarQube (Windows)
                }
            }
        }

        // Phase 3 : Code Quality
        stage('Code Quality') {
            steps {
                script {
                    // Vérification de l'état de Quality Gates
                    bat '''
                        curl -u %SONAR_AUTH_TOKEN%: %SONAR_HOST_URL%/api/qualitygates/project_status?projectKey=your-project-key
                    '''
                    // Si Quality Gates est en échec, le pipeline s'arrête
                }
            }
        }

        // Phase 4 : Build
        stage('Build') {
            steps {
                script {
                    bat 'gradlew build' // Construction du projet (Windows)
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true // Archivage du fichier JAR
                    bat 'gradlew javadoc' // Génération de la documentation (Windows)
                    archiveArtifacts artifacts: 'build/docs/javadoc/**/*', fingerprint: true // Archivage de la documentation
                }
            }
        }

        // Phase 5 : Deploy
        stage('Deploy') {
            steps {
                script {
                    bat 'gradlew publish' // Déploiement du fichier JAR sur MyMavenRepo (Windows)
                }
            }
        }

        // Phase 6 : Notification
        stage('Notification') {
            steps {
                script {
                    // Envoi d'une notification en cas de succès ou d'échec
                    emailext (
                        subject: 'Pipeline Status: ${currentBuild.currentResult}',
                        body: 'Le pipeline a terminé avec le statut : ${currentBuild.currentResult}',
                        to: 'dev-team@example.com'
                    )
                    // Notification sur Slack (si configuré)
                    slackSend (
                        channel: '#dev-team',
                        message: 'Le pipeline a terminé avec le statut : ${currentBuild.currentResult}'
                    )
                }
            }
        }
    }

    post {
        // Actions post-build
        success {
            echo 'Pipeline réussi !'
        }
        failure {
            echo 'Pipeline en échec !'
        }
    }
}