<h1 align="center">🎓 Student Management System</h1>

<p align="center">
  <b>A production-grade, full-stack application built with Spring Boot, Vanilla JS, and MySQL.</b><br>
  <i>Fully containerized and deployed using an automated Jenkins CI/CD pipeline to AWS.</i>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring_Boot-3.5.13-brightgreen?style=for-the-badge&logo=spring" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql" alt="MySQL">
  <img src="https://img.shields.io/badge/Docker-Enabled-blue?style=for-the-badge&logo=docker" alt="Docker">
  <img src="https://img.shields.io/badge/Jenkins-CI/CD-red?style=for-the-badge&logo=jenkins" alt="Jenkins">
  <img src="https://img.shields.io/badge/AWS-EC2_%7C_S3-yellow?style=for-the-badge&logo=amazonaws" alt="AWS">
</p>

---

## 🏗️ System Architecture & Workflow

Below is the comprehensive architecture diagram illustrating the request lifecycle, data flow, and deployment pipeline for the Student Management System.

```mermaid
graph TD
    %% Define Styles
    classDef client fill:#f9f,stroke:#333,stroke-width:2px;
    classDef server fill:#bbf,stroke:#333,stroke-width:2px;
    classDef db fill:#ff9,stroke:#333,stroke-width:2px;
    classDef cloud fill:#ffdb58,stroke:#333,stroke-width:2px;
    classDef devops fill:#ffcccb,stroke:#333,stroke-width:2px;

    %% Nodes
    User(("🧑‍💻 Client (Browser)")):::client
    UI["🖥️ UI (HTML/JS/CSS)"]:::client
    
    subgraph "AWS EC2 Hosting (Dockerized)"
        Gateway["🔌 Port 8080"]:::server
        App["🚀 Spring Boot Backend <br>(REST API & JWT Auth)"]:::server
        MySQL[("🛢️ MySQL 8 Database <br> (student_db)")]:::db
    end

    S3(("☁️ AWS S3 Bucket <br> (Image Storage)")):::cloud
    
    subgraph "CI/CD Pipeline"
        GitHub["🐙 GitHub Repository"]:::devops
        Jenkins["🤖 Jenkins Build Server"]:::devops
        Sonar["🔍 SonarQube (Analysis)"]:::devops
        DockerHub["🐳 Docker Hub"]:::devops
    end

    %% Workflow / Connections
    User -- "Interacts" --> UI
    UI -- "JSON + JWT Bearer <br> (Fetch API)" --> Gateway
    Gateway --> App
    
    App -- "Validates & Routes" --> App
    App -- "Reads/Writes Entity Data" --> MySQL
    App -- "Uploads Student Photos <br> (aws-java-sdk)" --> S3
    S3 -. "Returns Public Image URL" .-> App
    
    %% CI/CD Workflow
    Developer(("👨‍💻 Developer")) -->|Git Push| GitHub
    GitHub -->|Triggers Build| Jenkins
    Jenkins -->|Code Scan| Sonar
    Jenkins -->|Builds Image| DockerHub
    Jenkins -->|SSH Deploy & <br> docker compose up| Gateway
```

---

## 🛠️ Technology Stack

| Domain | Technology / Framework | Usage |
| :--- | :--- | :--- |
| **Backend** | Java 17, Spring Boot 3 | API Routing, Data JPA, Security filters |
| **Security** | Spring Security, JWT tokens | Secures API endpoints against unauthorized access |
| **Frontend** | HTML5, Vanilla CSS, Vanilla JS | Responsive Glassmorphism UI, async Fetch API logic |
| **Database** | MySQL 8 | Relational data persistence for Users and Students |
| **Storage** | Amazon Web Services (AWS) S3 | Cloud Object Storage for student profile photos |
| **Containers** | Docker Engine, Docker Compose | Orchestrates the App and Database on the cloud host |
| **CI/CD** | Jenkins, Maven, Git, SonarQube | Automated Build, Test, Code Analysis, and Deployment |
| **Cloud Hosting** | AWS EC2 (Ubuntu 24.04) | Production environment hosting the docker containers |

---

## 📂 Project Structure

```text
Student-Management/
├── src/main/java/com/student/
│   ├── config/               # JWT Security Filters & AWS S3 Configuration
│   ├── controller/           # REST APIs & HTML Page Routing
│   ├── model/                # JPA Database Entities (User, Student)
│   ├── repository/           # Spring Data JPA Repositories
│   └── service/              # Core Business Logic & AWS S3 Object Uploads
├── src/main/resources/
│   ├── static/               # Vanilla JS (script.js) & CSS styling (style.css)
│   ├── templates/            # Dynamic HTML Views (login, signup, dashboard)
│   └── application.properties# Core Spring Boot environment variable mappings
├── Jenkinsfile               # Declarative Jenkins CI/CD Pipeline Configuration
├── docker-compose.yml        # Docker Compose service orchestration blueprint
├── Dockerfile                # Spring Boot App Container build instructions
└── pom.xml                   # Maven dependencies and build plugins
```

---

