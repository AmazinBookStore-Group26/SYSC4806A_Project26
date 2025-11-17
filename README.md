# Amazin Bookstore

Amazin Bookstore is a web application built with **Spring Boot** and **MongoDB**.  
It provides a secure online bookstore platform where customers can browse books, search and filter by author or genre, and where store owners can manage the catalog through an administrative interface.

---

## Features

### Customer Features
- **Browse & Search Books**: View all available books with details such as author, genre, price, and inventory.
- **Advanced Filtering**: Search by title, author, publisher, or genre with real-time results.
- **Sort Options**: Sort results by title, author, publication year, or price (ascending or descending).
- **User Authentication**: Registration and login with Spring Security and password encryption.
- **Book Details Page**: View comprehensive book information including description, ISBN, and availability.
- **Shopping Cart**: Add books to cart, update quantities, and remove items with real-time total calculation.
- **Checkout Simulation**: Complete purchases with simulated payment form (card details, billing address).
- **Inventory Validation**: Real-time stock availability checks prevent adding out-of-stock items.

### Admin Features
- **Role-Based Access Control**: Only users with OWNER role can access the admin panel.
- **Book Management**: Add, edit, and delete books through admin interface.
- **Inventory Management**: Update stock quantities with automatic low-stock highlighting.
- **Real-time Updates**: All changes reflect immediately in the database and UI.

---

## Architecture

- **Presentation Layer**: `index.html`, `admin.html`, `login.html`, `register.html`, `edit-book.html`, `book-details.html`, `cart.html`
  - User interface built with Thymeleaf and secured via Spring Security
  - Interactive checkout modal with payment form simulation
- **Controllers**: `BookController`, `ShoppingCartController`, `AuthController`, `UserController`, `ViewController`
  - Handles web requests and bridges between UI, services, and data layer
  - RESTful API endpoints for books, cart operations, and user management
- **Services**: `BookService`, `ShoppingCartService`, `UserService`, `CustomUserDetailsService`
  - Encapsulates business logic and validation
  - Cart management and checkout processing
- **Persistence**: `Book`, `User`, `ShoppingCart`, `CartItem`
  - MongoDB domain models
  - Embedded documents for cart items
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

### Web Pages
- `/`: Home page displaying books with search and filter
- `/book/{id}`: Book details page with add-to-cart functionality
- `/cart`: Shopping cart page (authenticated users only)
- `/login`: Login form
- `/register`: Registration form
- `/admin`: Admin panel (OWNER role only)
- `/admin/book/edit/{id}`: Edit book page (OWNER role only)

### REST API Endpoints
- `/api/books`: GET (all), POST (create), PUT (update), DELETE (remove)
- `/api/books/{id}`: GET book by ID
- `/api/cart/{userId}`: GET user's cart
- `/api/cart/{userId}/items`: POST (add item), PUT (update quantity), DELETE (remove item)
- `/api/users`: User management endpoints

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
- `BookServiceTest.java` - verifies filtering and sorting logic with 13 test cases
- `BookControllerTest.java` - validates REST endpoints and error handling with 9 test cases
- `UserServiceTest.java` - tests user CRUD operations and validation with 16 test cases
- `AmazinBookstoreApplicationTest.java` - basic context load check

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

#### Enhanced User Experience
- **Book Details Page**: Comprehensive view with description, ISBN, price, and availability
- **Responsive Design**: Mobile-friendly checkout modal and cart interface
- **Error Handling**: User-friendly alerts for failed operations

#### Code Quality
- **Comprehensive Testing**: 38+ unit tests with full Javadoc documentation
- **Test Coverage**: All service and controller methods tested
- **Documentation**: Complete Javadoc for all test classes and methods

---

## Next Sprint Plan (Milestone 3 - Final Release)

**Target Date:** December 1, 2025

### Objectives
For Milestone 3, we aim to complete the application with order management, recommendations, and polish the user experience to create a production-ready bookstore platform.

### Planned Features

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

#### Book Recommendations
- **Recommendation Algorithm**: Implement intelligent book suggestions
  - Genre-based recommendations
  - "Customers also viewed" feature
  - Popular books section
  - REST endpoint for recommendations

- **Recommendation UI**: Display recommended books
  - Homepage recommendations section
  - Related books on product pages
  - Personalized suggestions based on browsing history

#### Advanced Search & Filtering
- **Enhanced UI**: Improve search and filter interface
  - Filter by multiple criteria simultaneously
  - Price range slider
  - Availability filter (in stock only)
  - Better mobile experience

#### Integration Testing
- **End-to-End Tests**: Complete checkout flow testing
  - Shopping cart to order completion
  - Inventory update verification
  - Multi-user scenarios
  - Error handling and edge cases

#### UI Polish & Bug Fixes
- **Visual Improvements**: Refine user interface
  - Consistent styling across all pages
  - Loading indicators for async operations
  - Success/error message improvements
  - Remove any dangling links

- **Bug Fixes**: Address any remaining issues
  - Cross-browser compatibility
  - Mobile responsiveness
  - Performance optimizations

### Weekly Scrums
- Weekly Scrum - November 24, 2025
- Weekly Scrum - December 1, 2025 (Final Sprint Review)

---

## License

This project was developed for SYSC 4806 – Software Engineering Lab at Carleton University.