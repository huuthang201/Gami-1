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
```

## Redis
- Create distributed lock
```java
try {
    String lockKey = "lock:checkin:" + userId + ":" + today;
    RLock lock = redisson.getLock(lockKey);
    ...
    locked = lock.tryLock(3, TimeUnit.SECONDS);
    ...
} finally {
    if (locked) {
        try { lock.unlock(); } catch (Exception ignore) {}
    }
}
```

- Wait up to **3 seconds** to acquire the lock.  
- If interrupted → throw `ApiException` with code **LOCK_INTERRUPTED (409)**.  
- If lock cannot be acquired (`locked == false`) → throw `ApiException` with code **LOCK_FAILED (409)**.  
- If the lock is acquired successfully → always call `lock.unlock()`.  
- Use `try/catch` to **swallow unlock exceptions** so they don’t hide the original error.  