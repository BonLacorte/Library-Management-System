# 🚀 Setup Guide: Costa LMS Server

This is the backend core of the **Costa Library Management System**, built with Java 21, Spring Boot 3, and PostgreSQL. It handles authentication, circulation logic, and automated fine processing.

---

## 🐳 Quick Start with Docker (Recommended)

The server is already containerized. Use this method for a fast, consistent setup.

### Prerequisites
- **Docker Desktop** installed and running.
- **PostgreSQL** (can be running in Docker or locally).

### Steps

#### 1. Build the Application
Before containerizing, you must build the JAR file:
```bash
./mvnw clean package -DskipTests
```

#### 2. Build the Docker Image
```bash
docker build -t costa-lms-server .
```

#### 3. Run the Container
```bash
docker run -p 8080:8080 --name costa-lms-server costa-lms-server
```

---

## 🔧 Manual Local Development Setup

### Prerequisites
- **Java JDK**: v21.
- **Maven**: (Included via `./mvnw`).
- **PostgreSQL**: v15 or higher.

### Steps

#### 1. Database Configuration
1. Create a database named `library_db` in PostgreSQL.
2. Update `src/main/resources/application.yaml` with your credentials:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/library_db
    username: postgres
    password: your_password
```

#### 2. Configure External Services
The system uses **Razorpay** for payments and **SMTP** for emails. Update these in `application.yaml`:
- **Razorpay**: Keys are pre-configured for testing but should be replaced in production.
- **SMTP**: Configure your mail host and credentials for notifications.

#### 3. Run the Application
```bash
./mvnw spring-boot:run
```
The API server will start on [http://localhost:8080](http://localhost:8080).

---

## 🛠 Features
- **Stateless Security**: JWT-based authentication and role-based access control.
- **Automated Tasks**: Midnight cron jobs for overdue calculation and notification.
- **Billing Engine**: Integrated with Razorpay for fine and subscription processing.
- **Clean Architecture**: Decoupled modules for Catalog, Circulation, and Billing.

---

## 👨‍💻 API Documentation
By default, the server runs on port `8080`. The Next.js frontend is configured to communicate with the base path `/api`.
- **Auth Endpoint**: `/api/auth/login`
- **Book Stats**: `/api/books/stats`
- **User Statistics**: `/api/users/statistics`
