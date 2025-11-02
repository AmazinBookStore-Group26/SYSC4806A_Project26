# Amazin Bookstore

Amazin Bookstore is a web application built with **Spring Boot** and **MongoDB**.  
It provides a secure online bookstore platform where customers can browse books, search and filter by author or genre, and where store owners can manage the catalog through an administrative interface.

---

## Features

### Public
- View all available books with details such as author, genre, and price.
- Search by title, author, publisher, or genre.
- Sort results by title, author, year, or price (ascending or descending).
- User registration and authentication with Spring Security.

### Admin
- Role-based access control: only users with the OWNER role can access the admin panel.
- Add, edit, and delete books through a REST API connected to MongoDB.
- Automatic low-stock highlighting to help manage inventory.

---

## Architecture

- Presentation Layer: `index.html`, `admin.html`, `login.html`, `register.html`, `edit-book.html`
  User interface built with Thymeleaf and secured via Spring Security
- Controller:  `BookController`, `AuthController`, `UserController`, `ViewController`
  Handles web requests and bridges between UI, services, and data layer
- Service: `BookService`, `UserService`, `CustomUserDetailsService`
  Encapsulates business logic and validation
- Persistence: `Book`, `User`
  MongoDB domain models
- Security: `SecurityConfig`
  Configures authentication, authorization, and password encoding

---

## Technologies Used

- **Java 17**
- **Spring Boot 3.2**
- **Spring Data MongoDB**
- **Spring Security**
- **Thymeleaf**
- **JUnit 5**
- **Maven**

---

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.9+
- MongoDB running locally on port 27017

### Clone and Build
```bash
git clone https://github.com/<your-username>/amazin-bookstore.git
cd amazin-bookstore
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

The server starts at:  
**http://localhost:8080**

---

## Default Routes
- `/`: GET, Home page displaying books
- `/login`: GET, Login form
- `/register`: GET, Registration form
- `/admin`: GET, Admin panel (OWNER only)
- `/api/books`: REST,  CRUD endpoints for books
- `/api/users`: REST, User management API

---

## Configuration

Application properties (`src/main/resources/application.properties`):

```
spring.data.mongodb.uri=mongodb://localhost:27017/amazin_bookstore
spring.thymeleaf.cache=false
spring.main.allow-bean-definition-overriding=true
```

---

## Testing

Run unit tests with:
```bash
mvn test
```

Test classes include:
- `BookServiceTest.java` - verifies filtering and sorting logic
- `BookControllerTest.java` - validates REST endpoints and error handling
- `AmazinBookstoreApplicationTests.java` - basic context load check

---

## License

This project was developed for SYSC 4806 – Software Engineering Lab at Carleton University.