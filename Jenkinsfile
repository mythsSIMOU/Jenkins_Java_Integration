pipeline {
    agent any

    stages {
        stage('Notification') {
            steps {
                script {
                    // Envoyer une notification par email
                    emailext (
                        subject: "Test Email Notification: ${currentBuild.currentResult}",
                        body: """
                        Bonjour,

                        Ceci est un test de la notification par email depuis Jenkins.

                        Status du build: ${currentBuild.currentResult}
                        Job: ${env.JOB_NAME}
                        Build: ${env.BUILD_NUMBER}

                        Cordialement,
                        Votre pipeline Jenkins
                        """,
                        to: "lw_beldjoudi@esi.dz"
                    )
                }
            }
        }
    }
}
