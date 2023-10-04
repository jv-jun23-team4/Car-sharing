<div align="center"> <h1 > <img src=logo.jpg width="200" align="center" alt="50"/></h1></div>

# <div align="center">Car Sharing Service</div>
___

#### Welcome to our Car Sharing Service platform!

#### Our Car Sharing Service application is powered by Spring Boot, offering a convenient and efficient way to share vehicles. Whether you need a car for a quick errand or a road trip, our service is designed to make your experience smooth and hassle-free.

#### In this README, you'll find detailed information about our platform, its features, and how to get started with car sharing. Explore the exciting world of shared mobility and enjoy the freedom of driving without the responsibilities of ownership. 
#### Happy car sharing!
___

## Table of contents
- [👨‍💻Project Overview](#Overview)
- [🌟Features](#features)
- [‍📝Controllers](#controllers)
- [🛠️Installation](#installation)
- [⚙️Usage](#usage)
- [🎯Summary](#summary)
- [🧑Contributing](#contributing)
- [🏋️Challenges](#challenges)

<hr>
<div id="Overview" align="center">
  <h2 > Project Overview</h2>
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
- **Spark:** Spark is used for online payment implementation.
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
  <h2 > Features</h2>
</div>
<hr>

<hr>
<div id="controllers" align="center">
  <h2 > Controllers</h2>
</div>
<hr>


|        FEATURE        |      Endpoint      |                         Required fields                                      |
|-----------------------|:------------------:|:----------------------------------------------------------------------------:|
| Register a new user   | POST:/api/register | email, password, repeat password, first name, last name   |
| Log in                |  POST:/api/login   | email, password                                                              |



<hr>
<div id="️installation" align="center">
  <h2 > ️Installation</h2>
</div>
<hr>

<hr>
<div id="️usage" align="center">
  <h2 > ️Usage</h2>
</div>
<hr>