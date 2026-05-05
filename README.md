# Task Manager

REST API for managing users and tasks.

##  Features

* CRUD operations for Users
* CRUD operations for Tasks
* Get tasks by user
* Update task status
* One-to-many relationship (User → Tasks)
* DTO layer for secure and controlled API responses

---

##  Tech Stack

* Java 17
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Maven

---

##  API Endpoints

###  Users

* `GET /users` – get all users
* `GET /users/{id}` – get user by id
* `POST /users` – create user
* `DELETE /users/{id}` – delete user
* `GET /users/{userId}/tasks` – get tasks for a specific user

---

###  Tasks

* `GET /tasks` – get all tasks
* `GET /tasks/{id}` – get task by id
* `POST /tasks` – create task
* `DELETE /tasks/{id}` – delete task
* `PUT /tasks/{id}/status` – update task status

---

##  Example Request

### Create Task

```json
{
  "title": "Test Task",
  "description": "Test description",
  "status": "WARTET",
  "userId": 1
}
```

---

##  How to Run

1. Install and run PostgreSQL
2. Create a database (e.g. `task_manager`)
3. Configure `application.properties`
4. Run the application:

```bash
mvn spring-boot:run
```

---

##  Security

* Sensitive data like passwords are not exposed in API responses
* DTOs (`TaskResponse`, `UserResponse`) are used to control output

---

##  In Progress

* Authentication (JWT)
* Role-based access (USER / ADMIN)
* Frontend (UI)

---


