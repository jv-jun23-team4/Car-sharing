<div align="center"> <h1 > <img src=logo.jpg width="600" align="center" alt="50"/></h1></div>
<div align="center"> <h1> Car Sharing Service </h1> </div>

### Welcome to our Car Sharing Service!

#### Our Car Sharing Service application is powered by Spring Boot, offering a convenient and efficient way to share vehicles. Whether you need a car for a quick errand or a road trip, our service is designed to make your experience smooth and hassle-free.

#### In this README, you'll find detailed information about our project, its features, and how to get started with car sharing. We offer you to try a reliable and convenient car sharing service without unnecessary worries and responsibilities. 
#### Let's get started!

---
## Content
- [👨‍💻Project Overview](#Overview)
- [🌟Features](#features)
- [‍📝Controllers](#controllers)
- [🛠️Installation](#installation)
- [⚙️Usage](#usage)
- [🛠️Installation](#installation)
- [🎯Conclusion](#conclusion)

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

| Feature                |        Endpoint        | CUSTOMER | MANAGER |
|------------------------|:----------------------:|:--------:|:-------:|
| Get all cars           |     GET: /api/cars     |    ✔     |    ✔    | 
| Get car by ID          |  GET: /api/cars/{id}   |    ✔     |    ✔    | 
| Create new car         |    POST: /api/cars     |    X     |    ✔    | 
| Update a car           |  PUT: /api/cars/{id}   |    X     |    ✔    | 
| Delete car by ID       | DELETE: /api/cars/{id} |    X     |    ✔    | 
| Search cars by params  | GET: /api/cars//search |    ✔     |    ✔    | 

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
| Get users payments by users ID          | GET: /api/payments/{user_id} |    X     |    ✔    | 
| Handle successful Stripe payments       | GET: /api/payments/success/  |    ✔     |    ✔    | 
| Handle unsuccessful Stripe payments     |  GET: /api/payments/cancel/  |    ✔     |    ✔    | 


<hr>
<div id="usage" align="center">
  <h2 >⚙️ ️Usage</h2>
</div>
<hr>

[Back to content](#content)

#### You can use this application without installing anything because this project is deployed by AWS, so the functionality of our project can be accessed anytime and from anywhere. 
#### You can test the work of our project easily, with the help of detailed documentation from Swagger UI or using the Postman collection. 
**Swagger UI**
>You can access the Swagger UI for API documentation and testing.


#### Follow these steps:

1.     Start the Application
2.     Launch web browser 
3.     In the browser's address bar, enter the URL for the Swagger documentation

` URL: `http://ec2-51-20-108-114.eu-north-1.compute.amazonaws.com/api/swagger-ui/index.html#/ `

**Postman**
>You can access the Postman and test API with ready-to-use postman collection.

#### Follow these steps:

1.     Open Postman.
2.     Click on the "Import" button in the top left corner. 
3.     In the "Import" dialog, select the "File" tab.
4.     Click on the "Upload Files" button and select the`Car Sharing.postman_collection.json` file from root project directory.

>You can also test our project in postman without downloading it locally, use this link.
URL:  http://ec2-13-53-170-220.eu-north-1.compute.amazonaws.com/api


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
<<<<<<< HEAD
- JDK, JDE
- You may also need Telegram, Docker and Postman.
=======
- Docker (https://www.docker.com/products/docker-desktop/)
- Maven (https://maven.apache.org/download.cgi)
- MySql (https://dev.mysql.com/downloads/installer/)
- You may also need Telegram and Postman.

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

---

<hr>
<div id="conclusion" align="center">
  <h2 >🎯 Conclusion</h2>
</div>
<hr>

[Back to content](#content)

***_We are glad to welcome you in our Car Sharing Service!_***

***_Thank you for your time with us, we hope you got a lot of benefits and new opportunities. We created this project with thoughts about our users and their needs, to make Car Sharing even more convenient and online payment even more secure._***

