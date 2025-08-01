# Book Management – Spring Boot REST Application

## Table of Contents
- [Project Overview](#project-overview)
- [Technologies Used](#technologies-used)
- [Data Models & Relationships](#data-models--relationships)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Swagger Documentation](#swagger-documentation)
- [Postman Collection](#postman-collection)
- [Contact](#contact)

---

## Project Overview

**Book Management** is a stateless RESTful application with JWT-based authentication and user registration.  
It provides a platform for both **administrators** and **customers** to manage books, categories, orders, and shopping carts.

The application is built following **SOLID principles** and adopts a **Three-Tier Architecture**:
1. **Presentation Layer** – Controllers for handling client requests
2. **Business Logic Layer** – Services implementing application logic
3. **Data Access Layer** – Repositories for communication with the database

---

## Technologies Used

- **Java** `17`
- **Spring Boot** `3.4.2`
    - Spring Web
    - Spring Security
    - Spring Data JPA
- **Hibernate** `6.5.3.Final`
- **MySQL** `8.0.33`
- **Lombok**
- **MapStruct**
- **Liquibase**
- **Swagger (SpringDoc)**
- **Docker & Docker Compose**
- **JWT (JJWT library)**

---

## Data Models & Relationships

![book-management.png](images/book-management.png)]

---

## Project Structure

```
src/
├── config/           → Application configuration
├── controller/       → REST API endpoints
├── dto/              → Data Transfer Objects
├── exception/        → Custom exceptions
├── mapper/           → MapStruct mappers for model ↔ DTO
├── model/            → Entity classes
├── repository/       → JPA repositories
├── security/         → JWT-based authentication & authorization
├── service/          → Business logic
└── validation/       → Custom validation annotations
```

### Key Controllers

#### `AuthenticationController`
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/auth/registration` | Register a new user | Public |
| POST | `/api/auth/login` | Authenticate and get JWT | Public |

#### `BookController`
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/books` | Add a new book | Admin |
| PUT | `/api/books/{id}` | Update book details | Admin |
| GET | `/api/books` | Get all books | Admin/User |
| GET | `/api/books/{id}` | Get book by ID | Admin/User |
| DELETE | `/api/books/{id}` | Delete book | Admin |
| GET | `/api/books/search` | Search books | Admin/User |

#### `CategoryController`
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/categories` | Add new category | Admin |
| PUT | `/api/categories` | Update category | Admin |
| GET | `/api/categories` | Get all categories | Admin/User |
| GET | `/api/categories/{id}` | Get category by ID | Admin/User |
| DELETE | `/api/categories/{id}` | Delete category | Admin |
| GET | `/api/categories/{id}/books` | Books by category | Admin/User |

#### `OrderController`
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/orders` | Create new order | User |
| GET | `/api/orders` | Get user's order history | User |
| PATCH | `/api/orders/{id}` | Update order status | Admin |
| GET | `/api/orders/{orderId}/items` | Get order items | User |
| GET | `/api/orders/{orderId}/items/{itemId}` | Get specific order item | User |

#### `ShoppingCartController`
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/cart` | Get shopping cart | User |
| POST | `/api/cart` | Add/update book in cart | User |
| PUT | `/api/cart/cart-items/{cartItemId}` | Update book quantity | User |
| DELETE | `/api/cart/cart-items/{cartItemId}` | Remove book from cart | User |

---

## Getting Started

### Prerequisites

Ensure you have the following installed:
- Java 17+
- Maven
- Docker & Docker Compose

### Steps

1. **Clone the repository:**
```bash
git clone https://github.com/Oleksii21th/book-management-system.git
cd book-management
```

2. **Create a `.env` file** in the project root using `.env-sample` as a reference.

3. **Build the project:**
```bash
mvn clean package
```

4. **Start the app using Docker Compose:**
```bash
docker-compose build
docker-compose up
```

5. Open your browser and go to:  
   `http://localhost:8088`

---

## Swagger Documentation

After launching the application, visit the Swagger UI for API reference:  
[http://localhost:8088/api/swagger-ui/index.html#/](http://localhost:8088/api/swagger-ui/index.html#/)

---

## Postman Collection
https://www.postman.com/redbul-7208203/my-workspace
A Postman collection is available to streamline API testing.

> To test as a regular user:
> - Register with any email/password.
> - Use `/api/auth/login` to get a JWT token.

> To test as an admin, use:
> ```
> Email: admin@gmail.com
> Password: password
> ```

---

## Contact

**Developer**: Oleksii Babych
**Email**:   
**GitHub**: https://github.com/Oleksii21th/book-management-system