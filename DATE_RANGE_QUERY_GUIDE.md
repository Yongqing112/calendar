# Date Range Query Feature - 使用指南

**功能完成日期:** December 11, 2025  
**狀態:** ✅ 已實現並通過所有測試

---

## 📋 功能概述

新增端點允許你按日期範圍查詢事件。

---

## 🔗 API 端點

### 日期範圍查詢
```
GET /events/search?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
```

**必需參數:**
- `startDate` - 開始日期 (格式: YYYY-MM-DD，包含)
- `endDate` - 結束日期 (格式: YYYY-MM-DD，包含)

**響應:** JSON 陣列，按 `startTime` 升序排序

---

## 💡 使用範例

### 1. cURL 查詢

**查詢 2025年9月20日到2025年9月25日的所有事件:**
```bash
curl -X GET "http://localhost:8080/events/search?startDate=2025-09-20&endDate=2025-09-25" \
  -H "Content-Type: application/json"
```

**響應範例:**
```json
[
  {
    "id": 1,
    "title": "Team Meeting",
    "description": "Weekly sync",
    "createdBy": "john",
    "startTime": "2025-09-22T10:00:00",
    "endTime": "2025-09-22T11:00:00",
    "event_type": "Meeting"
  },
  {
    "id": 2,
    "title": "Project Planning",
    "description": "Q4 Planning",
    "createdBy": "jane",
    "startTime": "2025-09-23T14:00:00",
    "endTime": "2025-09-23T15:00:00",
    "event_type": "Planning"
  }
]
```

### 2. JavaScript/Fetch 範例

```javascript
const startDate = '2025-09-20';
const endDate = '2025-09-25';

fetch(`http://localhost:8080/events/search?startDate=${startDate}&endDate=${endDate}`)
  .then(response => response.json())
  .then(events => {
    console.log(`Found ${events.length} events`);
    events.forEach(event => {
      console.log(`${event.title} on ${event.startTime}`);
    });
  })
  .catch(error => console.error('Error:', error));
```

### 3. JavaScript (Async/Await)

```javascript
async function searchEvents(startDate, endDate) {
  try {
    const response = await fetch(
      `http://localhost:8080/events/search?startDate=${startDate}&endDate=${endDate}`
    );
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    const events = await response.json();
    return events;
  } catch (error) {
    console.error('Failed to search events:', error);
    return [];
  }
}

// 使用
const events = await searchEvents('2025-09-20', '2025-09-25');
console.log(events);
```

### 4. Postman 設定

**方法:** GET  
**URL:** `http://localhost:8080/events/search`

**Params Tab:**
| Key | Value |
|-----|-------|
| startDate | 2025-09-20 |
| endDate | 2025-09-25 |

---

## ✅ 驗證和錯誤處理

### 成功 (200)
```json
[
  { "id": 1, "title": "Event 1", ... },
  { "id": 2, "title": "Event 2", ... }
]
```

### 錯誤 (400) - 日期順序錯誤
```bash
curl -X GET "http://localhost:8080/events/search?startDate=2025-12-31&endDate=2025-01-01"
```

**響應:**
```json
{
  "error": "Start date must be before or equal to end date"
}
```

### 錯誤 (400) - 缺少參數
```bash
curl -X GET "http://localhost:8080/events/search?endDate=2025-09-25"
```

**響應:** 400 Bad Request

---

## 📝 重要事項

1. **日期格式:** 必須使用 ISO 8601 格式 (YYYY-MM-DD)
2. **日期範圍:** 包含 startDate 和 endDate
3. **排序:** 結果按 startTime 升序排列
4. **時區:** 使用 LocalDateTime（本地時區）
5. **查詢範圍:** startDate 00:00:00 至 endDate 23:59:59

---

## 🧪 測試覆蓋

### Service Layer Tests (EventServiceTest.java)
- ✅ testSearchEventsByDateRange_Success - 查詢成功
- ✅ testSearchEventsByDateRange_EmptyResult - 空結果
- ✅ testSearchEventsByDateRange_StartDateNull - 缺少開始日期
- ✅ testSearchEventsByDateRange_EndDateNull - 缺少結束日期
- ✅ testSearchEventsByDateRange_StartDateAfterEndDate - 日期順序錯誤
- ✅ testSearchEventsByDateRange_SameDateRange - 同一天查詢

### Controller Layer Tests (EventControllerTest.java)
- ✅ testSearchByDateRange_Success - 端點成功
- ✅ testSearchByDateRange_EmptyResult - 空結果
- ✅ testSearchByDateRange_InvalidDateRange - 無效日期
- ✅ testSearchByDateRange_MissingStartDate - 缺少開始日期
- ✅ testSearchByDateRange_MissingEndDate - 缺少結束日期

---

## 🔧 技術實現細節

### 1. EventRepository (Repository Layer)
```java
@Query("SELECT e FROM Event e " +
       "WHERE e.startTime >= :startDate " +
       "AND e.startTime < :endDate " +
       "ORDER BY e.startTime ASC")
List<Event> findEventsByDateRange(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);
```

### 2. EventService (Business Logic Layer)
```java
public List<Event> searchEventsByDateRange(LocalDate startDate, LocalDate endDate) {
    // 驗證日期不為空
    // 驗證 startDate <= endDate
    // 轉換為 LocalDateTime
    // 調用 Repository
    // 返回排序結果
}
```

### 3. EventController (REST Layer)
```java
@GetMapping("/search")
public ResponseEntity<?> searchByDateRange(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
```

---

## 📊 查詢性能

- 使用 JPQL 查詢，由 Hibernate 最佳化
- `startTime` 欄位應該建立索引以提高性能
- 建議的索引: `CREATE INDEX idx_event_starttime ON event(start_time);`

---

## 🚀 後續優化

### 計畫中的改進
1. **分頁支援** - 返回 Page<Event> 而不是 List<Event>
2. **排序選項** - 支援按 title, endTime 等排序
3. **過濾選項** - 同時按 createdBy, event_type 過濾
4. **緩存** - Redis 緩存查詢結果

---

## 📚 相關端點

| 端點 | 方法 | 功能 |
|------|------|------|
| `/events` | GET | 獲取所有事件 |
| `/events/search` | GET | 按日期範圍查詢 |
| `/events/{id}` | GET | 獲取單個事件 |
| `/events` | POST | 創建事件 |
| `/events/{id}` | PUT | 更新事件 |
| `/events?id={id}` | DELETE | 刪除事件 |

---

**建立日期:** December 11, 2025  
**最後更新:** December 11, 2025
