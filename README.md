# 🎓 Student Management System

A production-grade, full-stack Student Management Application built with Spring Boot, HTML/JS, and MySQL, fully containerized and deployed using an automated Jenkins CI/CD pipeline to AWS.

## 🌟 Key Features

### 🔐 Security & Identity 
* **JWT Authentication:** Secure user logins and registrations.
* **Token-based API Protection:** All sensitive backend endpoints are locked behind Spring Security token verification.
* **Role-Based Users:** Safe administrative access.

### 👥 Student Management
* **Full CRUD Functionality:** Create, Read, Update, and Delete student records on the fly.
* **Dynamic Media Uploading:** Upload student profile photos instantly leveraging **AWS S3 Cloud Storage**.
* **Advanced Data Table:** Filter by variables, dynamically sort (Age ascending/descending, Default views, Skill matching), and perform seamless pagination.

### 🚀 DevOps & CI/CD
* **Dockerized Architecture:** Separated Spring Boot app and MySQL database living within a unified internal Docker bridge network (`student_network`).
* **Continuous Integration:** Fully automated Jenkins Pipeline (`Jenkinsfile`).
* **Code Quality Assurance:** Integrated **SonarQube** code analysis enforcing quality gates automatically on new commits.
* **Continuous Deployment:** Zero-downtime automated deployment to an AWS EC2 Ubuntu Host.

---

## 🛠️ Technology Stack

| Domain | Technology / Framework |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot (Web, Data JPA, Security) |
| **Frontend** | HTML5, Vanilla CSS (Glassmorphism UI), Vanilla JS |
| **Database** | MySQL 8 |
| **Storage** | Amazon Web Services (AWS) S3 |
| **Containers** | Docker Engine, Docker Compose v2 |
| **CI/CD** | Jenkins, Maven, Git, SonarQube |
| **Cloud Hosting** | AWS EC2 (Ubuntu 24.04) |

---

## ⚙️ Architecture & Data Structures

### Database Models
- **`User` Table:** `id`, `username`, `password`, `role`
- **`Student` Table:** `id`, `name`, `rollNumber`, `branch`, `college`, `skills`, `email`, `phone`, `age`, `gender`, `imageUrl`

### System Workflow
1. User interacts with the HTML dashboard.
2. The `script.js` securely captures user token and form data (including `multipart/form-data` for images).
3. The Spring Boot `@RestController` intercepts the requests.
4. Images are autonomously dispatched using the `AWS SDK` directly to an S3 Bucket.
5. Entity data is mapped and processed natively using Spring Data JPA with the internal MySQL Database container.

---

## 🚀 Running Locally

1. **Clone the repository:**
   ```bash
   git clone https://github.com/raghavendra2006/Student-Management.git
   cd Student-Management
   ```

2. **Supply AWS Environment Variables:**
   You must either add these inside of a `.env` file within the system directory, or inject them at runtime:
   ```env
   AWS_ACCESS_KEY_ID=your_access_key
   AWS_SECRET_ACCESS_KEY=your_secret_key
   AWS_S3_BUCKET=your_s3_bucket_name
   ```

3. **Start the Database & Application using Docker:**
   ```bash
   docker compose up -d
   ```

4. **Access the application:**
   - **Frontend UI:** `http://localhost:8080/signup`
   - **Backend API Testing / Health:** `http://localhost:8080/test`

---

## 🔄 Automated Deployment Pipeline (Jenkins)

The included `Jenkinsfile` orchestrates the complete production lifecycle:
1. **Checkout SCM:** Clones code dynamically using Git plugins.
2. **Build Jar:** Uses Maven to clean and package the Spring Boot executable `.jar`.
3. **Sonar Analysis:** Injects temporary properties and analyzes the code to ensure bug-free pushes.
4. **Docker Build:** Leverages the `Dockerfile` to build a `raghavendra76/student-management:latest` image based on Eclipse Temurin 17-jdk.
5. **Docker Hub Push:** Streams the container into the cloud repository seamlessly.
6. **Deploy to EC2:** Uses temporary SSH authorization to execute pulling and `docker compose up -d` execution cleanly on the remote production server.

---

## 📜 API Documentation

### Auth Controllers
* `POST /auth/register` (Registers new admin profiles)
* `POST /auth/login` (Fetches the `Authorization` Bearer Token)

### App Controllers
* `POST /students` (Inserts students dynamically alongside file/image parameters)
* `GET /students` (Pulls standard raw array records)
* `PUT /students/{id}` (Modifies student records dynamically)
* `DELETE /students/{id}` (Wipes out records safely)
* `GET /students/filter` (Handles UI sorting and dynamic query parameters)

*Developed by Raghavendra*
