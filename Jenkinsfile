plugins {
    id 'java'
    id "com.github.spacialcircumstances.gradle-cucumber-reporting" version "0.1.25"
    id 'jacoco'
    id("org.sonarqube") version "4.4.0.3356"
    id 'maven-publish'
}

group = 'com.example'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'io.cucumber:cucumber-java:6.0.0'
    testImplementation 'io.cucumber:cucumber-junit:6.0.0'
    testImplementation 'junit:junit:4.12'
    implementation 'com.sendgrid:sendgrid-java:4.9.3'
}

cucumberReports {
    outputDir = file('build/reports/cucumber') // Répertoire par défaut
    reports = files('reports/cucumber-report.json') // Fichier de rapport par défaut
}

jacocoTestReport {
    reports {
        xml.required.set(true) // Génération du rapport XML
        html.outputLocation.set(file("$buildDir/reports/jacoco/html")) // Répertoire de sortie HTML
    }
}

publishing {
    publications {
        mavenJava(MavenPublication) {
            from components.java
        }
    }

    repositories {
        maven {
            url = uri("https://mymavenrepo.com/repo/2uH666PIedOzsAI77gey/") // URL fixe
            credentials {
                username = "myMavenRepo" // Identifiants fixes
                password = "123456789"
            }
        }
    }
}

tasks.register('sendSlackNotification') {
    doLast {
        def webhookUrl = 'https://hooks.slack.com/services/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'
        def payload = groovy.json.JsonOutput.toJson([text: "Build completed successfully!"])
        def connection = new URL(webhookUrl).openConnection() as HttpURLConnection
        connection.requestMethod = 'POST'
        connection.doOutput = true
        connection.setRequestProperty('Content-Type', 'application/json')
        connection.outputStream.write(payload.bytes)
        println "Slack notification response code: ${connection.responseCode}"
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy jacocoTestReport // Exécuter Jacoco après les tests
}

tasks.javadoc {
    destinationDir = file("$buildDir/docs/javadoc")
    finalizedBy tasks.publish // Publier après la génération de la documentation
}
