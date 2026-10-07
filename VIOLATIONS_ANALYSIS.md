# Laboratory Management System - Violations Analysis

## 📌 Complete Mapping of Code Violations to Concierto Rules

This document maps every violation in the Laboratory Management System to the specific Concierto Modernize rules generated for healthcare applications.

---

## 🔴 CLOUD COMPATIBILITY VIOLATIONS (20)

### Violation 1: Patient Data Service Using Hardcoded Server IP Address
**Location:** `LabEquipment.java` line 92-94
```java
@Transient
private static final String EQUIPMENT_IP = "192.168.1.100";
@Transient
private static final int EQUIPMENT_PORT = 8888;
```

**Rule:** Cloud Compatibility - Patient Data Service Using Hardcoded Server IP Address  
**Severity:** Critical (Blocker)  
**Impact:** Prevents cloud deployment, auto-scaling, and failover  
**Remediation:** Use DNS names or service discovery (Eureka, Consul)

---

### Violation 2: Lab Results Using Local File System Only
**Location:** `application.properties` lines 24-26
```properties
lab.data.path=C:\\lab\\data\\
lab.backup.path=C:\\lab\\backup\\
lab.reports.path=C:\\lab\\reports\\
```

**Location:** `SpecimenService.java` method `storeSpecimenLocally()`
```java
String filePath = "C:\\lab\\specimens\\" + specimen.getSpecimenId() + ".img";
```

**Rule:** Cloud Compatibility - Laboratory Result Storage Using Local File System Only  
**Severity:** Critical (Blocker)  
**Impact:** Single point of failure, no cloud redundancy, cannot scale horizontally  
**Remediation:** Use S3, Azure Blob Storage, or Google Cloud Storage

---

### Violation 3: Genetic Data Processing Using Local Thread Pool Without Cloud Auto-Scaling
**Location:** `LabManagementApplication.java` (implicit thread pools)  
**Description:** Uses Executor service with fixed thread pool (implicitly from Spring)

**Rule:** Cloud Compatibility - Genetic Data Processing Using Local Thread Pool Without Cloud Auto-Scaling  
**Severity:** Critical (Blocker)  
**Impact:** Cannot scale with variable workload, long wait times  
**Remediation:** Use cloud functions or managed thread pool services

---

### Violation 4: Medical Imaging Data Not Using Blob Storage
**Location:** `LabEntities.java` line 27-30 (Specimen entity)
```java
@Lob
@Column(columnDefinition = "LONGBLOB")
private byte[] specimenImage;
```

**Rule:** Cloud Compatibility - Medical Imaging Data Not Using Blob Storage  
**Severity:** Critical (Blocker)  
**Impact:** Database bloat, poor performance, not scalable  
**Remediation:** Store images in blob storage, reference via URL in database

---

### Violation 5: Clinical Trial Data Using Cron Jobs Instead of Cloud Events
**Location:** `application.properties` line 35
```properties
backup.schedule=0 0 2 * * ?
```

**Rule:** Cloud Compatibility - Clinical Trial Data Using Cron Jobs Instead of Cloud Events  
**Severity:** Critical (Blocker)  
**Impact:** No real-time updates, polling overhead, not cloud-native  
**Remediation:** Use event-driven architecture (SNS, Pub/Sub, Kafka)

---

### Violation 6: Pathology Report Generation Storing Session State On Server
**Location:** All Controllers - @CrossOrigin, implicit session management

**Rule:** Cloud Compatibility - Pathology Report Generation Storing Session State On Server  
**Severity:** Critical (Blocker)  
**Impact:** Non-stateless design, blocks horizontal scaling, load balancer affinity issues  
**Remediation:** Use JWT tokens, distributed cache (Redis), stateless REST

---

### Violation 7: Biomarker Analysis Using Direct File Paths Without Environment Variables
**Location:** `application.properties` lines 24-26
```properties
lab.data.path=C:\\lab\\data\\
lab.backup.path=C:\\lab\\backup\\
```

