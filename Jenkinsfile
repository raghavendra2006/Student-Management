pipeline {
    agent any

    environment {
        // --- Docker Properties ---
        DOCKERHUB_CREDENTIALS = 'dockerhub-credentials-id'
        DOCKER_IMAGE = 'yourdockerhubuser/student-management'
        DOCKER_TAG = "v${env.BUILD_ID}"
        
        // --- EC2 Deployment Properties ---
        EC2_USER = 'ubuntu'
        EC2_IP = 'your-ec2-ip-address'
        SSH_CREDENTIALS_ID = 'ec2-ssh-key-id'
    }

    tools {
        maven 'Maven3' // Assuming Maven is configured as 'Maven3' in Global Tool Configuration
        jdk 'JDK17'    // Assuming JDK 17 is configured as 'JDK17'
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
                // If the Jenkins server doesn't have Maven configured in tools block, 
                // you could also run: sh './mvnw clean package -DskipTests'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Sonar Analysis') {
            steps {
                echo 'Running SonarQube static code analysis...'
                // Requires SonarQube Scanner plugin configured as 'SonarQube-Server'
                withSonarQubeEnv('SonarQube-Server') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
            }
        }

        stage('Docker Login') {
            steps {
                echo 'Logging into DockerHub...'
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                }
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing image to DockerHub...'
                sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                sh "docker push ${DOCKER_IMAGE}:latest"
            }
        }

        stage('Deploy to EC2') {
            steps {
                echo 'Deploying application to EC2 instance...'
                sshagent([env.SSH_CREDENTIALS_ID]) {
                    // Copy docker-compose.yml to EC2 Server
                    sh "scp -o StrictHostKeyChecking=no docker-compose.yml ${EC2_USER}@${EC2_IP}:~/docker-compose.yml"
                    
                    // Run deployment via SSH
                    sh """
                        ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_IP} '
                            export DOCKER_IMAGE=${DOCKER_IMAGE}:latest
                            # Optional: Export secrets into the environment here if needed
                            docker-compose down
                            docker-compose pull
                            docker-compose up -d
                        '
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
