# 📅 Calendar System - Event Management REST API

A modern Spring Boot REST API for managing calendar events with session-based authentication and PostgreSQL persistence. Built with Java 21 LTS.

## ✨ Features

### Core Functionality
- **Create Events** - Add new calendar events with custom details
- **Read Events** - Retrieve all events or fetch specific events by ID
- **Update Events** - Modify event titles, descriptions, times, and event types
- **Delete Events** - Remove events from the calendar
- **Session Management** - JDBC-based session storage for user persistence

### Technical Highlights
- ✅ RESTful API design with proper HTTP status codes
- ✅ CORS support for Angular frontend (`http://localhost:4200`)
- ✅ PostgreSQL database with automatic schema generation
- ✅ Jakarta EE / Hibernate JPA for ORM
- ✅ Built with Java 21 LTS

## 🛠️ Technology Stack

- **Java**: 21 LTS
- **Framework**: Spring Boot 3.5.3
- **Database**: PostgreSQL 15
- **Build Tool**: Maven
- **ORM**: Hibernate/Jakarta JPA
- **Session Storage**: Spring Session with JDBC

## 📋 Prerequisites

- **Java 21 LTS** (already configured in the project)
- **Docker** (for PostgreSQL container)
- **Maven** (included via Maven Wrapper)
- **Node.js** (for Angular frontend development)

## 🚀 Quick Start

### Step 1: Start PostgreSQL with Docker

Run the command to launch PostgreSQL:

```bash
docker run --name my-postgres \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=calendar \
  -p 5432:5432 \
  -d postgres:15
```

| Parameter | Description |
|-----------|-------------|
| `--name my-postgres` | Container name |
| `-e POSTGRES_USER=admin` | Database username |
| `-e POSTGRES_PASSWORD=admin` | Database password |
| `-e POSTGRES_DB=calendar` | Database name |
| `-p 5432:5432` | Port mapping (PostgreSQL default: 5432) |
| `-d` | Run in background (detached mode) |
| `postgres:15` | PostgreSQL 15 official image |

### Step 2: Verify Database Connection

Connect to PostgreSQL to verify it's running:

```bash
# Access PostgreSQL via Docker
docker exec -it my-postgres psql -U admin -d calendar

# Check if tables exist
SELECT * FROM event;
```

### Step 3: Build the Project

```bash
# Build with Maven (skips tests if database is not needed)
./mvnw clean package -DskipTests

# Or run with tests (requires database to be running)
./mvnw clean package
```

### Step 4: Run the Spring Boot Application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

## 📡 API Endpoints

For complete API documentation with detailed examples, request/response samples, and use cases, please see:

📖 **[API Documentation](./API.md)**

The API includes the following endpoints:

- `GET /events` - Retrieve all events
- `POST /events` - Create a new event
- `GET /events/{id}` - Get a specific event
- `PUT /events/{id}` - Update an event
- `DELETE /events?id={id}` - Delete an event

All endpoints support the Event object with fields: `id`, `createdBy`, `title`, `description`, `startTime`, `endTime`, and `event_type`.

## 🔐 CORS Configuration

The API is configured to accept requests from the Angular frontend running on `http://localhost:4200`.

### CORS Policy
- **Origin**: http://localhost:4200
- **Methods**: GET, POST, PUT, DELETE, OPTIONS
- **Credentials**: Supported (for session cookies)

### Troubleshooting CORS Errors (403)

If you encounter a 403 error when making requests:

1. **Verify CORS is configured** - The backend must send `Access-Control-Allow-Origin` header
2. **Preflight requests** - Browsers send OPTIONS requests before POST/PUT/DELETE
3. **Current Configuration** - Already implemented in `com.calendar.confug.CorsConfig`

The CORS configuration is automatically handled. If you need to modify allowed origins, update `CorsConfig.java`:

```java
corsRegistry.addMapping("/**")
    .allowedOrigins("http://localhost:4200")
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowCredentials(true);
```

## 🗄️ Database Schema

The application automatically generates the database schema through Hibernate.

### Event Table
```sql
CREATE TABLE event (
  id BIGSERIAL PRIMARY KEY,
  created_by VARCHAR(255),
  title VARCHAR(255),
  description TEXT,
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  event_type VARCHAR(255)
);
```

## 🔄 Project Structure

```
src/main/java/com/calendar/
├── CalendarApplication.java          # Spring Boot entry point
├── controller/
│   ├── EventController.java          # REST API endpoints
│   └── AuthController.java           # Authentication endpoints
├── service/
│   └── EventService.java             # Business logic
├── domain/
│   └── Event.java                    # JPA Entity
├── repository/
│   └── EventRepository.java          # JPA Repository
└── confug/
    └── CorsConfig.java               # CORS configuration
```

## 🔧 Configuration

Main configuration file: `src/main/resources/application.properties`

```properties
# Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/calendar
spring.datasource.username=admin
spring.datasource.password=admin
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Session Management
spring.session.jdbc.initialize-schema=always
spring.sql.init.mode=always
```

## 📚 Reference Documentation

- [Spring Boot Official Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Session](https://spring.io/projects/spring-session)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Jakarta EE Specification](https://jakarta.ee/)

## 📝 Notes

- The project uses Java 21 LTS for modern language features
- Automatic schema generation is enabled (`ddl-auto=update`)
- The `createdBy` field is required for event creation (represents logged-in user)
- Session data is persisted in PostgreSQL via Spring Session
- All timestamps use `LocalDateTime` for local timezone handling

## 🤝 Development

### Running Tests
```bash
./mvnw test
```

### Building Docker Image
```bash
./mvnw spring-boot:build-image
```

### Clean Build
```bash
./mvnw clean install
```

---

**Happy event scheduling!** 📅✨
