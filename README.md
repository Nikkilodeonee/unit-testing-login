# Login Service — Unit Testing with JUnit 5 & Mockito

A small Java console application that demonstrates authentication logic with account lockout, backed by a comprehensive unit test suite using JUnit 5 and Mockito.

## Overview

The application prompts for credentials, validates them against an in-memory user store, and displays the authenticated user's address on success. Failed login attempts are tracked; after three consecutive failures the account is locked.

## Features

- Credential validation with success / failure results
- Failed-attempt counter persisted per user
- Automatic account lockout after 3 failed attempts
- `UserLockedException` when a locked account attempts to sign in
- Mockito-based unit tests with isolated `UserStore` dependency

## Tech Stack

| Technology | Version |
|------------|---------|
| Java | 21 |
| JUnit Jupiter | 5.11.4 |
| Mockito | 5.14.2 |
| Maven | 3.x |

## Project Structure

```
src/main/java/com/loginapp/
├── Application.java          # Console entry point
├── data/
│   ├── UserStore.java        # Persistence interface
│   └── DefaultUserStore.java # In-memory implementation
├── domain/
│   ├── User.java
│   ├── Address.java
│   ├── Credentials.java
│   └── LoginResult.java
└── service/
    ├── UserService.java
    ├── DefaultUserService.java
    └── UserLockedException.java

src/test/java/com/loginapp/service/
└── DefaultUserServiceTest.java
```

## Getting Started

### Prerequisites

- JDK 21+
- Apache Maven 3.8+

### Run tests

```bash
mvn test
```

### Run the application

Run `com.loginapp.Application` from your IDE, or after building:

```bash
mvn -q package -DskipTests
java -cp target/classes com.loginapp.Application
```

### Demo credentials

| Username   | Password          |
|------------|-------------------|
| `adam`     | `secret_adam`     |
| `charlotte`| `secret_charlotte`|

## Test Coverage

`DefaultUserServiceTest` verifies:

- Successful login resets the failed-attempt counter
- Wrong password increments the counter without locking (below threshold)
- Third failed attempt locks the account and throws `UserLockedException`
- Locked users cannot authenticate
- Unknown usernames return `UNSUCCESSFUL` without side effects
- Address is available after a successful login

## License

This project is licensed under the MIT License — see [LICENSE](LICENSE).

## Author

**[Nikkilodeonee](https://github.com/Nikkilodeonee)**
