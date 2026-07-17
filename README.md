# Task Manager API

REST API for task management with JWT authentication and role-based authorization.

Task Manager API is a RESTful backend application built with Spring Boot.

The project allows users to:

- register and authenticate using JWT;
- manage their own tasks;
- access administrator functionality based on user roles;
- explore and test the API using Swagger UI.

## Tech Stack

- Java 17
- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- Spring Data JPA
- Swagger / OpenAPI
- Maven
- Docker
- Docker Compose

## Features

- User registration and login
- JWT authentication
- Role-based access control
- Current user endpoints
- Admin endpoints
- Task management
- Swagger API documentation
- Dockerized application with PostgreSQL

## Running with Docker

Build and start the application:

```bash
docker compose up --build
```

The application will be available at:

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
