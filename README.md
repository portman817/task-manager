# Task Manager

A full-stack task management application with a Spring Boot REST API, React frontend, JWT authentication, and role-based access control.

The application allows users to manage their personal tasks and administrators to manage users, tasks, and task assignments.

## Tech Stack

### Backend

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

### Frontend

- JavaScript
- React
- React Router
- Vite
- CSS
- Fetch API

## Features

### Backend

- User registration and login
- JWT-based authentication
- Role-based access control (USER and ADMIN)
- Current user management
- Administrative user management
- Task CRUD operations
- Task ownership management
- Task reassignment between users
- Partial task updates (PATCH)
- Request validation
- Swagger / OpenAPI documentation

### Frontend

#### Authentication

- User registration
- User login
- JWT token management
- Session restoration
- Logout
- Role-based navigation
- Protected admin route

#### User Panel

- View personal tasks
- Create tasks
- Edit tasks
- Delete tasks
- Update task status
- Update username
- Automatic task list refresh when returning to the user panel

#### Admin Panel

- View all registered users
- Create users
- Edit user information
- Change user passwords
- Delete users
- View tasks assigned to individual users
- Create tasks for users
- Edit user tasks
- Delete user tasks
- Reassign tasks to other users

## Project Structure

    Task-Manager/
    ├── src/                  # Spring Boot backend
    ├── frontend/             # React frontend
    │   ├── src/
    │   │   ├── api/          # API request functions
    │   │   ├── components/   # Reusable React components
    │   │   ├── constants/    # Application constants
    │   │   ├── pages/        # Application pages
    │   │   └── App.jsx       # Main application component
    │   └── package.json
    ├── pom.xml
    ├── Dockerfile
    ├── docker-compose.yml
    └── README.md

## Getting Started

### Prerequisites

- Docker
- Docker Compose
- Node.js
- npm
- Java 17 and Maven (for local backend development)

### Running the Backend

From the project root, build and start the Docker containers:

    docker compose up -d --build

Check the container status:

    docker compose ps

The backend is available at:

    http://localhost:8080

### Running the Frontend

Go to the frontend directory:

    cd frontend

Install dependencies:

    npm install

Start the development server:

    npm run dev

The frontend is available at:

    http://localhost:5173

## API Documentation

The REST API is documented using Swagger / OpenAPI.

Swagger UI is available at:

    http://localhost:8080/swagger-ui/index.html

Protected endpoints require a valid JWT token.

## User Roles

| Feature | USER | ADMIN |
|---|---|---|
| Manage personal tasks | Yes | Yes |
| Update personal profile | Yes | Yes |
| Access admin panel | No | Yes |
| Manage users | No | Yes |
| Manage other users' tasks | No | Yes |
| Reassign tasks | No | Yes |

## Development Status

The core backend and frontend functionality has been implemented.

The project is currently entering the refactoring and optimization phase.

## Roadmap

- [x] Backend REST API
- [x] JWT authentication and authorization
- [x] User registration and login
- [x] User task management
- [x] Admin panel
- [x] Administrative user management
- [x] Administrative task management
- [x] Task reassignment
- [ ] API layer refactoring and standardization
- [ ] React component refactoring
- [ ] Improved error handling
- [ ] Loading states and user feedback
- [ ] UI/UX improvements
- [ ] Final integration testing

## Project Purpose

This project was created as a full-stack learning project focused on practical experience with REST API development, authentication, authorization, database integration, React, and frontend-backend communication.
