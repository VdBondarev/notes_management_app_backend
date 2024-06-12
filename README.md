# Hi there!!! The backend of notes management application is welcoming you!

# The backend of Notes Management project API README

### [Frontend is here.](https://github.com/VdBondarev/notes_management_app_frontend)

## [See the detailed video-explanation on starting and using both backend and frontend applications by following this link.](https://www.loom.com/share/995da21a4a6e4b03bfdf7beab86a2ee9?sid=f132659f-6bdb-4c53-a428-7c6a3e26a737)

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
4. Ensure you have Docker installed (+ up and running).
5. Put your properties into the `.env` file. [Take a look at an example in this file](.envSample) (you can just rename it to .env and work with this).
6. Compile the project into jar using Maven: `mvn clean package` (+ it will run all the tests).
7. Build an image of this application and database used here with the command: `docker compose build`.
8. Run the application using Docker: `docker compose up`.

## For endpoints understanding

- First of all: see descriptions (@Operation annotation) on each endpoint and controller (@Tag annotation).
- Second of all: watch the video attached at the very start of this file.
