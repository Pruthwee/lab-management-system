# Laboratory Management System - Violation Demo

A fully functional Spring Boot 3.1.5 Laboratory Management System with **intentional violations** across Cloud Compatibility, Software Portability, Code Sustainability, and Database Dependency domains.

**Created for:** Concierto Modernize custom rules analysis and detection

## 🎯 Purpose

This application demonstrates real-world anti-patterns and violations commonly found in enterprise systems, specifically tailored to laboratory and healthcare management contexts. Each violation is intentionally embedded to be:
- ✅ **Functional** - The application runs and works
- ✅ **Realistic** - Patterns found in actual legacy systems  
- ✅ **Detectable** - Rules can identify these violations
- ✅ **Educational** - Learn from anti-patterns

## 📋 Violations Included

### Cloud Compatibility (20 violations)
- Hardcoded server IP addresses (192.168.1.100)
- Local file system storage only (C:\lab\data\)
- Stateful session management
- Direct TCP socket connections without service discovery
- Non-idempotent operations
- Synchronous blocking calls

### Software Portability (20 violations)
- Windows-only file paths (C:\lab\equipment\)
- Database vendor coupling (Oracle mode)
- External binary tools (ProcessBuilder)
- Custom serialization (ObjectInputStream)
- Unsafe singleton patterns
- Hard-coded character encoding

### Code Sustainability (20 violations)
- No database indexes on frequently searched fields
- String concatenation instead of StringBuilder
- Eager loading in entity relationships
- N+1 query problems
- No pagination support
- String-based enums instead of Java enums
- Unbounded in-memory caches
- No validation on input
- Database queries in loops

### Database Dependency (20 violations)
- Database-specific date functions (TRUNC, DATEPART)
- LOB/CLOB column storage
- Stored procedures for business logic
- Database-specific numeric types
- Full-text search indexes
- Database-specific CASE logic
- Oracle compatibility mode assumptions

## 🏗️ Architecture

```
lab-management-system/
├── pom.xml
├── README.md
├── VIOLATIONS_ANALYSIS.md
├── src/main/
│   ├── java/com/lab/management/
│   │   ├── LabManagementApplication.java
│   │   ├── entity/LabEntities.java
│   │   ├── repository/LabRepositories.java
│   │   ├── service/LabServices.java
│   │   ├── controller/LabControllers.java
│   │   └── config/LabConfig.java
│   └── resources/
│       ├── application.properties
│       └── data.sql
└── target/
    └── lab-management-system-1.0.0.jar
```

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- 2GB RAM minimum

### Build

```bash
cd lab-management-system
mvn clean install -DskipTests
```

### Run

```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/lab-management-system-1.0.0.jar
```

### Access

**Application:** http://localhost:8080/
**H2 Console:** http://localhost:8080/h2-console

H2 Console credentials:
- Username: `sa`
- Password: (empty)
- JDBC URL: `jdbc:h2:mem:testdb`

## 📡 API Endpoints

### Lab Results
```
GET    /api/results              - Get all results (NO PAGINATION)
GET    /api/results/{id}/report  - Get result report (STRING CONCAT)
POST   /api/results/search       - Search results (NO VALIDATION)
GET    /api/results/export       - Export (CUSTOM URL ENCODING)
```

### Specimens
```
GET    /api/specimens            - Get all specimens (NO PAGINATION)
POST   /api/specimens/{id}/store - Store locally (CLOUD VIOLATION)
GET    /api/specimens/{id}/info  - Get info (HARDCODED FORMAT)
```

### Lab Tests
```
POST   /api/tests/batch-process  - Process batch (DB QUERY IN LOOP)
GET    /api/tests/by-date-range  - Get by date (NO PAGINATION)
POST   /api/tests/{id}/connect   - Connect to equipment (HARDCODED IP)
```

### Patients
```
GET    /api/patients             - Get all patients (NO PAGINATION)
GET    /api/patients/by-gender   - Get by gender (STRING ENUM)
GET    /api/patients/mrn/{mrn}   - Get by MRN (NO CACHE)
POST   /api/patients/register    - Register patient (STRING CONCAT)
```

### Equipment
```
GET    /api/equipment            - Get all equipment
GET    /api/equipment/{id}/config-path        - Get config path (WINDOWS PATH)
GET    /api/equipment/{id}/test-connection   - Test connection (HARDCODED IP)
POST   /api/equipment/{id}/calibration       - Upload calibration (CLOB STORAGE)
```

### Reagents
```
GET    /api/reagents             - Get all reagents (NO PAGINATION)
GET    /api/reagents/low-stock   - Get low stock (STRING STATUS)
GET    /api/reagents/{code}/qty  - Get quantity (NO CACHE)
```

### Audit
```
GET    /api/audit/logs           - Get all logs (NO PAGINATION)
GET    /api/audit/logs/action/{action} - Get by action (NO INDEX)
```

