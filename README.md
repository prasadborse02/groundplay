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
- `POST /v1/updatePlayerName/{id}` - Update player name
- `GET /v1/{id}/players/{status}` - Get players by game ID and status

### Game API

- `POST /v1/game` - Create a new game
- `GET /v1/games/nearby?lat={lat}&lon={lon}&radiusKm={radius}` - Find games near a location
- `GET /v1/gameDetails/{id}` - Get game details
- `POST /v1/updateGame/{id}` - Update game details

### Game Member API

- `POST /v1/games/{gameId}/enroll/{playerId}` - Enroll a player in a game
- `POST /v1/games/{gameId}/unenroll/{playerId}` - Unenroll a player from a game

## Request/Response Examples

### Player Registration

**Request:**
```http
POST /v1/player
Content-Type: application/json

{
  "name": "John Doe",
  "phoneNumber": "+1234567890"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "John Doe",
  "phoneNumber": "+1234567890"
}
```

### Update Player Name

**Request:**
```http
POST /v1/updatePlayerName/1
Content-Type: application/json

{
  "name": "John Smith"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "John Smith",
  "phoneNumber": "+1234567890"
}
```

### Creating a Game

**Request:**
```http
POST /v1/game
Content-Type: application/json

{
  "sport": "CRICKET",
  "location": "Central Park",
  "startTime": "2025-04-01T14:00:00",
  "endTime": "2025-04-01T16:00:00",
  "description": "Friendly cricket match",
  "teamSize": 11,
  "status": true,
  "organizer": 1,
  "coordinates": {
    "lat": 40.785091,
    "lon": -73.968285
  }
}
```

**Response:**
```json
{
  "id": 1,
  "sport": "CRICKET",
  "location": "Central Park",
  "startTime": "2025-04-01T14:00:00",
  "endTime": "2025-04-01T16:00:00",
  "description": "Friendly cricket match",
  "teamSize": 11,
  "enrolledPlayers": 0,
  "status": true,
  "organizer": 1,
  "coordinates": {
    "lat": 40.785091,
    "lon": -73.968285
  }
}
```

### Updating a Game

**Request:**
```http
POST /v1/updateGame/1
Content-Type: application/json

{
  "sport": "CRICKET",
  "location": "Riverside Park",
  "startTime": "2025-04-01T15:00:00",
  "endTime": "2025-04-01T17:00:00",
  "description": "Friendly cricket match - updated venue",
  "teamSize": 11,
  "status": true,
  "organizer": 1,
  "coordinates": {
    "lat": 40.801979,
    "lon": -73.972070
  }
}
```

**Response:**
```json
{
  "id": 1,
  "sport": "CRICKET",
  "location": "Riverside Park",
  "startTime": "2025-04-01T15:00:00",
  "endTime": "2025-04-01T17:00:00",
  "description": "Friendly cricket match - updated venue",
  "teamSize": 11,
  "enrolledPlayers": 0,
  "status": true,
  "organizer": 1,
  "coordinates": {
    "lat": 40.801979,
    "lon": -73.972070
  }
}
```

### Get Game Details

**Request:**
```http
GET /v1/gameDetails/1
```

**Response:**
```json
{
  "id": 1,
  "sport": "CRICKET",
  "location": "Riverside Park",
  "startTime": "2025-04-01T15:00:00",
  "endTime": "2025-04-01T17:00:00",
  "description": "Friendly cricket match - updated venue",
  "teamSize": 11,
  "enrolledPlayers": 0,
  "status": true,
  "organizer": 1,
  "coordinates": {
    "lat": 40.801979,
    "lon": -73.972070
  }
}
```

### Enrolling a Player

**Request:**
```http
POST /v1/games/1/enroll/1
```

**Response:**
```json
{
  "id": 1,
  "gameId": 1,
  "playerId": 1,
  "status": true
}
```

### Unenrolling a Player

**Request:**
```http
POST /v1/games/1/unenroll/1
```

**Response:**
```json
{
  "id": 1,
  "gameId": 1,
  "playerId": 1,
  "status": false
}
```

### Getting Players by Game ID and Status

**Request:**
```http
GET /v1/1/players/true
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "John Smith",
    "phoneNumber": "+1234567890"
  },
  {
    "id": 2,
    "name": "Jane Doe",
    "phoneNumber": "+9876543210"
  }
]
```

### Finding Nearby Games

**Request:**
```http
GET /v1/games/nearby?lat=40.7128&lon=-74.0060&radiusKm=5
```

**Response:**
```json
[
  {
    "id": 1,
    "sport": "CRICKET",
    "location": "Riverside Park",
    "startTime": "2025-04-01T15:00:00",
    "endTime": "2025-04-01T17:00:00",
    "description": "Friendly cricket match - updated venue",
    "teamSize": 11,
    "enrolledPlayers": 1,
    "status": true,
    "organizer": 1,
    "coordinates": {
      "lat": 40.801979,
      "lon": -73.972070
    }
  },
  {
    "id": 2,
    "sport": "FOOTBALL",
    "location": "Battery Park",
    "startTime": "2025-04-02T18:00:00",
    "endTime": "2025-04-02T20:00:00",
    "description": "After-work football game",
    "teamSize": 7,
    "enrolledPlayers": 4,
    "status": true,
    "organizer": 2,
    "coordinates": {
      "lat": 40.703137,
      "lon": -74.016262
    }
  }
]
```