## 📜 API Reference

> **Base URL:** `http://54.85.195.87:8080`
> Protected endpoints require: `Authorization: Bearer <jwt_token>` header.

---

### 🔐 Authentication

#### Register a New User
```
POST /auth/register
Content-Type: application/json
```
**Request Body:**
```json
{
  "username": "admin",
  "password": "secret123",
  "role": "USER"
}
```
**Response (200 OK):**
```json
{
  "id": 1,
  "username": "admin",
  "role": "USER"
}
```

---

#### Login & Get JWT Token
```
POST /auth/login
Content-Type: application/json
```
**Request Body:**
```json
{
  "username": "admin",
  "password": "secret123"
}
```
**Response (200 OK):**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9.xxxx  (raw JWT string)
```

---

### 🎓 Student Management

> All student endpoints require `Authorization: Bearer <token>` header.

#### Get All Students
```
GET /students
Authorization: Bearer <token>
```
**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Raghavendra",
    "rollNumber": "CS101",
    "branch": "CSE",
    "college": "NIT",
    "skills": "Java, Spring Boot, Docker",
    "email": "raghu@example.com",
    "phone": "9876543210",
    "age": 21,
    "gender": "Male",
    "imageUrl": "https://s3.amazonaws.com/..."
  }
]
```

---

#### Get Student by ID
```
GET /students/{id}
Authorization: Bearer <token>
```
**Response (200 OK):** Single student object (same schema as above).

---

#### Add a New Student (with optional photo)
```
POST /students
Authorization: Bearer <token>
Content-Type: multipart/form-data
```
**Form Fields:**

| Field | Type | Required | Description |
| :--- | :--- | :--- | :--- |
| `student` | String (JSON) | ✅ Yes | Serialized student JSON |
| `file` | File | ❌ No | Profile image (jpg/png) |

**Example `student` JSON string:**
```json
{
  "name": "Raghavendra",
  "rollNumber": "CS101",
  "branch": "CSE",
  "college": "NIT",
  "skills": "Java, Docker",
  "email": "raghu@example.com",
  "phone": "9876543210",
  "age": 21,
  "gender": "Male"
}
```
**Response (200 OK):** Created student object with generated `id` and `imageUrl`.

---

#### Update an Existing Student
```
PUT /students/{id}
Authorization: Bearer <token>
Content-Type: multipart/form-data
```
**Form Fields:** Same as `POST /students`. Provide updated values.
**Response (200 OK):** Updated student object.

---

#### Delete a Student
```
DELETE /students/{id}
Authorization: Bearer <token>
```
**Response (200 OK):**
```
Student deleted successfully
```

---

#### Filter, Sort & Paginate Students
```
GET /students/filter
Authorization: Bearer <token>
```
**Query Parameters:**

| Parameter | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `page` | Integer | `0` | Page number (0-indexed) |
| `size` | Integer | `4` | Number of records per page |
| `sort` | String | `null` | Sort key: `ageAsc`, `ageDesc`, `name`, `gender`, `skills` |
| `skills` | String | `null` | Filter by skill |
| `gender` | String | `null` | Filter by gender |
| `age` | Integer | `null` | Filter by exact age |

**Example Request:**
```
GET /students/filter?page=0&size=4&sort=ageAsc&skills=Java
```
**Response (200 OK):**
```json
{
  "content": [...],
  "totalPages": 3,
  "totalElements": 11,
  "number": 0,
  "size": 4
}
```

---

### 🖥️ Page Routes

| Route | Description |
| :--- | :--- |
| `GET /login` | Renders the login HTML page |
| `GET /signup` | Renders the registration HTML page |
| `GET /dashboard` | Renders the main student management dashboard |
| `GET /test` | Health check — returns `"Backend is working 🚀"` |

---

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

## ⚙️ How It Works (The Request Lifecycle)

1. **Client Interaction:** The user interacts with the `dashboard.html` UI.
2. **Security:** The vanilla JS `script.js` attaches a JWT (`Authorization: Bearer <token>`) to HTTP requests via the Fetch API.
3. **Backend Processing:** The Spring Boot `@RestController` intercepts the requests. Security filters validate the JWT.
4. **Cloud Integration:** If a photo is attached (via `multipart/form-data`), the service dispatches it autonomously using the AWS SDK directly to an S3 Bucket and saves the public URL.
5. **Persistence:** Entity data is mapped and processed natively using Spring Data JPA, running transactions against the internal MySQL Database container.

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
    AWS_REGION=your_aws_region (e.g., us-east-1)
    AWS_S3_BUCKET=your_s3_bucket_name
    ```

    > [!TIP]
    > You can also create a `.env` file in the root directory of the project. The application is configured to automatically load variables from this file using the `dotenv-java` library.

3. **Start the Database & Application using Docker:**
   ```bash
   docker compose up -d
   ```

4. **Access the application:**
   - **Frontend UI:** [http://localhost:8080/signup](http://localhost:8080/signup)
   - **Backend API Testing / Health:** [http://localhost:8080/test](http://localhost:8080/test)

---

