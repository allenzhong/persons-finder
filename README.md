# 👥 Persons Finder – Backend Challenge

Welcome to the **Persons Finder** backend challenge! This project simulates the backend for a mobile app that helps users find people around them.

Your task is to implement a REST API that allows clients to create, update, and search for people based on location and other criteria.

---

## 📌 Requirements

Implement the following endpoints:

### ➕ `POST /persons`

Create a new person.

---

### ✏️ `PUT /persons/{id}/location`

Update (or create if not exists) a person's current **latitude** and **longitude**.

---

### 🔍 `GET /persons/nearby`

Find people around a **query location**, specified using the following query parameters:

* `lat`: latitude
* `lon`: longitude
* `radiusKm`: radius in kilometres

> 🧠 **Extra challenge**: Return the list **sorted by distance** to the query point.

---

### 👤 `GET /persons`

Retrieve one or more persons by their IDs. Accepts:

* `id`: one or more person IDs (e.g., `?id=1&id=2`)

---

## 📦 Expected Output

All responses must be in **valid JSON format**, following clean and consistent REST API design principles.

---

## 🧱 What You Need to Build

* Domain models: `Person`, `Location`, etc.
* Services for saving, updating, and querying data
* In-memory storage or a basic persistent layer
* Proper project structure (e.g. controller / service / repository)
* Extra bonus if you use UseCase pattern (Controller -> Use Case (business logic) -> Service -> Repository)

---

## 🧪 Bonus Points

### ✅ Testing

* Include **unit tests** for service logic
* Include **integration tests** for API endpoints
* Use a test framework like **JUnit**, **MockK**, or **Mockito**

---

### 🧠 Scalability Challenge

* Seed the system with **1 million**, **10 million**, and **100 million** records
* Benchmark and **optimise** the `GET /persons/nearby` endpoint
* Explain any indexing or query optimisation strategies used

---

### 📚 Clean Code

* Use **DTOs** for API request and response bodies
* Apply proper **validation**, **error handling**, and maintain clean separation of concerns

---

## ✅ Getting Started

```bash
git clone https://github.com/leonardoduartelana/persons-finder.git
cd persons-finder
```

Implement your solution and push it to your **own GitHub repository**.

---

## 📬 Submission & Questions

* Submit the link to your GitHub repository
* For any questions, email: [leo@emerge.nz](mailto:leo@emerge.nz)

---

## 💡 Tips

* Use **OpenAPI/Swagger** to document your APIs (optional, but encouraged)
* Handle edge cases like missing locations or malformed input
* Design the system **as if it were going into production**

---

## 📁 Project Structure

```
persons-finder/
├── 📂 src/
│   ├── 📂 main/
│   │   ├── 📂 kotlin/com/persons/finder/
│   │   │   ├── 📂 application/
│   │   │   │   └── 📂 usecases/           # Business logic use cases
│   │   │   │       ├── CreatePersonUseCase.kt
│   │   │   │       ├── GetNearbyPersonsUseCase.kt
│   │   │   │       └── UpdatePersonLocationUseCase.kt
│   │   │   ├── 📂 domain/
│   │   │   │   ├── 📂 models/              # Domain entities
│   │   │   │   │   ├── Person.kt
│   │   │   │   │   └── Location.kt
│   │   │   │   └── 📂 services/            # Domain services
│   │   │   │       ├── LocationsService.kt
│   │   │   │       └── PersonsService.kt
│   │   │   ├── 📂 infrastructure/
│   │   │   │   ├── 📂 config/              # Configuration classes
│   │   │   │   │   └── GeoConfig.kt
│   │   │   │   └── 📂 repositories/        # Data access layer
│   │   │   │       ├── LocationRepository.kt
│   │   │   │       └── PersonRepository.kt
│   │   │   ├── 📂 presentation/
│   │   │   │   ├── 📂 controllers/         # REST API controllers
│   │   │   │   │   └── PersonController.kt
│   │   │   │   └── 📂 dto/                 # Data Transfer Objects
│   │   │   │       ├── 📂 request/         # API request DTOs
│   │   │   │       └── 📂 response/        # API response DTOs
│   │   │   └── 📂 seeding/                 # Data seeding utilities
│   │   │       ├── DataSeeder.kt
│   │   │       └── SeedingCommandLineRunner.kt
│   │   └── 📂 resources/
│   │       ├── application.properties      # Main configuration
│   │       ├── application-postgres.properties  # PostgreSQL config
│   │       ├── application-seeding.properties   # Seeding config
│   │       ├── schema.sql                  # H2 database schema
│   │       └── schema-postgres.sql         # PostgreSQL schema
│   └── 📂 test/
│       └── 📂 kotlin/com/persons/finder/
│           ├── 📂 application/             # Use case tests
│           ├── 📂 domain/                  # Domain service tests
│           ├── 📂 infrastructure/          # Repository tests
│           └── 📂 integration/             # Integration tests
│               └── 📂 controllers/         # API endpoint tests
├── 📂 scripts/
│   └── benchmark-scalability.sh            # Performance benchmark script
├── 📂 benchmark/                           # Benchmark results and reports
│   ├── README.md                           # Benchmark documentation
│   ├── benchmark-results-*.csv             # Raw benchmark data
│   ├── scalability-benchmark-report-*.md   # Formatted reports
│   └── seeding-times.txt                   # Seeding performance data
├── 📂 request-tests/                       # HTTP request test files
│   └── find-nearby.http                    # API testing examples
├── build.gradle.kts                        # Gradle build configuration
├── gradlew                                 # Gradle wrapper script
└── README.md                               # This file
```