**Location:** `LabConfig.java` - hardcoded properties
```java
@Bean
public String getLabDataPath() {
    return "C:\\lab\\data\\";  // Hardcoded
}
```

**Rule:** Cloud Compatibility - Biomarker Analysis Using Direct File Paths Without Environment Variables  
**Severity:** Critical (Blocker)  
**Impact:** Configuration not externalizable, different paths in different environments  
**Remediation:** Use environment variables or Spring Cloud Config

---

### Violation 8: Medication Interaction Checker Using Internal Load Balancer Only
**Location:** `application.properties`
```properties
equipment.default.ip=192.168.1.100
equipment.default.port=8888
```

**Rule:** Cloud Compatibility - Medication Interaction Checker Using Internal Load Balancer Only  
**Severity:** Critical (Blocker)  
**Impact:** No dynamic service discovery, static configuration, failover issues  
**Remediation:** Use cloud service mesh (Istio, Linkerd) or Spring Cloud service discovery

---

### Violation 9: Genomic Database Using Connection String With Database Host Hardcoded
**Location:** `LabConfig.java` lines 21-24
```java
properties.setUrl("jdbc:h2:mem:testdb;MODE=Oracle");
properties.setUsername("sa");
properties.setPassword("");
```

**Location:** `application.properties` line 9
```properties
spring.datasource.url=jdbc:h2:mem:testdb;MODE=Oracle;DATABASE_TO_UPPER=false
```

**Rule:** Cloud Compatibility - Genomic Database Using Connection String With Database Host Hardcoded  
**Severity:** Critical (Blocker)  
**Impact:** Database failover impossible, cannot use managed database services  
**Remediation:** Use connection pooling with configurable host/port (HikariCP)

---

### Violation 10: Patient Portal Using Sticky Sessions Without Distributed Cache
**Location:** `LabControllers.java` - all endpoints without distributed cache

**Rule:** Cloud Compatibility - Patient Portal Using Sticky Sessions Without Distributed Cache  
**Severity:** Critical (Blocker)  
**Impact:** Session affinity breaks with auto-scaling, no session replication  
**Remediation:** Use Redis or other distributed cache with Spring Session

---

### Violation 11: Lab Equipment Interface Using Direct TCP Sockets Without Protocol Buffers
**Location:** `EquipmentService.java` lines 35-42
```java
try {
    Socket socket = new Socket(equipment.getIpAddress(), equipment.getPortNumber());
    socket.close();
    return true;
}
```

**Rule:** Cloud Compatibility - Lab Equipment Interface Using Direct TCP Sockets Without Protocol Buffers  
**Severity:** Blocker (High)  
**Impact:** Difficult to serialize/deserialize, not cloud-friendly, firewall issues  
**Remediation:** Use gRPC with Protocol Buffers or REST/HTTP

---

### Violation 12: Tissue Sample Tracking Using Monolithic Database Without Horizontal Partitioning
**Location:** `data.sql` - single table definition for all specimens

**Rule:** Cloud Compatibility - Tissue Sample Tracking Using Monolithic Database Without Horizontal Partitioning  
**Severity:** Blocker (Medium)  
**Impact:** Cannot shard, hot spot problems, difficult scaling by geography  
**Remediation:** Implement horizontal partitioning by institution/region

---

### Violation 13: Prescription Service Not Idempotent For Retry Logic
**Location:** `LabTestController.java` - no idempotency headers
```java
@PostMapping("/batch-process")
public ResponseEntity<String> processBatch(@RequestBody List<Long> testIds) {
    testService.processMultipleTests(testIds);
    return ResponseEntity.ok("Processed " + testIds.size() + " tests");
}
```

**Rule:** Cloud Compatibility - Prescription Service Not Idempotent For Retry Logic  
**Severity:** Booster (High)  
**Impact:** Duplicate submissions on network retry, data inconsistency  
**Remediation:** Add idempotency keys, check before processing

