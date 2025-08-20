# Daily Check-in Backend

## Tech
- Java 17, Spring Boot 3
- MySQL + Liquibase
- Redis + Redisson (distributed lock)
- JPA/Hibernate
- Docker

## Run
```bash
docker compose up -d
mvn -q clean
mvn -q compile
mvn -q spring-boot:run