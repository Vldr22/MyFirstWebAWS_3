# S3FileManager

REST API для управления файлами в Yandex Object Storage (S3) с публичным доступом к чтению и ролевым контролем загрузки. Реализованы коммерческие практики: JWT + Redis для аутентификации, Docker с multi-profile конфигурацией, Flyway migrations, Swagger документация.

## Tech Stack

- **Backend:** Java 17, Spring Boot 3.3.5 (Web, Security, Data JPA, Validation)
- **Database:** PostgreSQL 15 + Flyway migrations
- **Cache & Sessions:** Redis (JWT token whitelist)
- **Storage:** Yandex Object Storage (S3-compatible)
- **Infrastructure:** Docker, Docker Compose
- **Monitoring:** Spring Boot Actuator
- **Documentation:** Swagger/OpenAPI 3.0

## Key Features

- **Public access:** Просмотр и скачивание файлов без регистрации
- **Role-based upload:** USER загружает 1 файл, ADMIN - неограниченно с правом удаления
- **JWT authentication:** HttpOnly cookies + Redis whitelist для токенов
- **Duplicate prevention:** SHA-256 хеширование предотвращает дубликаты файлов
- **Multi-profile config:** Отдельные настройки для dev/prod окружений
- **API pagination:** Постраничный вывод списка файлов

## Quick Start

### With Docker (рекомендуется)

```bash
# Создайте .env файл с переменными (см. ниже)
cp .env.example .env

# Запустите все сервисы
docker-compose up -d

# Приложение доступно на http://localhost:8080
```
## Environment Variables

| Variable            | Description                    | Example           |
|---------------------|--------------------------------|-------------------|
| `POSTGRES_DB`       | Database name                  | `s3filemanager`   |
| `POSTGRES_USER`     | Database user                  | `dbuser`          |
| `POSTGRES_PASSWORD` | Database password              | `securepass`      |
| `JWT_SECRET_KEY`    | JWT signing key (min 256 bits) | `your-secret-key` |
| `ACCESS_KEY`        | Yandex Storage access key      | `YCAxxxxx`        |
| `SECRET_KEY`        | Yandex Storage secret key      | `YCMxxxxx`        |
| `ADMIN_NAME`        | Default admin username         | `admin`           |
| `ADMIN_PASSWORD`    | Default admin password         | `admin123`        |

## API Documentation
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`

## User Roles

- **Anonymous:** Просмотр и скачивание файлов
- **ROLE_USER:** Загрузка 1 файла (после загрузки → ROLE_USER_ADDED)
- **ROLE_USER_ADDED:** Просмотр и скачивание (загрузка заблокирована)
- **ROLE_ADMIN:** Полный доступ (загрузка множества файлов, удаление)