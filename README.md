    # Ktor Server Application

This is a custom Ktor server project generated using the official recommendations from the Ktor Project Generator.

## 🚀 Technology Stack

- **Framework**: [Ktor 3.0.0](https://ktor.io/)
- **Programming Language**: [Kotlin 2.0.20](https://kotlinlang.org/)
- **Build System**: Gradle Kotlin DSL with Version Catalog
- **Server Engine**: Netty
- **Configuration Format**: HOCON (`application.conf`)

## 🛠️ Features Included

1. **Routing**: Standard endpoint routing.
2. **Content Negotiation**: Uses `kotlinx.serialization` for handling JSON requests/responses.
3. **Call Logging**: Built-in HTTP request logging.
4. **Logback Support**: Structured logging to stdout.

---

## 📁 Project Structure

```text
ktor-app/
├── gradle/
│   └── libs.versions.toml      # Dependency Version Catalog
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/
│       │       └── example/
│       │           └── Application.kt   # Main Entrypoint & Routing
│       └── resources/
│           ├── application.conf         # App configurations (host/port)
│           └── logback.xml              # Logging configurations
├── build.gradle.kts            # Build scripts
├── settings.gradle.kts         # Project settings
└── gradlew                     # Gradle wrapper script
```

---

## 🏃 Running the Project

To run the application locally, execute the following command in the root directory:

```bash
./gradlew run
```

The application will start, and you can access the following endpoints:

- **Home**: [http://localhost:8080/](http://localhost:8080/)
- **JSON Endpoint**: [http://localhost:8080/json](http://localhost:8080/json)

## 📦 Building for Production

To compile and package the application:

```bash
./gradlew build
```

This will compile the Kotlin source code, run tests, and generate standard distribution zip/tar archives under `build/distributions/`.
