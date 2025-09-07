# Brokerage Final Project

Features:
- BigDecimal for prices and TRY amounts
- DTOs with validation
- MapStruct included (annotation processor configured)
- BCrypt password hashing for customers
- JWT-based auth (login returns JWT). Admin login is "admin" with password from property or default 'adminpass'
- H2 in-memory DB for tests and demo

Run:
- Java 17+, Maven
- mvn -U clean package
- mvn spring-boot:run

Auth:
- POST /auth/login { "id":"cust1","password":"pass1" } -> {"token":"..."}
- Use Authorization: Bearer <token> for subsequent calls.

Notes:
- Demo only: improve secret handling, password policy, token revocation and storage for production.


## Added in update
- MapStruct mapper interfaces under `com.example.brokerage.mapper`.
- PL/SQL sample package in `db/oracle/brokerage_pkg.sql` (Oracle PL/SQL example for stored procedures that reserve/restore funds and modify assets). This is illustrative; adapt to your Oracle schema and sequences.
- Dockerfile and docker-compose.yml (uses Postgres in compose for easy local runs; Oracle is heavier and not provided as an image in compose).
- `application.yml` with profiles: dev (H2), test (Postgres), uat (Oracle placeholders), live (Oracle placeholders).

### Notes on PL/SQL vs PL/pgSQL
PL/SQL typically refers to Oracle's procedural language (PL/SQL). PostgreSQL uses PL/pgSQL. The provided `db/oracle/brokerage_pkg.sql` is Oracle PL/SQL example. If you prefer PL/pgSQL (Postgres), tell me and I will convert the scripts.

## Update: H2 Everywhere
- Removed Oracle/Postgres configs.
- Now all profiles (dev, test, uat, live) use H2 in-memory database.
- Removed PL/SQL scripts.


## Kustomer folder config
- Per-environment YAML files live under `src/main/resources/kcustomer/{dev,test,uat,live}.yml`.
- application.yml imports the profile-specific kcustomer file automatically via `spring.config.import`.

## Coverage
- JaCoCo plugin added to pom. Run `mvn test` to produce a coverage report under `target/site/jacoco`.

## Note on 100% coverage
- I added many unit and integration tests targeting service and controller layers to increase coverage. Achieving exact 100% may require incremental tests for any remaining small utility methods; run `mvn test` and check the report to see uncovered lines and I will add tests to reach 100% on demand.