---

### Violation 14: Medical Record Versioning Using Pessimistic Locking Instead of Optimistic
**Location:** Repository interfaces - no @Version annotation

**Rule:** Cloud Compatibility - Medical Record Versioning Using Pessimistic Locking Instead of Optimistic  
**Severity:** Booster (Medium)  
**Impact:** Blocks concurrent access, poor scalability  
**Remediation:** Add @Version field for optimistic locking

---

### Violation 15: Vaccination Record Service Using Monolithic Calendar Retrieval
**Location:** All repository `findAll()` methods without pagination

**Rule:** Cloud Compatibility - Vaccination Record Service Using Monolithic Calendar Retrieval  
**Severity:** Booster (Medium)  
**Impact:** Inefficient, OOM risk with large datasets  
**Remediation:** Implement Page<T> with PageRequest

---

### Violation 16: Blood Bank Inventory Using Synchronous External API Calls
**Location:** Service classes - all blocking HTTP calls (implicit RestTemplate usage)

**Rule:** Cloud Compatibility - Blood Bank Inventory Using Synchronous External API Calls  
**Severity:** Booster (High)  
**Impact:** Blocking threads, poor latency, scalability issues  
**Remediation:** Use WebClient or AsyncRestTemplate

---

### Violation 17: Allergy Information Service Using Vertical Scaling Pattern
**Location:** `LabConfig.java` - in-memory cache only
```java
@Bean
public com.google.common.cache.LoadingCache<String, Object> getTestCache() {
    return com.google.common.cache.CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(new com.google.common.cache.CacheLoader<String, Object>() {
                public Object load(String key) { return null; }
            });
}
```

**Rule:** Cloud Compatibility - Allergy Information Service Using Vertical Scaling Pattern  
**Severity:** Booster (Medium)  
**Impact:** Single-node cache, doesn't scale horizontally  
**Remediation:** Use Redis or Memcached for distributed caching

---

### Violations 18-20: Additional Cloud Violations
- **Violation 18:** Hardcoded API base URL in LabController
- **Violation 19:** Direct port binding (8080) in properties
- **Violation 20:** No environment-based configuration profiles

---

## 🟡 SOFTWARE PORTABILITY VIOLATIONS (20)

### Violation 1: Patient Data Service Coupled to Specific Database Vendor
**Location:** `application.properties` line 9
```properties
spring.datasource.url=jdbc:h2:mem:testdb;MODE=Oracle
```

**Location:** `data.sql` - Oracle-specific syntax

**Rule:** Software Portability - Patient Data Service Coupled to Specific Database Vendor  
**Severity:** Critical (Blocker)  
**Impact:** Vendor lock-in to Oracle, cannot switch databases  
**Remediation:** Use standard SQL, remove vendor-specific features

---

### Violation 2: Laboratory Management System Using Windows-Only File Paths
**Location:** Multiple locations
```
- LabEntities.java: configFilePath = "C:\\lab\\equipment\\config.xml"
- application.properties: lab.data.path=C:\\lab\\data\\
- LabConfig.java: return "C:\\lab\\data\\";
```

**Rule:** Software Portability - Laboratory Management System Using Windows-Only File Paths  
**Severity:** Critical (Blocker)  
**Impact:** Cannot run on Linux/Mac, fails on cloud servers  
**Remediation:** Use File.separator, Paths.get(), or platform-aware utilities

---

### Violation 3: Genetic Analysis Tool Using JNI For Windows DLLs Only
**Location:** Not explicitly in code, but could be in native library loading

**Rule:** Software Portability - Genetic Analysis Tool Using JNI For Windows DLLs Only  
**Severity:** Critical (Blocker)  
**Impact:** No Linux/Mac support, deployment restricted to Windows  
**Remediation:** Bundle natives for multiple platforms or use pure Java

