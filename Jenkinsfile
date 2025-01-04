pipeline {
    agent any

    environment {
        // Configuration des variables d'environnement (hardcodées)
        SONAR_HOST_URL = 'http://197.140.142.82:9000/'
        SONAR_AUTH_TOKEN = '31506ababc12919cbd806fafe389c7f005c105a3' // Token SonarQube hardcodé
        MAVEN_REPO_URL = 'https://mymavenrepo.com/repo/2uH666PIedOzsAI77gey/'
        MAVEN_REPO_USERNAME = 'myMavenRepo' // Nom d'utilisateur MyMavenRepo hardcodé
        MAVEN_REPO_PASSWORD = '123456789' // Mot de passe MyMavenRepo hardcodé
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
                    // Analyse du code avec SonarQube
                    withSonarQubeEnv('SonarQube') { // Utilise la configuration SonarQube définie dans Jenkins
                        bat 'gradlew sonar'
                    }
                }
            }
        }

        // Phase 3 : Code Quality
        stage('Code Quality') {
            steps {
                script {
                    // Vérification des Quality Gates
                    timeout(time: 10, unit: 'MINUTES') {
                        def qualityGate = waitForQualityGate() // Attend le résultat des Quality Gates
                        if (qualityGate.status != 'OK') {
                            error "Quality Gate failed: ${qualityGate.status}"
                        }
                    }
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
                    // Déploiement du fichier JAR sur MyMavenRepo
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
                    // Envoi d'une notification en cas de succès ou d'échec
                    emailext (
                        subject: 'Pipeline Status: ${currentBuild.currentResult}',
                        body: 'Le pipeline a terminé avec le statut : ${currentBuild.currentResult}',
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
        // Actions post-build
        success {
            echo 'Pipeline réussi !'
        }
        failure {
            echo 'Pipeline en échec !'
        }
    }
}