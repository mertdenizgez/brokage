# Brokage Firm API

## Build and Run

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Build the Project

```bash
mvn clean compile
```

### Run Tests

```bash
mvn test
```

### Generate Test Coverage Report

```bash
mvn clean test
```

Coverage report will be available at `target/site/jacoco/index.html`

### Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access H2 Console

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:brokage`
- Username: `sa`
- Password: `password`

### Access API Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

The Swagger UI provides interactive API documentation where you can:

- View all available endpoints
- See request/response schemas
- Test API endpoints directly from the browser
- Authenticate using JWT tokens

## Default Users

The application initializes with the following users:

### Admin User

- Username: `admin`
- Password: `admin123`
- Role: ADMIN
- Can access all endpoints

### Customer Users

- Username: `customer1` / Password: `password123`
    - Initial TRY balance: 100,000
- Username: `customer2` / Password: `password123`
    - Initial TRY balance: 50,000

## Sample API Usage

You can test all these endpoints interactively using Swagger UI at `http://localhost:8080/swagger-ui.html`

### 1. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"customer1","password":"password123"}'
```

### 2. Create Buy Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "customerId": 1,
    "assetName": "AAPL",
    "orderSide": "BUY",
    "size": 10,
    "price": 150
  }'
```

### 3. List Orders

```bash
curl -X GET "http://localhost:8080/api/orders?customerId=1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Cancel Order

```bash
curl -X DELETE "http://localhost:8080/api/orders/1?customerId=1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Admin: Match Order

```bash
curl -X POST http://localhost:8080/api/admin/orders/1/match \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```
