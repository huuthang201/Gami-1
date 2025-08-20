# Daily Check-in Backend
## Postman documentation
- [Postman Collection](https://www.postman.com/thangminh01/huuthang201/collection/v4h5i1f/wiinvent-test?action=share&creator=16881645&active-environment=16881645-04ea24ed-82c4-46ac-aba2-788db3e1ff44)
- [API Documentation](https://documenter.getpostman.com/view/16881645/2sB3BKG94s)

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