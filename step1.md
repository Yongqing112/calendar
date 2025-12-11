了解 👍 既然你希望更簡單的設計，我幫你把 **第 1 點基礎事件管理與資料庫設計** 簡化成「可以直接跑、結構乾淨、但保留基本 CRUD 功能」的版本。這樣一來你可以先有一個可運行的 Spring Boot 專案骨架，之後再慢慢加上 **重複事件** 或 **假期** 等進階功能。

---

## 簡化版設計

### 資料表結構（SQL）

只保留最基本的欄位：

```sql
CREATE TABLE events (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  start_date TIMESTAMP NOT NULL,
  end_date TIMESTAMP,
  event_type VARCHAR(32) NOT NULL DEFAULT 'user_event'
);
```

---

### Maven 依賴（`pom.xml` 主要部分）

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>runtime</scope>
</dependency>
```

👉 我用 **H2 memory DB**（免安裝），方便測試。之後要換成 MySQL / PostgreSQL 只要改 `application.properties`。

---

### application.properties

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

### Event Entity

```java
package com.example.calendar.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String eventType; // user_event / holiday

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
}
```

---

### Repository

```java
package com.example.calendar.repository;

import com.example.calendar.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
```

---

### Service

```java
package com.example.calendar.service;

import com.example.calendar.model.Event;
import com.example.calendar.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public Event save(Event e) { return repo.save(e); }

    public List<Event> findAll() { return repo.findAll(); }

    public Event findById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public void delete(Long id) { repo.deleteById(id); }
}
```

---

### Controller

```java
package com.example.calendar.controller;

import com.example.calendar.model.Event;
import com.example.calendar.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<Event> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Event getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Event create(@RequestBody Event e) {
        return service.save(e);
    }

    @PutMapping("/{id}")
    public Event update(@PathVariable Long id, @RequestBody Event e) {
        Event existing = service.findById(id);
        existing.setTitle(e.getTitle());
        existing.setUserId(e.getUserId());
        existing.setStartDate(e.getStartDate());
        existing.setEndDate(e.getEndDate());
        existing.setEventType(e.getEventType());
        return service.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
```

---

## 使用方式

啟動 Spring Boot 後，你可以用 Postman / curl 測試：

1. 建立事件

   ```bash
   POST http://localhost:8080/api/events
   Content-Type: application/json

   {
     "userId": 1,
     "title": "Meet friend",
     "startDate": "2025-09-05T10:00:00",
     "endDate": "2025-09-05T12:00:00",
     "eventType": "user_event"
   }
   ```
2. 查詢全部事件

   ```bash
   GET http://localhost:8080/api/events
   ```

---

這樣就完成了「最小可運行」的 **基礎事件管理與資料庫設計** 🎉
之後要擴充「重複事件（recurring）」和「假期自動匯入」時，可以在這個骨架上加上 `recurringRule` 欄位與額外邏輯。

---

要不要我下一步幫你 **在這個簡化版上加上 recurringRule 欄位**（但先不實作展開，只先存起來），讓設計與你原本的 3 點能接得上？