### 📋 Key Directories Explained

#### **🏗️ Architecture Layers**
- **`application/`**: Contains use cases that orchestrate business logic
- **`domain/`**: Core business entities and domain services
- **`infrastructure/`**: Data access layer and external configurations
- **`presentation/`**: REST API controllers and DTOs

#### **🧪 Testing Structure**
- **`test/`**: Mirrors the main source structure for comprehensive testing
- **`integration/`**: End-to-end API tests
- **`application/`, `domain/`, `infrastructure/`**: Unit tests for each layer

#### **📊 Benchmark Suite**
- **`benchmark/`**: Contains all performance testing results and documentation
- **`scripts/`**: Automated benchmark execution scripts

#### **🔧 Configuration**
- **`resources/`**: Application properties for different environments
- **`schema-*.sql`**: Database schemas for H2 and PostgreSQL

#### **📝 Documentation & Testing**
- **`request-tests/`**: HTTP files for manual API testing
- **`README.md`**: Project documentation and setup instructions

This structure follows **Clean Architecture** principles with clear separation of concerns, making the codebase maintainable, testable, and scalable.

---

## 🚀 Performance Benchmarking

This project includes a comprehensive performance benchmarking suite to test scalability across different dataset sizes and usage patterns.

### 🏃‍♂️ Running Benchmarks

```bash
# Run the complete benchmark suite
./scripts/benchmark-scalability.sh
```

### 📈 Benchmark Results

- 📄 **[Benchmark Documentation](benchmark/README.md)** - Complete guide to running and interpreting benchmarks
- 📊 **[Latest Benchmark Report](benchmark/scalability-benchmark-report-20250615-142210.md)** - Detailed performance analysis with 100M dataset results
- 📋 **[Raw Benchmark Data](benchmark/benchmark-results-20250615-142210.csv)** - CSV data for custom analysis and visualization
- ⏱️ **[Seeding Performance](benchmark/seeding-times.txt)** - Data insertion performance metrics across all dataset sizes

### 🎯 Key Performance Highlights

- **✅ 100M Dataset**: Successfully tested with optimized JVM settings
- **⚡ Response Times**: 1km queries complete in ~500ms, 100km queries in ~25 seconds  
- **🧠 Memory Optimization**: Database-level pagination prevents OutOfMemoryError
- **📈 Scalability**: Linear performance scaling with dataset size
- **🔧 Optimizations**: G1GC garbage collector, 4GB heap, efficient SQL queries

---

*The benchmark suite demonstrates the system's ability to handle real-world scalability requirements, from small datasets to millions of records, ensuring robust performance across all usage scenarios.*
