# Amazin Bookstore

Amazin Bookstore is a web application built with **Spring Boot** and **MongoDB**.
It provides a secure online bookstore platform where customers can browse books, search and filter by author or genre, and where store owners can manage the catalog through an administrative interface.

---

## Features

### Customer Features
- **Browse & Search Books**: View all available books with details such as author, genre, price, and inventory.
- **Pagination**: Browse books with 10 items per page, with navigation controls.
- **Advanced Filtering**: Search by title, author, publisher, or genre with real-time results.
- **Sort Options**: Sort results by title, author, publication year, or price (ascending or descending).
- **User Authentication**: Registration and login with Spring Security and password encryption.
- **Book Details Page**: View comprehensive book information including description, ISBN, publication year, and availability with stock badges.
- **Quick Add to Cart**: Add books directly from the browse page with one click.
- **Shopping Cart**: Add books to cart, update quantities, and remove items with real-time total calculation.
- **Checkout Simulation**: Complete purchases with simulated payment form (card details, billing address).
- **Inventory Validation**: Real-time stock availability checks prevent adding out-of-stock items.
- **Order History**: View all past orders with details and status tracking.
- **Book Recommendations**: Personalized recommendations using Jaccard similarity algorithm, with fallback to popular books.

### Admin Features
- **Role-Based Access Control**: Only users with OWNER role can access the admin panel.
- **Book Management**: Add, edit, and delete books through admin interface.
- **Inventory Management**: Update stock quantities with automatic low-stock highlighting.
- **Real-time Updates**: All changes reflect immediately in the database and UI.

---

## Architecture

- **Presentation Layer**: `index.html`, `admin.html`, `login.html`, `register.html`, `edit-book.html`, `book-details.html`, `cart.html`, `orders.html`, `recommendations.html`
  - User interface built with Thymeleaf and secured via Spring Security
  - Interactive checkout modal with payment form simulation
  - Responsive full-width design with pagination support
- **Controllers**: `BookController`, `ShoppingCartController`, `AuthController`, `UserController`, `ViewController`, `OrderController`, `RecommendationController`
  - Handles web requests and bridges between UI, services, and data layer
  - RESTful API endpoints for books, cart operations, orders, and recommendations
- **Services**: `BookService`, `ShoppingCartService`, `UserService`, `CustomUserDetailsService`, `OrderService`, `RecommendationService`
  - Encapsulates business logic and validation
  - Cart management and checkout processing
  - Recommendation algorithm using Jaccard similarity
- **DTOs**: `RecommendationResponse`
  - Data transfer objects for API responses
- **Persistence**: `Book`, `User`, `ShoppingCart`, `CartItem`, `Order`, `OrderItem`
  - MongoDB domain models
  - Embedded documents for cart and order items
- **Security**: `SecurityConfig`
  - Configures authentication, authorization, and password encoding
  - Role-based access control (CUSTOMER, OWNER)

---

## Technologies Used

- **Java 17**
- **Spring Boot 3.2**
- **Spring Data MongoDB**
- **Spring Security**
- **Thymeleaf**
- **JUnit 5 & Mockito**
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

## Web Pages

| Route | Description | Access |
|-------|-------------|--------|
| `/` | Home page - browse books with search, filter, sort, and pagination | Public |
| `/book/{id}` | Book details page with full information and add-to-cart | Public |
| `/cart` | Shopping cart with quantity management and checkout | Authenticated |
| `/orders` | Order history showing past purchases | Authenticated |
| `/recommendations` | Personalized book recommendations | Authenticated |
| `/login` | Login form | Public |
| `/register` | Registration form | Public |
| `/admin` | Admin panel for book management | OWNER role |
| `/admin/book/edit/{id}` | Edit book form | OWNER role |

---

## REST API Endpoints

### Books API (`/api/books`)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `GET` | `/api/books` | Get all books with optional filters | - | `List<Book>` |
| `GET` | `/api/books?author={author}` | Filter by author | - | `List<Book>` |
| `GET` | `/api/books?publisher={publisher}` | Filter by publisher | - | `List<Book>` |
| `GET` | `/api/books?genre={genre}` | Filter by genre | - | `List<Book>` |
| `GET` | `/api/books?title={title}` | Search by title | - | `List<Book>` |
| `GET` | `/api/books?sortBy={field}` | Sort by field (price, title, author, year) | - | `List<Book>` |
| `GET` | `/api/books/{id}` | Get book by ID | - | `Book` |
| `POST` | `/api/books` | Create new book | `Book` JSON | `Book` |
| `PUT` | `/api/books/{id}` | Update existing book | `Book` JSON | `Book` |
| `DELETE` | `/api/books/{id}` | Delete book | - | `204 No Content` |

### Shopping Cart API (`/api/cart`)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `GET` | `/api/cart/{userId}` | Get user's cart | - | `ShoppingCart` |
| `POST` | `/api/cart/{userId}/items` | Add item to cart | `{bookId, quantity}` | `ShoppingCart` |
| `PUT` | `/api/cart/{userId}/items/{bookId}?quantity={qty}` | Update item quantity | - | `ShoppingCart` |
| `DELETE` | `/api/cart/{userId}/items/{bookId}` | Remove item from cart | - | `ShoppingCart` |
| `DELETE` | `/api/cart/{userId}` | Clear entire cart | - | `204 No Content` |

