# Groundplay

A Kotlin Spring Boot application for managing sports games and player enrollments. The application allows players to create and join games, view nearby games, and manage their participation.

## Features

- Player registration and profile management
- Game creation and management
- Location-based game search
- Player enrollment in games
- Spatial data support (PostgreSQL with PostGIS)
- JWT-based authentication
- Role-based access control
- Request/response logging
- Exception handling with custom error responses

## Tech Stack

- Kotlin 1.9.25
- Spring Boot 3.4.3
- Spring Security with JWT authentication
- Spring Data JPA
- Hibernate Spatial
- PostgreSQL with PostGIS
- JTS Topology Suite
- JJWT (Java JWT Library)

## Getting Started

### Prerequisites

- JDK 21
- PostgreSQL with PostGIS extension
- Gradle

### Running the Application

```bash
# Clone the repository
git clone https://github.com/yourusername/groundplay.git
cd groundplay

# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

### Configuration

The application is configured via `src/main/resources/application.properties`. Make sure to set up your database connection properties:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/groundplay
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT Configuration
jwt.secret=your_jwt_secret_key
jwt.expiration=1296000000  # 15 days in milliseconds

# Encryption Configuration
encryption.secret=your_encryption_secret_key
```

### Environment Variables

For production, it's recommended to use environment variables instead of hardcoded values:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
encryption.secret=${ENCRYPTION_SECRET}
```