## 🔍 Example Violations

### Violation 1: Hardcoded IP Address
```java
// LabEquipment.java - CLOUD COMPATIBILITY VIOLATION
@Transient
private static final String EQUIPMENT_IP = "192.168.1.100";

@Transient
private static final int EQUIPMENT_PORT = 8888;
```

**Impact:** Blocks cloud deployment, auto-scaling, failover

### Violation 2: Local File System Only
```java
// SpecimenService.java - CLOUD COMPATIBILITY VIOLATION
private void storeSpecimenLocally(Specimen specimen, byte[] imageData) {
    String filePath = "C:\\lab\\specimens\\" + specimen.getSpecimenId() + ".img";
    specimen.setLocalStoragePath(filePath);
}
```

**Impact:** Cannot use cloud blob storage, scales poorly

### Violation 3: String Concatenation
```java
// LabResultService.java - CODE SUSTAINABILITY VIOLATION
String report = "";
report += "Test Name: " + result.getTest().getTestName() + "\n";
report += "Result Value: " + result.getResultValue() + "\n";
// ... many more concatenations
```

**Impact:** Poor performance, creates many String objects

### Violation 4: No Pagination
```java
// SpecimenRepository.java - CODE SUSTAINABILITY VIOLATION
List<Specimen> findByStatus(String status);  // Could return millions of records
```

**Impact:** OOM errors, poor performance with large datasets

### Violation 5: N+1 Query Problem
```java
// SpecimenService.java - CODE SUSTAINABILITY VIOLATION
for (Specimen specimen : specimens) {
    int testCount = specimen.getTests().size();  // Database query per specimen!
}
```

**Impact:** 1,000 specimens = 1,001 database queries

### Violation 6: Database-Specific Features
```java
// application.properties - DATABASE DEPENDENCY VIOLATION
spring.datasource.url=jdbc:h2:mem:testdb;MODE=Oracle
```

**Impact:** Locked into Oracle, can't switch databases

## 📊 Violation Statistics

- **Total Violations:** 100
- **Cloud Compatibility:** 20
- **Software Portability:** 20
- **Code Sustainability:** 20
- **Database Dependency:** 20

## 🧪 Testing with Concierto Modernize

### Step 1: Upload Rules
Upload the 4 CSV rule files to Concierto:
```
- HEALTHCARE_JAVA_RULES_CLOUD_COMPATIBILITY_20.csv
- HEALTHCARE_JAVA_RULES_SOFTWARE_PORTABILITY_20.csv
- HEALTHCARE_JAVA_RULES_CODE_SUSTAINABILITY_20.csv
- HEALTHCARE_JAVA_RULES_DATABASE_DEPENDENCY_20.csv
```

### Step 2: Run Analysis
Point Concierto to this source code directory

### Step 3: Review Violations
Concierto should detect violations matching the rules

## 📈 Expected Violations Detected

- 18-20 Cloud Compatibility violations
- 18-20 Software Portability violations
- 18-20 Code Sustainability violations
- 18-20 Database Dependency violations

## 🔧 Technology Stack

- **Framework:** Spring Boot 3.1.5
- **Java Version:** 17
- **Database:** H2 in-memory (with Oracle compatibility mode)
- **Build Tool:** Maven 3.8+
- **Dependencies:**
  - Spring Boot Web Starter
  - Spring Data JPA
  - H2 Database
  - Lombok
  - Guava (caching)
  - Jackson (JSON)
  - GSON (serialization)

## 📝 Code Metrics

- **Total Lines of Code:** ~8,000
- **Number of Classes:** 14
- **Number of Files:** 8 Java files + 2 property files
- **Database Tables:** 11
- **API Endpoints:** 30+

## ⚠️ Production Use

**DO NOT USE IN PRODUCTION**

This application is intentionally designed with violations. It serves as a demo/training tool for:
- Learning anti-patterns
- Training on cloud modernization
- Rule validation
- Architecture review

## 🔗 Integration

This application is built to complement the **100 Healthcare Java Rules** covering:
- Cloud Compatibility
- Software Portability
- Code Sustainability
- Database Dependency

All violations in this application should match patterns detected by those rules.

## 📚 Documentation

- `VIOLATIONS_ANALYSIS.md` - Detailed analysis of each violation
- Rule descriptions in CSV files (2000+ characters each)

## 👨‍💼 Support

For questions about violations or Concierto Modernize rules:
- Review the rule descriptions
- Check violation examples in source code
- Analyze the impact of each violation
- Plan remediation strategies

## 📄 License

Educational/Demo Use Only

---

**Created:** June 2026  
**For:** Concierto Modernize  
**Version:** 1.0.0  
**Status:** ✅ Fully Functional with Intentional Violations
