pipeline {
    agent any

    environment {
        // --- Docker Properties ---
        DOCKERHUB_CREDENTIALS = 'dockerhubcred'
        DOCKER_IMAGE = 'raghavendra76/student-management'
        DOCKER_TAG = "v${env.BUILD_ID}"
        
        // --- EC2 Deployment Properties ---
        EC2_USER = 'ubuntu'
        EC2_IP = '54.85.195.87'
        SSH_CREDENTIALS_ID = 'ec2-pem-key'
    }

    tools {
        maven 'cseMaven' // Assuming Maven is configured as 'cseMaven' in Global Tool Configuration
        jdk 'javacse'    // Assuming JDK 17 is configured as 'javacse'
    }

    stages {
        stage('Clone Code') {
            steps {
                echo 'Cloning from Git repository...'
                checkout scm
            }
        }

        stage('Build Jar') {
            steps {
                echo 'Building Spring Boot application...'
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Sonar Analysis') {
            steps {
                withSonarQubeEnv('SonarCse') {
                    bat 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                bat "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
            }
        }

        stage('Docker Login') {
            steps {
                echo 'Logging into DockerHub...'
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    // Windows CMD syntax for environment variables
                    bat 'echo %DOCKER_PASS%| docker login -u %DOCKER_USER% --password-stdin'
                }
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing image to DockerHub...'
                retry(3) {
                    bat "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    bat "docker push ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                echo 'Deploying application to EC2 instance...'
                withCredentials([sshUserPrivateKey(credentialsId: env.SSH_CREDENTIALS_ID, keyFileVariable: 'SSH_KEY')]) {
                    // Copy docker-compose.yml to EC2 Server
                    bat "scp -o StrictHostKeyChecking=no -i \"%SSH_KEY%\" docker-compose.yml ${EC2_USER}@${EC2_IP}:~/docker-compose.yml"
                    
                    // Run deployment via SSH
                    bat """
                        ssh -o StrictHostKeyChecking=no -i "%SSH_KEY%" ${EC2_USER}@${EC2_IP} "export DOCKER_IMAGE=${DOCKER_IMAGE}:latest && docker-compose down && docker-compose pull && docker-compose up -d"
                    """
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up Jenkins workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully! The Student Management Backend is live.'
        }
        failure {
            echo 'Pipeline failed! Please check the logs in Jenkins.'
        }
    }
}
