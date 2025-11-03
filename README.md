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

## Deployment

The application is deployed and running on **Azure App Service**:

**Live Production URL:** [https://amazinbooks-htg4fcgthzfwcphm.canadacentral-01.azurewebsites.net/](https://amazinbooks-htg4fcgthzfwcphm.canadacentral-01.azurewebsites.net/)

Continuous deployment is configured via GitHub Actions. Every merge to `main` triggers an automated build, test, and deployment pipeline.

---

## Database Schema

The application uses **MongoDB** as its NoSQL database with the following collections:

### Collection: `books`
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `_id` | String | Primary Key, Auto-generated | MongoDB ObjectId |
| `title` | String | Required, Not Blank | Book title |
| `author` | String | Required, Not Blank | Author name |
| `publisher` | String | Required, Not Blank | Publisher name |
| `isbn` | String | Required, Not Blank | ISBN identifier |
| `price` | BigDecimal | Required, Positive | Book price |
| `genre` | String | Optional | Book genre/category |
| `publicationYear` | Integer | Optional | Year of publication |
| `description` | String | Optional | Book description |
| `stockQuantity` | Integer | Min: 0 | Available inventory count |
| `pictureUrl` | String | Optional | URL to book cover image |

### Collection: `users`
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `_id` | String | Primary Key, Auto-generated | MongoDB ObjectId |
| `username` | String | Required, Unique, Not Blank | User login name |
| `firstName` | String | Required, Not Blank | User's first name |
| `lastName` | String | Required, Not Blank | User's last name |
| `email` | String | Required, Unique, Valid Email | User email address |
| `password` | String | Required, Write-only | Hashed password (BCrypt) |
| `role` | Enum | Default: CUSTOMER | User role (CUSTOMER or OWNER) |

**UML Class Diagram:** See [UML_Class_Diagram.png](./UML_Class_Diagram.png) for visual representation of domain models.

---

## Next Sprint Plan (Milestone 2 - Alpha Release)

**Target Date:** November 17, 2025

### Objectives
For Milestone 2, we aim to create a functional alpha release where users can complete an end-to-end shopping experience. This includes browsing books, adding items to a cart, and completing checkout.

### Planned Features

#### Shopping Cart Functionality
- **Issue #16:** Implement Shopping Cart API (Backend)
  - Create shopping cart entity and repository
  - REST endpoints for add/remove/update cart items
  - Session-based or user-based cart persistence

- **Issue #21:** Create Shopping Cart Page (Frontend)
  - Display cart items with quantities and prices
  - Update item quantities
  - Remove items from cart
  - Show cart total

#### Checkout Flow
- **Issue #17:** Implement Checkout Simulation (Backend)
  - Process cart items and calculate totals
  - Validate inventory availability
  - Update stock quantities on purchase
  - Create order confirmation logic

- **Issue #22:** Create Checkout Page (Frontend)
  - Order summary display
  - Customer information form
  - Payment simulation interface
  - Order confirmation page

#### Inventory Management
- **Issue #14:** Implement Inventory Validation
  - Stock quantity checks during add-to-cart
  - Prevent overselling
  - Low stock warnings on product pages

#### Book Recommendations
- **Issue #18:** Backend - Recommendations
  - Recommendation algorithm (genre-based or popularity-based)
  - REST endpoint for fetching recommendations

- **Issue #24:** Create Simple Recommendation Display
  - Show recommended books on homepage or product pages
  - "Customers also viewed" section

#### UI Enhancements
- **UI portion of book filtering** - Improve filter interface and user experience

#### Testing & Quality
- **Issue #26:** Integration Tests for Checkout Flow
  - End-to-end tests for shopping cart
  - Checkout process validation
  - Inventory update verification

### Weekly Scrums
- **Issue #3:** Weekly Scrum - November 6, 2025
- **Issue #4:** Weekly Scrum - November 13, 2025

---

## License

This project was developed for SYSC 4806 – Software Engineering Lab at Carleton University.