pipeline {
    agent any

    stages {
        stage('Email Notification Test') {
            steps {
                script {
                    echo 'Testing email notification...'

                    // Remplacer SUCCESS par FAILURE si vous voulez tester un email d'échec.
                    currentBuild.result = 'SUCCESS'

                    if (currentBuild.result == 'SUCCESS') {
                        mail to: 'lw_beldjoudi@esi.dz',
                             subject: "Test - Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                             body: """Bonjour,
                                      Ceci est un test de notification pour une build réussie.
                                      Projet: ${env.JOB_NAME}
                                      Build Number: ${env.BUILD_NUMBER}
                                      Jenkins URL: ${env.BUILD_URL}
                                      """
                    } else {
                        mail to: 'lw_beldjoudi@esi.dz',
                             subject: "Test - Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                             body: """Bonjour,
                                      Ceci est un test de notification pour une build échouée.
                                      Projet: ${env.JOB_NAME}
                                      Build Number: ${env.BUILD_NUMBER}
                                      Jenkins URL: ${env.BUILD_URL}
                                      """
                    }
                }
            }
        }
    }
}