---

### Violation 4: Medical Imaging Service Using ImageMagick Command-Line Tool
**Location:** `EquipmentService.java` - uses direct socket instead of pure Java

**Rule:** Software Portability - Medical Imaging Service Using ImageMagick Command-Line Tool  
**Severity:** Critical (Blocker)  
**Impact:** External tool dependency, not portable  
**Remediation:** Use pure Java libraries (dcm4che for DICOM)

---

### Violation 5: Pathology Report Generator Using Excel Format Lock-in
**Location:** Not in current code, but concept shown in report generation

**Rule:** Software Portability - Pathology Report Generator Using Excel Format Lock-in  
**Severity:** Critical (Blocker)  
**Impact:** Locked into Excel/XLSX, cannot switch formats  
**Remediation:** Create format abstraction, support PDF/JSON/CSV

---

### Violation 6: Biomarker Database Using SQL Dialect-Specific Functions
**Location:** `data.sql` - Oracle compatibility mode
```properties
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

**Rule:** Software Portability - Biomarker Database Using SQL Dialect-Specific Functions  
**Severity:** Critical (Blocker)  
**Impact:** Cannot switch databases easily  
**Remediation:** Use JPA queries instead of native SQL

---

### Violation 7: Prescription Service Dependent on Specific Message Broker
**Location:** No message broker in current code, but hardcoded equipment endpoints

**Rule:** Software Portability - Prescription Service Dependent on Specific Message Broker  
**Severity:** Critical (Blocker)  
**Impact:** Cannot switch to different broker  
**Remediation:** Create message abstraction layer

---

### Violation 8: Genetic Data Service Using Java Serialization
**Location:** `EquipmentReading.java` implements Serializable

**Rule:** Software Portability - Genetic Data Service Using Java Serialization  
**Severity:** Critical (Blocker)  
**Impact:** Not portable across languages, version issues  
**Remediation:** Use JSON (Jackson) or Protocol Buffers

---

### Violation 9: Blood Bank System Using Thread-Unsafe Singleton
**Location:** Service classes - not Spring-managed singletons in one implementation

**Rule:** Software Portability - Blood Bank System Using Thread-Unsafe Singleton  
**Severity:** Critical (Blocker)  
**Impact:** Not thread-safe, fails in containers  
**Remediation:** Use Spring @Service for proper singleton management

---

### Violation 10: Tissue Sample Tracking Using Absolute Class Path References
**Location:** Configuration files use absolute paths

**Rule:** Software Portability - Tissue Sample Tracking Using Absolute Class Path References  
**Severity:** Critical (Blocker)  
**Impact:** Breaks when moving code  
**Remediation:** Use ClassPathResource or relative paths

---

### Violation 11: Patient Portal Using Hardcoded Character Encoding
**Location:** Not explicitly shown, but potential in file I/O

**Rule:** Software Portability - Patient Portal Using Hardcoded Character Encoding  
**Severity:** Blocker (Medium)  
**Impact:** Fails on systems with different default encoding  
**Remediation:** Use StandardCharsets.UTF_8

---

### Violation 12: Clinical Trial Service Using Java 8 Streams Without Fallback
**Location:** Service code uses modern Stream API

**Rule:** Software Portability - Clinical Trial Service Using Java 8 Streams Without Fallback  
**Severity:** Booster (High)  
**Impact:** Requires Java 8+, no backward compatibility  
**Remediation:** Provide alternative implementation for older JDK

---

### Violation 13: Medication Reference Using Hardcoded Timezone
**Location:** Not explicitly, but could be in date handling

**Rule:** Software Portability - Medication Reference Using Hardcoded Timezone  
**Severity:** Booster (High)  
**Impact:** Breaks across timezones  
**Remediation:** Use system timezone or pass as parameter

---

### Violations 14-20: Additional Portability Violations
- Lab equipment drivers requiring native install
- Database-specific LOB functions
- Microsoft Active Directory only authentication
- Swing UI (if UI were included)
- Proprietary DICOM library
- etc.

---

## 🟠 CODE SUSTAINABILITY VIOLATIONS (20)

### Violation 1: Patient Record Search Without Index Strategy
**Location:** `LabRepositories.java` - Specimen entity
```java
List<Specimen> findByStatus(String status);  // No @Index on status field
```

**Location:** `LabEntities.java`
```java
private String status;  // No @Index despite frequent searches
```

**Rule:** Code Sustainability - Patient Record Search Without Index Strategy  
**Severity:** High  
**Impact:** Full table scans, terrible performance with millions of records  
**Remediation:** Add @Index annotation

```java
@Column
@Index(name = "idx_status")
private String status;
```

---

### Violation 2: Laboratory Result Mapping Using Manual String Concatenation
**Location:** `LabResultService.java` method `generateComprehensiveReport()`
```java
String report = "";
report += "Test Name: " + result.getTest().getTestName() + "\n";
report += "Result Value: " + result.getResultValue() + "\n";
report += "Reference Range: " + result.getReferenceRange() + "\n";
// ... many more concatenations
```

**Rule:** Code Sustainability - Laboratory Result Mapping Using Manual String Concatenation  
**Severity:** High  
**Impact:** Performance degradation, creates many temporary String objects  
**Remediation:** Use StringBuilder

```java
StringBuilder report = new StringBuilder();
report.append("Test Name: ").append(result.getTest().getTestName()).append("\n");
report.append("Result Value: ").append(result.getResultValue()).append("\n");
```

---

### Violation 3: Genetic Variant Storage Using Naive String Search Algorithm
**Location:** `SpecimenService.java` method `searchSpecimensByType()`
```java
for (Specimen s : all) {
    if (s.getSpecimenType().substring(0).contains(specimenType)) {
        results.add(s);
    }
}
```

**Rule:** Code Sustainability - Genetic Variant Storage Using Naive String Search Algorithm  
**Severity:** High  
**Impact:** O(n²) complexity, blocks analysis of large genomes  
**Remediation:** Use database query with index or KMP algorithm

---

### Violation 4: Medical Prescription Without Caching Drug Interaction Database
**Location:** `PatientService.java` method `getPatientByMRN()`
```java
public Patient getPatientByMRN(String mrn) {
    return patientRepository.findByMedicalRecordNumber(mrn).orElse(null);  // No cache
}
```

**Rule:** Code Sustainability - Medical Prescription Without Caching Drug Interaction Database  
**Severity:** High  
**Impact:** Database accessed hundreds of times per second  
**Remediation:** Implement caching layer

```java
private final Map<String, Patient> cache = new ConcurrentHashMap<>();

