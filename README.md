# Task Manager

Full-stack task management application with a Spring Boot REST API and React frontend.

## Tech Stack

### Backend

- Java
- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- Spring Data JPA
- Swagger / OpenAPI
- Maven
- Docker

### Frontend

- JavaScript
- React
- Vite

## Features

### Backend

- User registration and login
- JWT authentication
- Role-based access control
- Current user endpoints
- Admin endpoints
- Task management
- Swagger API documentation

### Frontend

- User login
- JWT authentication
- View user tasks
- Create tasks
- Delete tasks
- Logout

## Project Structure

    Task-Manager/
    ├── src/              # Spring Boot backend
    ├── frontend/         # React frontend
    ├── pom.xml
    ├── Dockerfile
    ├── docker-compose.yml
    └── README.md

## API Documentation

Swagger UI is available at:

    http://localhost:8080/swagger-ui/index.html

## Running the Frontend

Go to the frontend directory:

    cd frontend

Install dependencies:

    npm install

Start the development server:

    npm run dev

The frontend is available at:

    http://localhost:5173
