# Library Book Catalog API

Spring Boot RESTful API managing a library book catalog with H2 in-memory database.

## Tech Stack
- Java 17, Spring Boot 3
- Spring Web, Spring Data JPA, Validation
- H2 in-memory DB
- JUnit 5, Mockito, MockMvc

## Build & Run
```
mvn spring-boot:run
```
H2 console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:lmsdb`)

## Entity Fields
Book: id, title (required), author (required), isbn (unique), publishedDate (LocalDate), status (AVAILABLE|BORROWED)

## Endpoints
| Method | URI | Description | Status Codes |
|--------|-----|-------------|--------------|
| POST | /api/v1/books | Create book | 201,400,409 |
| GET | /api/v1/books | List books (filters: author, status) | 200 |
| GET | /api/v1/books/{id} | Get by id | 200,404 |
| PUT | /api/v1/books/{id} | Update (full/partial) | 200,404,409 |
| DELETE | /api/v1/books/{id} | Delete | 204,404 |
| GET | /api/v1/books/published-after?date=YYYY-MM-DD | Books published after date | 200 |

## Sample cURL
```
# Create
curl -X POST http://localhost:8080/api/v1/books -H 'Content-Type: application/json' -d '{"title":"Clean Architecture","author":"Robert C. Martin","isbn":"9780134494166","publishedDate":"2017-09-20"}'

# List filtered
curl 'http://localhost:8080/api/v1/books?author=Joshua%20Bloch&status=AVAILABLE'

# Get one
curl http://localhost:8080/api/v1/books/1

# Update
curl -X PUT http://localhost:8080/api/v1/books/1 -H 'Content-Type: application/json' -d '{"status":"BORROWED"}'

# Delete
curl -X DELETE http://localhost:8080/api/v1/books/5

# Published after
curl 'http://localhost:8080/api/v1/books/published-after?date=2020-01-01'
```

## Validation & Errors
- 400: validation failures (title/author blank)
- 404: resource not found
- 409: duplicate ISBN
- 500: unhandled

## Architecture
- Controller -> Service (interface + impl) -> Repository
- DTOs isolate persistence model from API
- Java Streams used for filtering (author/status & published-after)
- Global exception handler standardizes responses

## Tests
- Service tests: creation, duplicates, find, update partial, stream filter, delete not found
- Controller tests: happy paths, validation error, not found, filters, published-after

## Seed Data
Five books inserted at startup via CommandLineRunner (DataSeeder).

## Notes
- Partial updates supported via PUT
- H2 data resets each restart.