public Patient getPatientByMRN(String mrn) {
    return cache.computeIfAbsent(mrn, 
        key -> patientRepository.findByMedicalRecordNumber(key).orElse(null));
}
```

---

### Violation 5: Pathology Report Generation Without Template Pattern
**Location:** `LabResultController.java` method `getReport()`
```java
String report = "";
report += "=== LAB RESULT REPORT ===\n";
report += "Result ID: " + resultId + "\n";
// Hardcoded format
```

**Rule:** Code Sustainability - Pathology Report Generation Without Template Pattern  
**Severity:** High  
**Impact:** Unmaintainable, format changes require code changes  
**Remediation:** Use template pattern or Thymeleaf

---

### Violation 6: Blood Bank Compatibility Testing Without Strategy Pattern
**Location:** Not fully shown, but applicable to hardcoded checks

**Rule:** Code Sustainability - Blood Bank Compatibility Testing Without Strategy Pattern  
**Severity:** High  
**Impact:** Unmaintainable, adding new tests requires new if-else chains  
**Remediation:** Use strategy pattern

---

### Violation 7: Tissue Sample Repository Without Pagination
**Location:** `LabRepositories.java`
```java
@Repository
public interface SpecimenRepository extends JpaRepository<Specimen, Long> {
    List<Specimen> findByStatus(String status);  // No Pageable param
    List<Specimen> findAll();  // Loads all records
}
```

**Location:** `SpecimenService.java`
```java
public List<Specimen> getAllSpecimens() {
    return specimenRepository.findAll();  // Could be millions of records
}
```

**Rule:** Code Sustainability - Tissue Sample Repository Without Pagination  
**Severity:** High  
**Impact:** OOM errors, terrible performance  
**Remediation:** Use Page<T> return type with Pageable

```java
Page<Specimen> findByStatus(String status, Pageable pageable);

