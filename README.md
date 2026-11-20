# GitHub Repository Searcher

Spring Boot REST API that searches GitHub repositories, stores them in PostgreSQL (upsert by GitHub repo id) and lets you filter the stored data.

**Live URL:** https://github-repo-searcher-pame.onrender.com
**Swagger:** https://github-repo-searcher-pame.onrender.com/swagger-ui.html
(Free hosting sleeps when idle. The first request may take ~50 seconds.)

## Tech Stack
Java 21, Spring Boot 4, Spring Data JPA, PostgreSQL (Neon), springdoc-openapi (Swagger), Docker, Render

## APIs
**POST /api/github/search**
```json
{ "query": "spring boot", "language": "Java", "sort": "stars" }
```
`sort` is one of: stars, forks, updated. Fetches from GitHub, saves or updates in the DB, and returns the repositories.

**GET /api/github/repositories?language=Java&minStars=100&sort=stars**
All params are optional. `sort` defaults to stars. Reads only from the DB.

## Architecture
Controller -> Service -> Repository -> PostgreSQL
GithubApiClient (interface and implementation) calls the GitHub API.
DTOs separate API shapes from the entity. A global exception handler returns consistent errors.

## Error Handling
- 400: validation errors or invalid sort
- 429: GitHub rate limit
- 422/502/503: invalid query or GitHub unavailable
- Empty results return an empty list with a message

## Run Locally
1. Install Java 21 and PostgreSQL. Create a database named `github_search`.
2. Set your DB password:
   `$env:DB_PASSWORD="your_password"` (PowerShell)
3. Run: `./mvnw spring-boot:run`
4. Open http://localhost:8090/swagger-ui.html

