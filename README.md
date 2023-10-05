<div align="center"> <h1 > <img src=logo.jpg width="200" align="center" alt="50"/></h1></div>

# <div align="center">Car Sharing Service</div>
___

#### Welcome to our Car Sharing Service!

#### Our Car Sharing Service application is powered by Spring Boot, offering a convenient and efficient way to share vehicles. Whether you need a car for a quick errand or a road trip, our service is designed to make your experience smooth and hassle-free.

#### In this README, you'll find detailed information about our project, its features, and how to get started with car sharing. Explore the exciting world of shared mobility and enjoy the freedom of driving without the responsibilities of ownership. 
#### Happy car sharing!
___

## Content
- [👨‍💻Project Overview](#Overview)
- [🌟Features](#features)
- [‍📝Controllers](#controllers)
- [🛠️Installation](#installation)
- [⚙️Usage](#usage)
- [🏋️Challenges](#challenges)
- [🎯Summary](#summary)


<hr>
<div id="Overview" align="center">
  <h2 >👨‍💻 Project Overview</h2>
</div>
<hr>

### Technologies used
___

- **Spring Boot:** The project is built using the Spring Boot templates and conventions.
- **Spring Web:** Spring Web is used for HTTP requests handling, managing sessions, and processing web-related tasks.
- **Spring Security:** Authentication and authorization are managing to ensure secure access to endpoints with Spring Security.
- **Spring Data JPA:** Spring Data JPA is used for simplifies database operations and enables easy interaction with the database.
___
- **JWT:** JWT is used for implementation of the main principles of REST, namely Stateless.
- **Lombok:** Lombok is used to reduce boilerplate code by automatically generating common code constructs.
- **Swagger:** Swagger is used to provide interactive API documentation, testing use APIs more easily.
- **Mapstruct:** Mapstruct is used for object mapping between DTOs and entities.
___
- **MySQL:** MySQL is used as the database management system.
- **Liquibase:** Liquibase is employed for database version control and management.
- **Hibernate:** Hibernate is implemented for simplifies the interaction between Java code and our database.
___
- **Docker:** Docker is used for containerization database and for testing.
- **Postman:** Postman is used for automate testing workflows and application presentation.
- **Stripe:** Stripe is used for online payment implementation.
- **Telegram:** Telegram is used to create notifications for customers and managers with current car rental information.
- **Spark:** Spark is used for online payment implementation, using webhook.
 ___
### Domain Models (Entities)
___

> - 🚗 **Car**: Represents cars with different types: SEDAN, SUV, HATCHBACK, UNIVERSAL.
> - 😊 **User**: Contains information about the registered user including their authentication details and personal information.
> - 💲 **Payment**: Represents payment operation, contains information about car rental payment (status and time).
> - 🔑 **Rental**: Represents car rental operation placed by a user, contains information about rental date and actual return date.

### Who use this project?
___

* **_CUSTOMER_** - customer searches for and chooses a car, rents it for a certain period, returns the car and pays for the rental, and can also view his previous payments. The customer can view and change his profile. 
* **_MANAGER_** - manager can change the role of the customer, set the actual return date, search for a rental, manage cars.

<hr>
<div id="features" align="center">
  <h2 >🌟 Features</h2>
</div>
<hr>

[Back to content](#content)

### 1. Security: 

- User Authentication: Users can register and log in to the system.
- Role-Based Access Control: There are two roles: CUSTOMER(default) and MANAGER, for CUSTOMER some endpoints are not available.
- JWT Support: JSON Web Tokens (JWT) are used for authentication and securing API endpoints.
- Data Validation: Data validation is implemented in the new DTO classes to ensure the integrity of user inputs.

### 2. Exception handling, logging 

- Global Exception Handling: GlobalExceptionHandler is implemented to handle exceptions gracefully and provide consistent error responses.
- Custom exceptions: added EntityNotFoundException and RegistrationException to handle specific exceptions.
- Logger: Logging info and warn logs to file `logback.xml`

### 3. Car searching

- The user can search the list of cars, open detailed information about a specific car.
- The user can choose a car according to the following criteria: `brand, type(SEDAN, SUV, HATCHBACK, UNIVERSAL), fromPrice, toPrice`

### 4. Car managing

- The manager can add data about a new car, change the details of the car description and delete a car.

### 5. Rental service

- The user can rent the selected car for a specific time.
- The user cannot rent a second car until the first rental is completed.
- The manager can search the list of rentals, open detailed information about a specific rental.
- Also, manager can search for specific rental with users' ID and rentals' status(active by default).
- The manager sets actual return car date.

### 6. Users' profile

- The user can see detailed information in his profile and update personal data.
- The manager can update roles.

### 7. Online payment

- Users can make online payments thanks to the connected Stripe system.
- Our application also implements a webhook that manages the session and in any case the session will be executed.
- Users and managers can create payments on the basis of rental details and payment type. 
- There are two payment types:
     - `PAYMENT` (The user returned the car before the end of the rental period).
     - `FINE` (The user did not return the car before the end of the rental period, so a penalty is added to the total amount for the car rental).
- Payments can have different status: `PENDING, PAID, CANCELED, EXPIRED.`
- Users and managers can search for payments by users' ID.
- Also, users and managers can renew the session if the expiration time has expired.

### 8. Telegram notifications

- The user can use the Telegram bot to log into the application, see all rental's history and his current rental.
- Users and managers can receive Telegram notifications about a new rental created.
- Users can receive a notification that a new payment has been created for them to pay.


<hr>
<div id="controllers" align="center">
  <h2 >📝 Controllers</h2>
</div>
<hr>

[Back to content](#content)


> _To start using our application, the user must register and log in to the system._
#### AuthenticationController `/auth`

|        Feature        |      Endpoint      |                         Required fields                                      |
|-----------------------|:------------------:|:----------------------------------------------------------------------------:|
| Register a new user   | POST:/api/auth/register | email, password, repeat password, first name, last name   |
| Log in                |  POST:/api/auth/login   | email, password                                                              |

> Thus, the user gets access to the functions of the application according to his role (buyer or manager)

#### UserController `/users`

| Feature                |                Endpoint                | CUSTOMER | MANAGER |
|------------------------|:--------------------------------------:|:--------:|:-------:|
| Update user role       |       PUT: /api/users/{id}/role        |    X     |    ✔    | 
| Get my profile info    |           GET: /api/users/me           |    ✔     |    ✔    | 
| Update my profile info |          PATCH: /api/users/me          |    ✔     |    ✔    | 

#### CarController `/cars`

| Feature            |               Endpoint               | CUSTOMER | MANAGER |
|--------------------|:------------------------------------:|:--------:|:-------:|
| Get all cars       |            GET: /api/cars            |    ✔     |    ✔    | 
| Get car by ID      |         GET: /api/cars/{id}          |    ✔     |    ✔    | 
| Create new car     |           POST: /api/cars            |    X     |    ✔    | 
| Update a car       |         PUT: /api/cars/{id}          |    X     |    ✔    | 
| Delete car by ID   |        DELETE: /api/cars/{id}        |    X     |    ✔    | 

#### RentalController `/rentals`

| Feature                                 |            Endpoint            | CUSTOMER | MANAGER |
|-----------------------------------------|:------------------------------:|:--------:|:-------:|
| Add a new rental                        |       POST: /api/rentals       |    ✔     |    ✔    | 
| Get rentals by user ID and its status   |       GET: /api/rentals        |    X     |    ✔    | 
| Get specific rental by ID               |     GET: /api/rentals/{id}     |    X     |    ✔    | 
| Get all rentals                         |     GET: /api/rentals/all      |    X     |    ✔    | 
| Set actual return date                  |  GET: /api/rentals/{id}/return |    X     |    ✔    | 

#### PaymentController `/payments`

| Feature                                 |           Endpoint           | CUSTOMER | MANAGER |
|-----------------------------------------|:----------------------------:|:--------:|:-------:|
| Create a new payment session            |     POST: /api/payments/     |    ✔     |    ✔    | 
| Renew a payment session by session ID   |     POST: /api/payments      |    ✔     |    ✔    | 
| Get users payments by users ID          | GET: /api/payments/{user_id} |    ✔     |    ✔    | 
| Handle successful Stripe payments       | GET: /api/payments/success/  |    ✔     |    ✔    | 
| Handle unsuccessful Stripe payments     |  GET: /api/payments/cancel/  |    ✔     |    ✔    | 


<hr>
<div id="installation" align="center">
  <h2 >🛠️ ️Installation</h2>
</div>
<hr>

[Back to content](#content)

>This is a web application, so it is not necessary to install it, you can use it through a browser.
>But if you want to explore how our project is built, here is a detailed installation guide.

### Prerequisites

Make sure you have the following apps installed, this is necessary to run our app:
- Java 17 (https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- JDK, JDE
- You may also need Telegram, Docker and Postman.

## How to start this app:
Download git repository by git command:
 ```bash
 git clone https://github.com/jv-jun23-team4/Car-sharing.git
 ```
Build a project using **Maven**:
 ```bash
 mvn clean install
 ```
Then, rise a **Docker** container of your app:
 ```bash
 docker build -t {imageʼs name or hash code}
 docker-compose build
 docker-compose up
 ```
Also, you can run this project without docker, but before that, you need to configure the connection to your local database in the application properties. Run this command after that:
```bash
  mvn spring-boot:run
  ```

<hr>
<div id="usage" align="center">
  <h2 >⚙️ ️Usage</h2>
</div>
<hr>

[Back to content](#content)

### Our project is open source, so feel free to use it for your own needs.

### Documentation

>You can access the Swagger UI for API documentation and testing. 
Ensure that application is running.

**Swagger UI URL:** 
`http://localhost:8080/swagger-ui.html`

>You can find file for importing collection of requests in Postman:

**Postman:**
`Car Sharing.postman_collection.json`



<hr>
<div id="challenges" align="center">
  <h2 >🏋️ ️Challenges</h2>
</div>
<hr>

[Back to content](#content)

<hr>
<div id="summary" align="center">
  <h2 >🎯 Summary</h2>
</div>
<hr>

[Back to content](#content)

