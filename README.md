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