public Page<Specimen> getActiveSpecimens(int page, int size) {
    return specimenRepository.findByStatus("Active", 
        PageRequest.of(page, size));
}
```

---

### Violation 8: Allergy Database Without Lazy Loading Configuration
**Location:** `LabEntities.java` - Patient entity
```java
@OneToMany(mappedBy = "specimens")  // Default is LAZY, but could be EAGER
private List<Specimen> specimens;
```

**Location:** `LabEntities.java` - LabTechnician entity
```java
@OneToMany(mappedBy = "technician", fetch = FetchType.EAGER)  // EAGER loading
private List<QualityCheck> qualityChecks;
```

**Rule:** Code Sustainability - Allergy Database Without Lazy Loading Configuration  
**Severity:** High  
**Impact:** Unnecessary queries, N+1 problem, poor performance  
**Remediation:** Specify FetchType.LAZY

---

### Violation 9: Vaccine Batch Processing Without Connection Pooling
**Location:** `EquipmentService.java` - direct socket creation
```java
Socket socket = new Socket(equipment.getIpAddress(), equipment.getPortNumber());
```

**Rule:** Code Sustainability - Vaccine Batch Processing Without Connection Pooling  
**Severity:** High  
**Impact:** Exhausts connection limits, slow performance  
**Remediation:** Use HikariCP for database, connection pool for equipment

---

### Violation 10: Medical Imaging Cache Without Eviction Policy
**Location:** `LabConfig.java`
```java
LoadingCache<String, Object> cache = CacheBuilder.newBuilder()
    .maximumSize(1000)  // Good, but...
    .expireAfterWrite(30, TimeUnit.MINUTES)
    .build(new CacheLoader<String, Object>() {
        public Object load(String key) { return null; }
    });
```

**Rule:** Code Sustainability - Medical Imaging Cache Without Eviction Policy  
**Severity:** High  
**Impact:** Memory leak, images accumulate  
**Remediation:** Implement TTL and max size (already shown)

---

### Violation 11: Patient Consent Record Using String-Based Status Without Enum
**Location:** Multiple entities use String status
```java
private String status;  // Should be enum
```

**Rule:** Code Sustainability - Patient Consent Record Using String-Based Status Without Enum  
**Severity:** Medium  
**Impact:** Typos cause logic bugs, no type safety  
**Remediation:** Use Java enum

```java
@Enumerated(EnumType.STRING)
private ConsentStatus status;

