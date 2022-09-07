pipeline {
    agent {
        docker { image 'adoptopenjdk:17' }
    }

    stages {
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'build/libs/*-all.jar', fingerprint: true
        }
    }
}
