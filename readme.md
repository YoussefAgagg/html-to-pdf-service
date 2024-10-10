# HTML to PDF Service

A service that converts HTML content to PDF using Spring MVC, Jakarta EE, and other technologies.

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [Endpoints](#endpoints)
- [Configuration](#configuration)
- [Chrome & ChromeDriver](#chrome--chromedriver)
- [Built With](#built-with)

## Features

- Convert HTML content to PDF
- Docker support for easy deployment

## Getting Started

These instructions will help you set up and run the project on your local machine.

### Prerequisites

- Java JDK 21
- Docker (optional, for containerized deployment)
- Gradle
- Chrome Browser

### Installation

1. Clone the repository
   ```sh
   git clone https://github.com/yourusername/html-to-pdf-service.git
   cd html-to-pdf-service
   ```

2. Build the project using Gradle
   ```sh
   ./gradlew build
   ```

3. (Optional) Build and run using Docker
    ```shell
    docker network create npm
    ```
   ```sh
   docker-compose up --build
   ```

## Running the Application

To run the application using Gradle, execute the following command:

Make sure to use the `local` profile if not running it with Docker. This can be done by setting the active profile when running the application:
when running with local profile, the application will use the local chrome browser installed on the machine.
and you will need to add the chrome driver to the resources folder.
[Chrome & ChromeDriver](#chrome--chromedriver)
```sh
./gradlew bootRun --args='--spring.profiles.active=local'
```

The application will be available at `http://localhost:8080`.


## Endpoints

- `POST /html-to-pdf-single` : Converts HTML content to PDF.
    - Request Body: String containing HTML content.
    - Response: PDF file.
- `POST /html-to-pdf` : Converts a list of HTML content to one PDF.
    - Request Body: array of Strings containing HTML content.
    - Response: PDF file.

## Configuration

Configuration files are located in the `src/main/resources` directory.

- `application.properties` : Main configuration file.
- `application-local.properties` : Local environment configurations.

## Chrome & ChromeDriver

This application requires the Chrome browser to be installed on your machine.

To find your currently installed Chrome version, run the following command in the terminal:
```sh
google-chrome --version
```

Once you have your Chrome version, you need to download the corresponding ChromeDriver from [Chrome for Testing](https://googlechromelabs.github.io/chrome-for-testing/).

Save the ChromeDriver executable in a directory that is included in your system's PATH, or configure the application by setting the `chrome.driver.path` property in the `application-local.properties` file:
```properties
chrome.driver.path=/path/to/chromedriver
```

Alternatively, you can place the ChromeDriver executable in the `src/main/resources` directory, and there will be no need to change the `chrome.driver.path` property.

## Built With

- [Spring](https://projects.spring.io/spring-boot/) - Web framework
- [Docker](https://www.docker.com/) - Containerization platform
- [Gradle](https://gradle.org/) - Build tool
- [Lombok](https://projectlombok.org/) - Boilerplate code reduction
- [selenuim](https://github.com/SeleniumHQ/selenium)

