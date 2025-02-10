# Java Spring Boot Backend

## Overview
This project is a backend service developed using **Spring Boot**, **Maven**, and **MySQL**. It provides a set of RESTful APIs for managing application data and business logic. The project is structured following best practices for maintainability and scalability.

## Features
- **User authentication and authorization** (JWT-based security)
- **CRUD operations** for managing entities
- **Database integration** with MySQL using JPA/Hibernate
- **Exception handling** for better error management
- **Logging and monitoring** using Spring Boot Actuator
- **API documentation** with Swagger
- **Environment configuration** using `application.properties`
- **Unit and integration testing** with JUnit and Mockito

## Installation
### Prerequisites
Before running the project, ensure you have the following installed:
- **Java 17+**
- **Maven 3+**
- **MySQL Database**

### Setup Instructions
1. **Clone the repository:**
   ```sh
   git clone <repository-url>
   ```
2. **Navigate to the project folder:**
   ```sh
   cd team-2
   ```
3. **Configure the database:**
   - Open `src/main/resources/application.properties`
   - Update the following properties with your MySQL credentials:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```
4. **Build the project:**
   ```sh
   mvn clean install
   ```
5. **Run the application:**
   ```sh
   mvn spring-boot:run
   ```

## API Documentation
After starting the application, you can access the API documentation via Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Testing
To run unit and integration tests, execute:
```sh
mvn test
```

## Deployment
### Docker Support
If you want to containerize the application, you can use the provided `Dockerfile`. Steps to build and run using Docker:
1. **Build the Docker image:**
   ```sh
   docker build -t spring-boot-app .
   ```
2. **Run the container:**
   ```sh
   docker run -p 8080:8080 spring-boot-app
   ```

## License
This project is for educational purposes only and is not intended for production use. If you wish to modify or use it in a commercial environment, please ensure compliance with applicable licenses and regulations.

