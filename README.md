# JPMorgan Chase & Co. - Software Engineering Job Simulation (Midas Core)

This repository contains the completed codebase for the **JPMorgan Chase & Co. Software Engineering Job Simulation** (formerly Forage). The project implements the core service of **Midas**, a high-throughput backend system responsible for receiving, validating, recording, and processing financial transactions.

---

## 🏗️ System Architecture

Midas Core is built using **Spring Boot 3.x** and integrates several technologies:
* **Ingestion Layer**: Apache Kafka for processing asynchronous, decoupled transaction messages.
* **Storage Layer**: H2 In-Memory Database managed through Spring Data JPA.
* **External Integrations**: RestTemplate calls to an external transaction incentives REST API.
* **User Endpoint**: REST Controller exposing a GET API for balance lookup.

```
       [Kafka Producer (Test)]
                  │
                  ▼ (Topic: trader-updates)
         [Midas Kafka Listener]
                  │
                  ▼
         [Database Conduit] ──── (REST POST) ───► [External Incentive API]
                  │                                         │
                  │ (Update Balances & Persist)             │ (Incentive Amount)
                  ▼                                         ▼
      [H2 Database (User & Transactions)] ◄─────────────────┘
                  ▲
                  │ (GET /balance?userId=...)
          [REST Controller]
                  ▲
                  │
          [Balance Querier]
```

---

## 📝 Task Summaries

### Task 1: Environment Setup & Configuration
* Configured a Java 17 development environment with Spring Boot 3.2.5 and Maven.
* Configured core project dependencies in `pom.xml`, including Spring Data JPA, H2 Database, Spring Kafka, Spring Test, and Kafka Testcontainers.
* Populated `application.yml` with the default Kafka topic configuration.

### Task 2: Kafka Decoupled Integration
* Configured String key and JSON value serializers/deserializers for Kafka in `application.yml`.
* Set up consumer package trust constraints.
* Implemented a `@Component` listener (`KafkaConsumer.java`) to consume transaction stream messages, deserializing them directly to `Transaction` domain objects.

### Task 3: Database & Business Logic Verification
* Defined the `@Entity` model for `TransactionRecord` establishing `@ManyToOne` database mappings back to the `UserRecord` entity.
* Built repository layers for database operations.
* Implemented `@Transactional` processing logic within `DatabaseConduit.java` to perform transaction validation checks:
  1. Sender ID validity.
  2. Recipient ID validity.
  3. Sender balance sufficiency.
* Deducted transaction amounts from the sender and credited them to the recipient upon successful validation.

### Task 4: External API Consumption (Incentive Integration)
* Built `IncentiveService.java` to invoke the external Incentives REST service endpoint (`POST http://localhost:8080/incentive`).
* Modified user balance adjustments to credit the computed incentive amount to the recipient's account without impacting the sender.
* Persisted both transaction and incentive values inside `TransactionRecord`.

### Task 5: Exposed GET /balance REST Controller
* Modified `application.yml` to specify server execution on port `33400`.
* Developed a `@RestController` class (`BalanceController.java`) exposing a `GET /balance` endpoint accepting a `userId` query parameter.
* Returned a JSON-serialized `Balance` object representing the user's current account balance (defaulting to `0.0` if the user is not found).

---

## 📂 Solutions and Certificate

All task solutions documents and the final program completion certificate are located in the [task_solutions](task_solutions/) directory:

* 📄 **Task 1 Solutions Guide**: [task_solutions/Task_1_solution.pdf](task_solutions/Task_1_solution.pdf)
* 📄 **Task 3 Solutions Guide**: [task_solutions/Task_3_solution.pdf](task_solutions/Task_3_solution.pdf)
* 📄 **Task 4 Solutions Guide**: [task_solutions/Task_4_solution.pdf](task_solutions/Task_4_solution.pdf)
* 📄 **Task 5 Solutions Guide**: [task_solutions/Task_5_solutions.pdf](task_solutions/Task_5_solutions.pdf)
* 🎓 **Program Completion Certificate**: [task_solutions/certificate_software_JPMC.pdf](task_solutions/certificate_software_JPMC.pdf)

---

## ⚙️ How to Run Locally

### Prerequisites
* Java 17 JDK
* Maven (or use the provided `./mvnw` wrapper)

### Step 1: Run the Incentive API
Start the local transaction incentive API jar:
```bash
java -jar services/transaction-incentive-api.jar
```

### Step 2: Build Midas Core
Compile and build the application:
```bash
./mvnw clean install
```

### Step 3: Run the Application
Start the Spring Boot application:
```bash
./mvnw spring-boot:run
```

### Step 4: Execute Test Verification Suite
Run the verification test suite to check code compliance:
```bash
# Run all tests
./mvnw test

# Run a specific task verifier (e.g. Task 5)
./mvnw -Dtest=TaskFiveTests test
```
