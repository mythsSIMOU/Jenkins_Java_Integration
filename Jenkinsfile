pipeline {
    agent any

    environment {
        // Adresse e-mail du destinataire
        EMAIL_TO = 'lw_beldjoudi@esi.dz'
    }

    stages {
        // Phase 1 : Build (simulé)
        stage('Build') {
            steps {
                script {
                    echo 'Simulation de la phase de build...'
                    // Simule une étape de build (vous pouvez remplacer cela par une commande réelle)
                    bat 'echo Building the project...'
                }
            }
        }
    }

    post {
        // Actions post-build
        success {
            echo 'Pipeline réussi ! Envoi d\'un e-mail de notification...'
            emailext (
                subject: 'Pipeline réussi : ${JOB_NAME} - Build #${BUILD_NUMBER}',
                body: '''
                    Le pipeline a réussi avec succès !
                    Détails du build :
                    - Job : ${JOB_NAME}
                    - Build #${BUILD_NUMBER}
                    - URL du build : ${BUILD_URL}
                ''',
                to: "${EMAIL_TO}",
                // Utilise les credentials SMTP configurés dans Jenkins (sans référence explicite dans le pipeline)
            )
        }
        failure {
            echo 'Pipeline en échec ! Envoi d\'un e-mail de notification...'
            emailext (
                subject: 'Pipeline en échec : ${JOB_NAME} - Build #${BUILD_NUMBER}',
                body: '''
                    Le pipeline a échoué !
                    Détails du build :
                    - Job : ${JOB_NAME}
                    - Build #${BUILD_NUMBER}
                    - URL du build : ${BUILD_URL}
                ''',
                to: "${EMAIL_TO}",
                // Utilise les credentials SMTP configurés dans Jenkins (sans référence explicite dans le pipeline)
            )
        }
    }
}