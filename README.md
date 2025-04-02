# Groundplay

A Kotlin Spring Boot application for managing sports games and player enrollments. The application allows players to create and join games, view nearby games, and manage their participation.

## Features

- Player registration and profile management
- Game creation and management
- Location-based game search
- Player enrollment in games
- Spatial data support (PostgreSQL with PostGIS)

## Tech Stack

- Kotlin 1.9.25
- Spring Boot 3.4.3
- Spring Data JPA
- Hibernate Spatial
- PostgreSQL with PostGIS
- JTS Topology Suite

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
spring.datasource.url=jdbc:postgresql://localhost:5432/groundplay
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## API Endpoints

### Player API

- `POST /v1/player` - Create a new player
- `PATCH /v1/updatePlayerName/{id}` - Update player details
- `GET /v1/{id}/players` - Get all players enrolled in a game

### Game API

- `POST /v1/game` - Create a new game
- `GET /v1/games/nearby?lat={lat}&lon={lon}&radiusKm={radius}` - Find games near a location
- `GET /v1/gameDetails/{id}` - Get game details
- `PATCH /v1/updateGame/{id}` - Update game details

### Game Member API

#### Legacy Endpoints
- `POST /v1/register` - Enroll or unenroll a player in a game based on status field

#### New RESTful Endpoints
- `POST /v1/games/{gameId}/enroll` - Enroll a player in a game
  ```json
  {
    "playerId": 123
  }
  ```
- `PUT /v1/games/{gameId}/unenroll/{playerId}` - Unenroll a player from a game

## Request/Response Examples

### Enrolling a Player

**Request:**
```http
POST /v1/games/5/enroll/10
```

**Response:**
```json
{
  "id": 42,
  "gameId": 5,
  "playerId": 10,
  "status": true
}
```

### Unenrolling a Player

**Request:**
```http
POST /v1/games/5/unenroll/10
```

**Response:**
```json
{
  "id": 42,
  "gameId": 5,
  "playerId": 10,
  "status": false
}
```

### Finding Nearby Games

**Request:**
```http
GET /v1/games/nearby?lat=37.7749&lon=-122.4194&radiusKm=10
```

**Response:**
```json
[
  {
    "id": 5,
    "sport": "SOCCER",
    "location": "Golden Gate Park",
    "startTime": "2025-03-15T14:00:00",
    "endTime": "2025-03-15T16:00:00",
    "description": "Casual soccer game",
    "teamSize": 10,
    "enrolledPlayers": 6,
    "status": true,
    "organizer": 1,
    "coordinates": {
      "x": -122.4194,
      "y": 37.7749
    }
  }
]
```