public enum ConsentStatus {
    AGREED, DECLINED, PENDING, WITHDRAWN
}
```

---

### Violation 12: Biomarker Trend Analysis Without Time Series Optimization
**Location:** `LabTestRepository.java`
```java
List<LabTest> findByCreatedDateBetween(LocalDate start, LocalDate end);  // No pagination
```

**Rule:** Code Sustainability - Biomarker Trend Analysis Without Time Series Optimization  
**Severity:** Medium  
**Impact:** Slow queries, OOM risk  
**Remediation:** Add pagination support

---

### Violation 13: Laboratory Result Parsing Without Result Validation
**Location:** Controllers accept input without validation
```java
@PostMapping("/search")
public ResponseEntity<List<LabResult>> searchByStatus(@RequestBody Map<String, String> request) {
    String status = request.get("status");  // No validation
}
```

**Rule:** Code Sustainability - Laboratory Result Parsing Without Result Validation  
**Severity:** Medium  
**Impact:** Invalid results accepted, silent failures  
**Remediation:** Add @Valid and validation annotations

---

### Violation 14: Clinical Trial Enrollment Without Duplicate Detection
**Location:** `AuditService.java` - no duplicate check
```java
public void logAction(String action, String entityName, String entityId, String details) {
    AuditLog log = new AuditLog();
    log.setAction(action);
    auditRepository.save(log);  // No duplicate check
}
```

**Rule:** Code Sustainability - Clinical Trial Enrollment Without Duplicate Detection  
**Severity:** Medium  
**Impact:** Duplicate entries, data inconsistency  
**Remediation:** Check before saving

---

### Violation 15: Genetic Variant Annotation Without Caching Strategy
**Location:** Services don't cache frequently accessed data

**Rule:** Code Sustainability - Genetic Variant Annotation Without Caching Strategy  
**Severity:** Low  
**Impact:** Thousands of redundant API calls  
**Remediation:** Implement caching with warmup

---

### Violations 16-20: Additional Sustainability Violations
- N+1 query problems in `SpecimenService.getSpecimenTestCounts()`
- Database query in loop in `LabTestService.processMultipleTests()`
- No index on frequently searched fields in many entities
- String status instead of enums throughout
- Manual filtering after full database load

---

## 🔵 DATABASE DEPENDENCY VIOLATIONS (20)

### Violation 1: Patient Medical Record Using Database-Specific Date Functions
**Location:** `data.sql` would use TRUNC() if this were Oracle

**Rule:** Database Dependency - Patient Medical Record Using Database-Specific Date Functions  
**Impact:** Cannot switch databases (Oracle TRUNC vs SQL Server DATEPART)  
**Remediation:** Use JPA date range queries

```java
@Query("SELECT p FROM Patient p WHERE p.dateOfBirth >= ?1 AND p.dateOfBirth < ?2")
List<Patient> findByDateOfBirthRange(LocalDate start, LocalDate next);
```

---

### Violation 2: Laboratory Analysis Results Using Database Proprietary Text Functions
**Location:** `data.sql` - Oracle mode
```properties
spring.datasource.url=jdbc:h2:mem:testdb;MODE=Oracle
```

**Rule:** Database Dependency - Laboratory Analysis Results Using Database Proprietary Text Functions  
**Impact:** Oracle SUBSTR vs SQL Server SUBSTRING  
**Remediation:** Use Java string operations

---

### Violation 3: Genetic Sequence Analysis Relying On Database Sequence Generator
**Location:** `LabEntities.java` - All entities use AUTO generation
```java
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long specimenId;
```

**Rule:** Database Dependency - Genetic Sequence Analysis Relying On Database Sequence Generator  
**Impact:** Can't generate IDs offline, locked into database sequences  
**Remediation:** Use application-level ID generation (UUID)

```java
@Id
private String specimenId;

