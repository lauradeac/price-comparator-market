# price-comparator-market

## Project Structure Overview

The project uses a layered architecture for clarity and maintainability:

- **controller/** – Contains the REST API endpoints that handle incoming HTTP requests.
- **service/** – Implements the business logic of the application.
- **repository/** – Provides access to the database using Spring Data JPA.
- **domain/entity/** – Holds the JPA entities mapped to database tables.
- **domain/dto/** – Contains Data Transfer Objects used to exchange data between layers.
- **utils/** – Utility classes including file data extraction and scheduled price alert tasks.
- **resources/** – Contains application configuration files like `application.yml` and a folder that holds the all csv files given as sample data.

## How to Build and Run the Application

### Prerequisites
- Java 17 or higher installed
- Maven installed
- MySQL database set up and running
- (Optional) Postman or any REST client for testing APIs

### Configuration
Edit `src/main/resources/application.yml` and update the database connection properties and any 
other required settings.

### Build the Project
```bash
mvn clean install
```

### Run the Application
```bash
mvn spring-boot:run
```

## How to Use the Implemented Features

Interact with the REST API endpoints to use the application's features. 
Below are example requests for functionalities.
First, ensure that the application is running and the database is populated with sample data from the CSV files.

## API Endpoints for Importing Data and Using Features
### Import Products and Discounts from CSV Files

The application provides endpoints to import product and discount data from CSV files into the database using the OpenCSV library.

#### 1. Import Products

**Endpoint:**  
`GET /api/import/products`

**Description:**  
Reads all product CSV files and saves the data to the database.

**Example Request:**
```bash
curl "http://localhost:8080/api/import/products"
``` 

#### 2. Import Discounts
**Endpoint:**  
`GET /api/import/products`

**Description:**  
Reads all product discounts CSV files and saves the data to the database.

**Example Request:**
```bash
curl "http://localhost:8080/api/import/discounts"
``` 

### 0. Add a New User (Required for Basket Optimization(Task 1) and Alerts(Task 6))

**Endpoint:**  
`POST /api/users/register`

**Example JSON Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "yourPassword"
}
```
This will create a new user in the database, which is required before using features like adding products to 
a basket or creating price alerts.

### 1. Add Products to User Basket

**Endpoint:**  
`POST /api/users/add-products/{userId}`

This endpoint allows you to add a specific number of products to a user's basket,
which will be randomly fetched from the database. The user must be registered first. 

### 2. Optimize User Basket

**Endpoint:**  
`GET /api/users/optimize-basket/{userId}`

**Description:**  
Optimizes the shopping basket for the specified user and groups the products by supermarket.

**Example Request:**
```bash
curl "http://localhost:8080/api/users/optimize-basket/1"
```

### 3. Set Price Alert

**Endpoint:**  
`POST /api/users/set-alert`

**Description:**  
Creates a price alert for a user and product.

**Example JSON Body:**
```json
{
  "userId": 1,
  "productName": "lapte zuzu",
  "targetPrice": 9.0
}
```

### 4. Get Best Discounts

**Endpoint:**  
`GET /api/products/best-discounts`

**Description:**  
Retrieves a list of products with the best available discounts.

**Example Request:**
```bash
curl "http://localhost:8080/api/products/best-discounts"
``` 

### 5. Get New Discounts

**Endpoint:**
`GET /api/products/new-discounts`

**Description:**
Retrieves a list of products with newly available discounts(withing 24 hours).

**Example Request:**
```bash
curl "http://localhost:8080/api/products/new-discounts"
```

### 6. Get Product Price History

**Endpoint:**  
`GET /api/products/price-history`

**Description:**  
Retrieves the price history of products filtered by store, category, brand, and a date range.

**Query Parameters:**
- `store` (optional): Name of the store (e.g., "kaufland")
- `category` (optional): Product category
- `brand` (optional): Product brand
- `startDate` (required): Start date in `YYYY-MM-DD` format
- `endDate` (required): End date in `YYYY-MM-DD` format

**Example Request:**
```bash
curl "http://localhost:8080/api/products/price-history?store=kaufland&startDate=2025-05-01&endDate=2025-05-20"
```

### 7. Get Product Recommendations

**Endpoint:**  
`GET /api/products/recommendations`

**Description:**  
Retrieves product recommendations by product name and a specified date range.

**Query Parameters:**
- `productName` (required): Name of the product (e.g., "iaurt grecesc")
- `fromDate` (required): Start date in `YYYY-MM-DD` format
- `toDate` (required): End date in `YYYY-MM-DD` format

**Example Request:**
```bash
curl "http://localhost:8080/api/products/recommendations?productName=iaurt%20grecesc&fromDate=2025-05-01&toDate=2025-05-15"
```
