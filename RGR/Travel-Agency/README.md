# Welcome to Travel Agency!

Travel Agency is a web-based management system: it enables travel offers, countries, and tour types to be created, accessed, and controlled from a single interface. The application features a role-based security system where data can be viewed by users and managed by administrators.

This release runs on Linux, Windows (via WSL), and macOS using Docker containers.

## Dependencies

Travel Agency depends on **Java 21** (Eclipse Temurin is recommended).
It also depends on **Docker** and **Docker Compose** for database orchestration.

To build Travel Agency, a JDK 21, Maven, and a running Docker Desktop instance are needed.

## Installation

### Configuration

Before running, the application requires database credentials to be passed as environment variables.
Create a `.env` file in the project root or configure your environment:

* `DB_LOGIN`
* `DB_PASSWORD`

### From source with Docker (Recommended)

To build and install Travel Agency from the source tree using Docker Compose, use:

1.  Build the project artifact:
    ```
    mvn clean package
    ```

2.  Run the containers:
    ```
    docker-compose up (-d)
    ```

The application will automatically provision a PostgreSQL database on port `5933` and start the web server on port `8086`.

### From IDE (Eclipse)

To run the application directly from an IDE:
1.  Ensure the database is running (`docker-compose up` with the app service commented out).
2.  Configure Run Environment variables (`DB_LOGIN`, `DB_PASSWORD`).
3.  Run `TravelAgencyApplication.java`.

### Credentials

The system comes pre-configured with the following accounts:

* `Role |Login|Password|Access          `
* `ADMIN|admin|password|Full CRUD access`
* `USER |user |password|Read-only access`

## Contributing

Bug reports, feature suggestions, and code contributions are most welcome.
Please make sure to update tests as appropriate.

## Support

For general discussion and bug reports, please open an issue in the project repository or contact the developer directly.
