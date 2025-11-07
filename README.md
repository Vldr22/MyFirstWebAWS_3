# FirstWebProject

Educational web application demonstrating commercial Spring Boot development practices with modern tech stack and production-ready configuration.

## 🛠 Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.3.5 (Web, Security, Data JPA, Validation)
- PostgreSQL 15 + Flyway migrations
- Redis (JWT token storage)
- Yandex Object Storage (S3-compatible)

**Infrastructure:**
- Docker & Docker Compose
- ELK Stack (Elasticsearch, Logstash, Kibana) for centralized logging
- Spring Boot Actuator for monitoring

**Security & API:**
- JWT authentication with HttpOnly cookies
- Role-based access control (ADMIN, USER)
- Swagger/OpenAPI 3.0 documentation

## ✨ Key Features

- 🔐 JWT-based authentication with Redis whitelist
- 📁 Secure file upload/download with Yandex Object Storage
- 📊 Pagination and filtering for API endpoints
- 🔄 Database versioning with Flyway
- 🐳 Multi-profile configuration (dev/prod)
- 📝 Structured JSON logging for ELK Stack
- 🔍 Health checks and monitoring endpoints

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Docker & Docker Compose
- `.env` file with required variables (see below)

### Run with Docker

```bash
# Clone and navigate to project
git clone <repository-url>
cd FirstWebProject

# Create .env file with your credentials
cp .env.example .env

# Start all services
docker-compose up -d

# Application will be available at http://localhost:8080
```

### Run locally (development)

```bash
# Start PostgreSQL and Redis
docker-compose up -d postgres redis

# Run application with dev profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🔐 Environment Variables

Create `.env` file in project root:

```env
# Database
POSTGRES_DB=firstWebProject
POSTGRES_USER=your_user
POSTGRES_PASSWORD=your_password

# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# JWT
JWT_SECRET_KEY=your_secret_key_min_256_bits

# Admin credentials
ADMIN_NAME=admin
ADMIN_PASSWORD=admin_password

# Yandex Object Storage
ACCESS_KEY=your_yandex_access_key
SECRET_KEY=your_yandex_secret_key
```

## 📚 API Documentation

Swagger UI available at: `http://localhost:8080/swagger-ui.html`

API docs (OpenAPI 3.0): `http://localhost:8080/v3/api-docs`

## 🏗 Project Structure

```
src/main/
├── java/
│   └── org/education/firstwebproject/
│       ├── config/          # Configuration classes
│       ├── controller/      # REST controllers
│       ├── dto/             # Data Transfer Objects
│       ├── entity/          # JPA entities
│       ├── exception/       # Exception handling
│       ├── repository/      # Spring Data repositories
│       ├── security/        # Security filters & configs
│       └── service/         # Business logic
└── resources/
    ├── db/migration/        # Flyway SQL migrations
    ├── application.yml      # Base configuration
    ├── application-dev.yml  # Development profile
    └── application-prod.yml # Production profile
```

## 🔧 Configuration Profiles

- **dev** - Local development (verbose logging, show SQL)
- **prod** - Production (minimal logging, optimized for Docker)

Activate profile: `SPRING_PROFILES_ACTIVE=dev`

## 🏥 Health & Monitoring

- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`
- Info: `http://localhost:8080/actuator/info`

## 📝 License

Educational project for portfolio purposes.