### Orders API (`/api/orders`)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `POST` | `/api/orders/checkout/{userId}` | Create order from cart | - | `Order` |
| `GET` | `/api/orders/{orderId}` | Get order by ID | - | `Order` |
| `GET` | `/api/orders/user/{userId}` | Get user's orders | - | `List<Order>` |
| `GET` | `/api/orders` | Get all orders | - | `List<Order>` |
| `PATCH` | `/api/orders/{orderId}/status?status={status}` | Update order status | - | `Order` |

### Users API (`/api/users`)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `GET` | `/api/users` | Get all users | - | `List<User>` |
| `GET` | `/api/users/{id}` | Get user by ID | - | `User` |
| `POST` | `/api/users` | Create new user | `User` JSON | `User` |
| `PUT` | `/api/users/{id}` | Update user | `User` JSON | `User` |
| `DELETE` | `/api/users/{id}` | Delete user | - | `204 No Content` |

### Recommendations API (`/api/recommendations`)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `GET` | `/api/recommendations/{userId}?limit={n}` | Get personalized recommendations | - | `RecommendationResponse` |

**RecommendationResponse Structure:**
```json
{
  "books": [Book],
  "fallback": boolean,
  "message": "Based on your reading history" | "We couldn't find readers with similar taste, here are some popular books"
}
```

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

### Test Summary

| Test Class | Tests | Description |
|------------|-------|-------------|
| `BookServiceTest` | 13 | Book CRUD, filtering, sorting, pagination |
| `BookControllerTest` | 10 | REST endpoints, error handling |
| `UserServiceTest` | 18 | User CRUD, validation, role management |
| `ShoppingCartServiceTest` | 5 | Cart operations, item management |
| `ShoppingCartControllerTest` | 4 | Cart API endpoints |
| `OrderServiceTest` | 14 | Order creation, checkout, status updates |
| `OrderControllerTest` | 16 | Order API endpoints |
| `RecommendationServiceTest` | 12 | Jaccard similarity, fallback logic, edge cases |
| `RecommendationControllerTest` | 6 | Recommendation API endpoints |
| `AmazinBookstoreApplicationTest` | 2 | Context loading |
| `AmazinBookstoreApplicationTests` | 1 | Spring Boot test |
| **Total** | **101** | |

All tests include comprehensive Javadoc documentation explaining test objectives and expected outcomes.

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
| `inventory` | Integer | Min: 0 | Available inventory count |
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
| `purchasedBookIds` | Array[String] | Optional | List of purchased book IDs |

### Collection: `shopping_carts`
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `_id` | String | Primary Key, Auto-generated | MongoDB ObjectId |
| `userId` | String | Required, Not Blank | Reference to user |
| `items` | Array[CartItem] | Optional | List of cart items |

#### Embedded Document: `CartItem`
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `bookId` | String | Required | Reference to book |
| `quantity` | Integer | Required, Min: 1 | Number of copies |

### Collection: `orders`
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `_id` | String | Primary Key, Auto-generated | MongoDB ObjectId |
| `userId` | String | Required | Reference to user |
| `items` | Array[OrderItem] | Required | List of order items |
| `totalAmount` | Double | Required | Total order amount |
| `orderDate` | LocalDateTime | Required | Timestamp of order creation |
| `status` | Enum | Required | Order status (PENDING, CONFIRMED, COMPLETED, CANCELLED) |

#### Embedded Document: `OrderItem`
| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `bookId` | String | Required | Reference to book |
| `bookTitle` | String | Required | Book title at time of purchase |
| `quantity` | Integer | Required | Number of copies |
| `priceAtPurchase` | BigDecimal | Required | Price at time of purchase |

**UML Class Diagram:** See [UML_Class_Diagram.png](./UML_Class_Diagram.png) for visual representation of domain models.

---

## Milestone 2 - Alpha Release Status

**Completed:** November 17, 2025

### Implemented Features

#### Shopping Cart System
- **Backend**: Full shopping cart API with add/remove/update operations
- **Frontend**: Interactive cart page with quantity updates and item removal
- **Persistence**: User-based cart storage in MongoDB
- **Real-time Totals**: Automatic calculation of cart subtotals and grand total

#### Checkout Simulation
- **Payment Form Modal**: Interactive checkout modal with credit card simulation
- **Form Validation**: Client-side validation for card number, expiry, CVV, and billing address
- **Auto-formatting**: Card number (spaces every 4 digits) and expiry date (MM/YY) formatting
- **Backend Integration**: Connected to `/api/orders/checkout/` endpoint
- **Authentication Check**: Only logged-in users can access checkout

