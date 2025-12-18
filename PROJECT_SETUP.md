# Project Setup — Calendar System

This document contains setup, build, and run instructions for the Calendar REST API project.

## Prerequisites

- Java 21 LTS
- Docker (for PostgreSQL container)
- Maven (you can use the included Maven Wrapper)
- Node.js (for the Angular frontend, if used)

## Quick Start

### 1) Start PostgreSQL with Docker

Run:

```bash
docker run --name my-postgres \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=calendar \
  -p 5432:5432 \
  -d postgres:15
```

### 2) Verify Database Connection

```bash
# Access PostgreSQL via Docker
docker exec -it my-postgres psql -U admin -d calendar

# Check if tables exist
SELECT * FROM event;
```

### 3) Build the Project

```bash
# Use the Maven Wrapper
./mvnw clean package -DskipTests

# Or run with tests
./mvnw clean package
```

### 4) Run the Application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

## API Endpoints

See [API.md](./API.md) for complete documentation.

Common endpoints:

- `GET /events` — retrieve all events
- `POST /events` — create an event
- `GET /events/{id}` — get a specific event
- `PUT /events/{id}` — update an event
- `DELETE /events?id={id}` — delete an event

Event fields: `id`, `createdBy`, `title`, `description`, `startTime`, `endTime`, `event_type`.

## CORS Configuration

The backend allows requests from `http://localhost:4200`. See `com.calendar.confug.CorsConfig` to modify origins.

Example:

```java
corsRegistry.addMapping("/**")
    .allowedOrigins("http://localhost:4200")
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowCredentials(true);
```

## Database Schema

The schema is generated automatically by Hibernate. Example `event` table:

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

## Project Structure

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

## Configuration (`application.properties`)

```properties
# Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/calendar
spring.datasource.username=admin
spring.datasource.password=admin
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Session management
spring.session.jdbc.initialize-schema=always
spring.sql.init.mode=always
```

## Development

Run tests:

```bash
./mvnw test
```

Build Docker image:

```bash
./mvnw spring-boot:build-image
```

Clean build:

```bash
./mvnw clean install
```

## References

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Session](https://spring.io/projects/spring-session)
- [PostgreSQL](https://www.postgresql.org/docs/)

---

Happy developing! 📅
