# Hi there!!! The backend of notes management application is welcoming you!

# The backend of Notes Management project API README

## Main technologies used

- **Spring Boot (v3.3.0):** A super-powerful framework for creating Java-based applications (just like this one).
- **Spring Data JPA:** Simplifies the data access layer and interactions with the database.
- **Springdoc-openapi:** Eases understanding and interaction with endpoints for other developers.
- **MapStruct (v1.5.5.Final):** Simplifies the implementation of mappings between Java objects.
- **Liquibase:** A powerful way to ensure database-independence, schema changes and control.
- **Docker.**

## Project structure

This Spring Boot application follows the most common structure with such **main layers** as:
- repository (for working with database).
- service (for business logic implementing).
- controller (for accepting client's requests and getting responses to them).

Also, it has other **important layers** such as:
- mapper (for converting models for different purposes).
- exception (custom global exception handler for better representation of problems you may face).
- dto (for managing info about models and better representation of it).
- config (mappers and web config).

## Setup Instructions

To set up and run the project locally, follow these steps:

1. Clone the repository.
2. Ensure you have Java 21 installed.
3. Ensure you have Maven installed.
4. Ensure you have Docker installed.
5. Put your properties in the `.env` file. [take a look at an example in this file](.envSample) (you can just rename it to .env and work with this).
6. Compile the project into jar using Maven: `mvn clean package` (+ it will run all the tests).
7. Build the image using Docker: `docker compose build`.
8. Run the application using Docker: `docker compose up`.

## [See the detailed explanation on starting and using both backend and frontend applications by following this link](https://www.loom.com/share/7ec76ef8a02144bb93836066957f34ce?sid=b76b5c6e-363f-4e58-9848-03c076361ea4)

## For endpoints understanding

- First of all: see descriptions (@Operation annotation) on each endpoint and controller (@Tag annotation).
- Second of all: watch the video attached above.