#### Inventory Management
- **Field Standardization**: Fixed `stockQuantity` → `inventory` naming across entire codebase
- **Stock Validation**: Real-time inventory checks on book details page
- **Low Stock Warnings**: Visual indicators for items with < 5 units
- **Out of Stock Handling**: Disabled add-to-cart for unavailable items

#### Order Management System
- **Order Backend**: Complete order processing and persistence
    - Create Order entity and repository
    - Process checkout with inventory deduction
    - Generate unique order IDs
    - Store order history in database

- **Order History Page**: Display user's past orders
    - View all previous purchases
    - Order details with items and totals
    - Order status tracking
    - Reorder functionality

#### Enhanced User Experience
- **Book Details Page**: Comprehensive view with description, ISBN, price, and availability
- **Responsive Design**: Mobile-friendly checkout modal and cart interface
- **Error Handling**: User-friendly alerts for failed operations

#### Code Quality
- **Comprehensive Testing**: 38+ unit tests with full Javadoc documentation
- **Test Coverage**: All service and controller methods tested
- **Documentation**: Complete Javadoc for all test classes and methods

---

## Milestone 3 - Final Release Status

**Completed:** December 1, 2025

### Implemented Features

#### Book Recommendations
- **Jaccard Similarity Algorithm**: Finds users with similar purchase history
- **Personalized Recommendations**: Suggests books from similar users that you haven't read
- **Fallback to Popular**: When no similar users found, shows most purchased books
- **UI Messaging**: Displays whether recommendations are personalized or popular books
- **REST API**: `/api/recommendations/{userId}` endpoint with limit parameter

#### Pagination
- **Browse Page Pagination**: 10 books per page with navigation controls
- **Results Info**: Shows "Showing X-Y of Z books"
- **Page Navigation**: Previous/Next buttons and page number links
- **Filter Preservation**: Search and sort options preserved across pages

#### Quick Add to Cart
- **One-Click Add**: Add books directly from browse page
- **Toast Notifications**: Success/error feedback without page reload
- **Login Prompt**: Unauthenticated users see "Login to Buy" button

#### Enhanced Book Details Page
- **Stock Badges**: Visual indicators (In Stock / Low Stock / Out of Stock)
- **Details Grid**: Publisher, ISBN, Genre, Publication Year, Availability
- **Description Section**: "About this Book" with formatted text
- **Quantity Selector**: +/- buttons for easy quantity adjustment
- **Large Add to Cart Button**: Prominent call-to-action

#### Full-Width UI
- **Modern Layout**: All pages span full browser width
- **Responsive Design**: Adapts to mobile and desktop screens
- **Consistent Styling**: Unified design across all pages

#### Code Quality
- **101 Unit Tests**: Comprehensive test coverage
- **Service Tests**: BookService, UserService, OrderService, RecommendationService, ShoppingCartService
- **Controller Tests**: BookController, OrderController, RecommendationController, ShoppingCartController
- **Edge Case Coverage**: Deleted books, empty carts, no similar users

---

## Project Structure

```
src/
├── main/
│   ├── java/org/amazinbookstore/
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── BookController.java
│   │   │   ├── OrderController.java
│   │   │   ├── RecommendationController.java
│   │   │   ├── ShoppingCartController.java
│   │   │   ├── UserController.java
│   │   │   └── ViewController.java
│   │   ├── dto/
│   │   │   └── RecommendationResponse.java
│   │   ├── exception/
│   │   │   └── ResourceNotFoundException.java
│   │   ├── model/
│   │   │   ├── Book.java
│   │   │   ├── CartItem.java
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   ├── ShoppingCart.java
│   │   │   └── User.java
│   │   ├── repository/
│   │   │   ├── BookRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   ├── ShoppingCartRepository.java
│   │   │   └── UserRepository.java
│   │   ├── security/
│   │   │   └── SecurityConfig.java
│   │   ├── service/
│   │   │   ├── BookService.java
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── OrderService.java
│   │   │   ├── RecommendationService.java
│   │   │   ├── ShoppingCartService.java
│   │   │   └── UserService.java
│   │   └── validation/
│   │       ├── PasswordValidator.java
│   │       └── ValidPassword.java
│   └── resources/
│       ├── static/
│       │   ├── css/style.css
│       │   └── js/script.js
│       ├── templates/
│       │   ├── admin.html
│       │   ├── book-details.html
│       │   ├── cart.html
│       │   ├── edit-book.html
│       │   ├── index.html
│       │   ├── login.html
│       │   ├── orders.html
│       │   ├── recommendations.html
│       │   └── register.html
│       └── application.properties
└── test/
    └── java/org/amazinbookstore/
        ├── controller/
        │   ├── BookControllerTest.java
        │   ├── OrderControllerTest.java
        │   ├── RecommendationControllerTest.java
        │   └── ShoppingCartControllerTest.java
        └── service/
            ├── BookServiceTest.java
            ├── OrderServiceTest.java
            ├── RecommendationServiceTest.java
            ├── ShoppingCartServiceTest.java
            └── UserServiceTest.java
```

---

## License

This project was developed for SYSC 4806 – Software Engineering Lab at Carleton University.