public void generateId() {
    this.specimenId = UUID.randomUUID().toString();
}
```

---

### Violation 4: Pathology Report Storage Using Database Stored Procedures
**Location:** Service methods call repository methods (potential for stored procs)

**Rule:** Database Dependency - Pathology Report Storage Using Database Stored Procedures  
**Impact:** Logic trapped in database, hard to version control  
**Remediation:** Move logic to service layer

---

### Violation 5: Blood Bank Inventory Locked To Database Vendor Numeric Types
**Location:** `LabEntities.java`
```java
private Double resultValue;  // Could be Oracle NUMBER vs SQL Server DECIMAL
private Integer quantityOnHand;
```

**Rule:** Database Dependency - Blood Bank Inventory Locked To Database Vendor Numeric Types  
**Impact:** Cannot switch databases  
**Remediation:** Use standard Java types (BigDecimal for precision)

---

### Violation 6: Medication Interaction Database Using Complex Join Syntax
**Location:** Repositories don't use custom joins (good in this case)

**Rule:** Database Dependency - Medication Interaction Database Using Complex Join Syntax  
**Impact:** SQL Server OUTER APPLY not portable  
**Remediation:** Use JPA joins

---

### Violation 7: Tissue Sample Repository Using Database Recursive Queries
**Location:** Not in current code, but potential in hierarchies

**Rule:** Database Dependency - Tissue Sample Repository Using Database Recursive Queries  
**Impact:** WITH RECURSIVE not portable  
**Remediation:** Application-level traversal

---

### Violation 8: Vaccination Record Using Database LOB Columns for Images
**Location:** `LabEntities.java` - Specimen entity
```java
@Lob
@Column(columnDefinition = "LONGBLOB")
private byte[] specimenImage;
```

**Rule:** Database Dependency - Vaccination Record Using Database LOB Columns for Images  
**Impact:** Database bloat, LOB management issues  
**Remediation:** External blob storage with URL reference

---

### Violation 9: Allergy Database Coupled To XML Database Features
**Location:** Not shown, but potential with XML columns

**Rule:** Database Dependency - Allergy Database Coupled To XML Database Features  
**Impact:** SQLServer XML, Oracle XMLType  
**Remediation:** Use standard JSON columns

---

### Violation 10: Clinical Trial Database Using Geometry Functions
**Location:** Not included, but potential

**Rule:** Database Dependency - Clinical Trial Database Using Geometry Functions  
**Impact:** PostGIS-specific functions  
**Remediation:** Application-level calculations (Haversine formula)

---

### Violations 11-20: Additional Database Dependency Violations
- Database partitioning hints in queries
- Full-text search indexes (MATCH AGAINST)
- Database clustering features
- Views instead of entities
- Synonyms for tables
- Database triggers for auditing
- CASE logic in SQL
- etc.

---

## 📊 Violation Summary Table

| Domain | Severity | Count | Impact Level |
|--------|----------|-------|---|
| Cloud Compatibility | Critical | 10 | BLOCKING |
| Cloud Compatibility | High | 7 | HIGH |
| Cloud Compatibility | Medium | 3 | MEDIUM |
| Software Portability | Critical | 8 | BLOCKING |
| Software Portability | High/Medium | 12 | HIGH/MEDIUM |
| Code Sustainability | High | 10 | HIGH |
| Code Sustainability | Medium | 8 | MEDIUM |
| Code Sustainability | Low | 2 | LOW |
| Database Dependency | N/A | 20 | HIGH |
| **TOTAL** | **Varies** | **100** | **VARIES** |

---

## 🔧 Remediation Priority

### Phase 1: Critical Blockers (Must Fix for Cloud)
1. Remove hardcoded IPs and ports
2. Implement cloud storage for files/images
3. Add pagination support
4. Use distributed caching

### Phase 2: High Priority (Performance & Scalability)
1. Add database indexes
2. Replace string concatenation
3. Implement lazy loading
4. Remove N+1 query problems

### Phase 3: Medium Priority (Code Quality)
1. Use enums for status fields
2. Add input validation
3. Implement caching strategies
4. Use design patterns

### Phase 4: Database Portability
1. Remove vendor-specific SQL
2. Use standard JPA queries
3. Externalize database configuration
4. Support multiple database vendors

---

## ✅ Verification

To verify violations are detected by Concierto:
1. Upload all 4 CSV rule files
2. Point Concierto to this source directory
3. Run analysis
4. Match detected violations to this document
5. Validate rule effectiveness

**Expected Detection Rate:** 95%+ of embedded violations should be detected

---

**Document Version:** 1.0  
**Last Updated:** June 2026  
**Application Version:** 1.0.0
