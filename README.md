# Car Sharing

## Overview

The Car Sharing project is designed to modernize and streamline the operations of car sharing services. Currently, many systems for tracking cars, rentals, users, and payments in the car sharing services rely on manual record-keeping, cash payments, and lack real-time information about car availability and rental status. This project aims to solve these issues by implementing an online management system that benefits both users and administrators.

## Features

### 1. User Registration and Authentication

- **User Sign-Up**: Users can create accounts by providing their personal information.
- **User Login**: Registered users can log in securely to access the system.

### 2. Car Listing and Availability

- **Car Catalog**: Users can browse through the available cars, view details, and check their availability.
- **Real-Time Updates**: The system provides real-time information on car availability, preventing double bookings.

### 3. Rental and Payment

- **Car Rental**: Users can rent a car for a specific date and time.
- **Payment Integration**: The system supports secure credit card payments, making the process more convenient.
- **Rental History**: Users can view their rental history.

### 4. Notifications Service
- **Telegram**: Sends notifications for new rentals, overdue rentals, and successful payments using Telegram.

### 5. Administrative Tools

- **Car Management**: Administrators can add, update, or remove cars from the system.
- **User Management**: Administrators can manage user accounts and view rental histories.
- **Payment Tracking**: The system keeps a record of payments, allowing administrators to track payments and late returns.

## API Functionalities

### Authentication

1. **Register User** (`POST /register`): Allows user registration.
2. **Login** (`POST /login`): Provides JWT tokens upon successful login.

### User Management

3. **Update User Role** (`PUT /users/{id}/role`): Administrators can update user roles.
4. **Get User Profile** (`GET /users/me`): Users can retrieve their own profile.
5. **Update User Profile** (`PUT/PATCH /users/me`): Users can update their profiles.

### Car Management

6. **Add Car** (`POST /cars`): Administrators can add new cars.
7. **List Cars** (`GET /cars`): Retrieves available cars.
8. **Get Car Details** (`GET /cars/<id>`): Provides details for a specific car.
9. **Update Car Info** (`PUT/PATCH /cars/<id>`): Administrators can update car details.
10. **Delete Car** (`DELETE /cars/<id>`): Administrators can remove cars.

### Rental Management

11. **Rent a Car** (`POST /rentals`): Allows users to rent cars.
12. **List Rentals** (`GET /rentals/?user_id=...&is_active=...`): Retrieves rentals by user and status.
13. **Get Rental Details** (`GET /rentals/<id>`): Provides rental information.
14. **Return Rental** (`POST /rentals/<id>/return`): Allows users to return rentals.

### Payments (Stripe)

15. **List Payments** (`GET /payments/?user_id=...`): Retrieves payment information.
16. **Create Payment Session** (`POST /payments/`): Initiates payment sessions using Stripe.
17. **Check Successful Payments** (`GET /payments/success/`): Verifies successful Stripe payments.
18. **Return Payment Status** (`GET /payments/cancel/`): Returns payment status.

## Technology Stack

- **Docker**: The application is containerized for easy deployment.
- **Spring Boot**: The project is built using the Spring Boot framework for Java.
- **MySQL**: As the relational database management system.
- **Spring Data JPA**: For easy integration with the database.
- **Spring Security**: To handle user authentication and security.
- **MapStruct**: For mapping between Java objects.
- **Liquibase**: For database schema version control.
- **OpenAPI**(Swagger): For API documentation and testing.

## Installation

1. **Clone the Repository:**

   ```shell
   git clone <https://github.com/jv-jun23-team4/Car-sharing> cd BookStore-api

2. **Build with Maven:**

   ```shell
    mvn clean install

3. **Create Docker Image:**

   ```shell 
    docker build -t [your-image-name] .

4. **Docker Compose:**

   ```shell
   docker-compose build 
   docker-compose up

## Usage

1. Access the application in your web browser.
2. Register as a new user or log in with your credentials.
3. Browse cars, make rentals, and complete payments.
4. Administrators can log in and manage cars, users, and payments through the admin panel.
