# Java Web Application with REST API. Task Manager

## Overview
This is a web application that provides the user with a convenient and flexible way to create tasks, edit and delete them. There is PRO functionality here, which provides more extensive customization of tasks, as well as more detailed data that can be added to any task. The Java-based application provides a RESTful API with an emphasis on security. It implements user authentication and authorization using JWT tokens, access control based on user roles, and a relational database for persistent data storage. The application is designed with a clean architecture that separates tasks into repository, service, and controller layers, and utility classes for common functions.

## Features
- **REST API**: Offers endpoints for resource management, including CRUD operations.
- **Authentication and Authorization**: Implements JWT-based authentication and role-based access control to endpoints.
- **Relational Database Integration**: Utilizes a relational database to store and manage application data.
- **Comprehensive Test Coverage**: Includes unit and integration tests to ensure functionality and reliability. Application layers tested - services, controllers, managers.
- **Logging**: Utilizing Lombok Sl4f4 for detailed logging
- **Project Structure**:
  - `repository`: A data access layer for interacting with the database.
  - `service`: Contains business logic and data processing.
  - `util`: Utility classes and common functions.
  - `controller`: REST controllers for processing API requests.
  - `manager`: An application layer that hides all logic of controllers to achieve a higher level of abstraction.

## API Endpoints

Below is a list of available API endpoints with their respective HTTP methods, descriptions, required roles, and parameters.

| Endpoint                        | Method  | Description                                                     | Required Role | Parameters/Body                                         |
|---------------------------------|---------|-----------------------------------------------------------------|---------------|---------------------------------------------------------|
| `/v1/api/tasks/{taskId}`        | PUT     | Updates a task                                                  | Pro/User      | TaskStatusUpdateDto JSON object, `id`: Task/ProTask ID  |
| `/v1/api/tasks/{taskId}`        | DELETE  | Deletes a task                                                  | Pro/User      | `id`: Task/ProTask ID                                   |
| `/v1/api/tasks`                 | GET     | Retrieves all user`s tasks                                      | Pro/User      | None                                                    |
| `/v1/api/tasks`                 | POST    | Creates a new task                                              | Pro/User      | Object taskDto JSON object                              |
| `/v1/api/tasks/expired`         | GET     | Retrieves all expired tasks                                     | Pro           | None                                                    |
| `/v1/api/tasks/deadline-soon`   | GET     | Retrieves all tasks at the deadline                             | Pro           | None                                                    |
| `/v1/api/tasks/by-time`         | GET     | Retrieves all tasks at the time of creation                     | Pro           | None                                                    |
| `/v1/api/admin/users/{userId}`  | PUT     | Updates an existing user                                        | Admin         | User JSON object, `id`: User ID                         |
| `/v1/api/admin/users/{userId}`  | DELETE  | Deletes a user                                                  | Admin         | `id`: User ID                                           |
| `/v1/api/admin/users`           | GET     | Retrieves all users                                             | Admin         | None                                                    |
| `/v1/api/admin/users`           | POST    | Creates a new user                                              | Admin         | User JSON object                                        |
| `/v1/api/admin/users/statistics`| GET     | Retrieves users statistics                                      | Admin         | None                                                    |
| `/auth/login`                   | POST    | Authenticates user and returns JWT                              | None          | JwtRequest JSON object                                  |
| `/`                             | GET     | Retrieves "Hello everyone! Welcome to our Mega Tasks Manager!"  | None          | None                                                    |


### Parameters and Body Details
*need to use Swagger for this*

## Getting Started

### Prerequisites
- JDK 11 or later
- Maven
- A relational database (preferably PostgreSQL, there are migrations in project which use postgreSQL dialect)

### Setup
1. Clone the repository to your local machine.
2. Configure the database connection in `src/main/resources/application.yml`.
3. Build the project using Maven: `mvn clean install`.
4. Run the application: `java -jar target/final-project.jar`.

## Usage
After starting the application, you can interact with the API using tools like Postman or Curl. Below are some example requests:

### Authenticate
```bash
curl -X POST http://localhost:8080/auth/login -d "username=user1&password=password"
