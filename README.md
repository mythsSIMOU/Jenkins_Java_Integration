# Jenkins CI/CD Pipeline for Java API

This project is a Jenkins pipeline for automating the CI/CD process of a Java API. It includes:
- Unit and integration testing.
- Code quality analysis with SonarQube.
- Building and deploying the project.
- Email & Slack notifications for pipeline status.

---

## ✨ Pipeline Features

1. **Test**:
   - Run unit tests with JUnit.
   - Generate Cucumber test reports.
   - Archive test results.

2. **Code Analysis**:
   - Analyze code quality with SonarQube.
   - Check Quality Gates.

3. **Build**:
   - Generate a JAR file.
   - Generate JavaDoc documentation.
   - Archive the JAR and documentation.

4. **Deploy**:
   - Deploy the JAR to a Maven repository (MyMavenRepo).

5. **Notification**:
   - Send email & Slack notifications on pipeline success or failure.

---

## 🛠 Technologies Used

- **Languages**: Java, Groovy
- **Frameworks**: JUnit, Cucumber
- **Tools**: Jenkins, SonarQube, Gradle
- **Maven Repository**: MyMavenRepo
- **Notifications**: Email, Slack 
