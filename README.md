# Menu Manager

Menu Manager is a Spring Boot web application for creating and maintaining a
restaurant menu. It demonstrates a complete server-rendered CRUD workflow with
persistent local storage, validation, search, filtering and a responsive UI.

## Features

- Create, view, edit and delete menu items
- Validate submitted names, descriptions, prices and categories on the server
- Search item names with case-insensitive partial matching
- Filter by database-driven category options and availability
- Combine search, category and availability filters
- Show clear success, empty-state and missing-item messages
- Confirm destructive delete actions in the browser
- Keep data between restarts in a file-based H2 database
- Use a responsive Bootstrap card layout on desktop and mobile

## Technologies

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- Thymeleaf
- Bootstrap 5
- H2 Database
- Maven
- Jakarta Validation

## Application Architecture

The project follows a small, conventional layered structure:

```text
Browser
  ↓
Controller → handles routes, form binding and page models
  ↓
Service    → contains menu-item operations and input normalisation
  ↓
Repository → runs Spring Data CRUD and filter queries
  ↓
H2 Database
```

The code remains in the `com.adriantomczak.menumanager` package, with separate
`controller`, `service`, `repository` and `model` packages.

## Running the Project

Requirements: Java 17 or newer. The Maven Wrapper is included, so a separate
Maven installation is not required.

On Windows PowerShell:

```powershell
git clone https://github.com/AdrianTomczakGit/menu-manager.git

cd menu-manager

.\mvnw.cmd spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) in a browser. Menu items are
stored under the local `data/` directory, which is intentionally excluded from
Git. The development-only H2 console is available at
[http://localhost:8080/h2-console](http://localhost:8080/h2-console).

## Running Tests

```powershell
.\mvnw.cmd test
```

The test suite uses a separate in-memory H2 database, so it does not change
locally stored menu items.

## Screenshots

Screenshots can be added here later to show the responsive homepage, filters
and edit form.

## Future Improvements

Possible next steps include:

- User authentication and role-based access
- PostgreSQL or MySQL for production deployments
- A REST API for other clients
- Menu-item image uploads
- Customer menu ordering
- Cloud deployment and production configuration

These are potential enhancements and are not part of the current application.
