# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot REST API project using Java 21, Maven, and MySQL. The project follows a standard Spring Boot structure with JPA for database operations and Lombok for reducing boilerplate code.

## Common Commands

### Build and Run
- `mvn clean compile` - Clean and compile the project
- `mvn spring-boot:run` - Run the Spring Boot application locally
- `mvn clean package` - Build the application JAR file
- `./mvnw spring-boot:run` - Run using Maven wrapper (cross-platform)
- `./mvnw.cmd spring-boot:run` - Run using Maven wrapper (Windows)

### Testing
- `mvn test` - Run all unit tests
- `mvn test -Dtest=ClassName` - Run specific test class
- `mvn test -Dtest=ClassName#methodName` - Run specific test method

### Development
- `mvn clean install` - Clean, compile, test, and install to local repository
- `mvn dependency:tree` - Show project dependencies
- `mvn spring-boot:help` - Show Spring Boot plugin help

## Architecture

### Project Structure
- **Package**: `com.example.prueba_tecnica_api_rest`
- **Main Class**: `PruebaTecnicaApiRestApplication.java`
- **Config**: `application.properties` in `src/main/resources`

### Key Dependencies
- Spring Boot 3.5.5 (Web, Data JPA)
- MySQL Connector
- Lombok for code generation
- JUnit 5 for testing

### Database Configuration
The project is configured to use MySQL database. Connection details should be configured in `application.properties`.

## Development Notes

- Java version: 21
- Uses Maven wrapper (`mvnw`/`mvnw.cmd`) for consistent builds
- Lombok is configured with annotation processing
- Spring Boot auto-configuration handles most setup
- Standard Spring Boot project structure with `src/main/java` and `src/test/java`

## Package Name Note
The original package name `com.example.prueba-tecnica-api-rest` was invalid due to hyphens, so the project uses `com.example.prueba_tecnica_api_rest` instead (with underscores).