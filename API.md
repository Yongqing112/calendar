# 📡 Calendar System - API Documentation

Complete REST API documentation for the Calendar System event management endpoints.

## Base URL

```
http://localhost:8080
```

## Authentication

The API uses session-based authentication. The `createdBy` field in event creation represents the authenticated user. If `createdBy` is null or not provided, the request will be rejected with a 401 UNAUTHORIZED response.

## Error Handling

All endpoints return appropriate HTTP status codes:

| Status Code | Meaning |
|-------------|---------|
| 200 OK | Request successful |
| 201 Created | Resource created successfully |
| 400 Bad Request | Invalid request data |
| 401 Unauthorized | User not authenticated |
| 404 Not Found | Resource not found |
| 500 Server Error | Internal server error |

## Endpoints

### 1. Get All Events

Retrieve a list of all events from the calendar.

**Request:**
```http
GET /events
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": 1,
    "createdBy": "john.doe",
    "title": "Team Meeting",
    "description": "Weekly sync with the team",
    "startTime": "2025-12-01T10:00:00",
    "endTime": "2025-12-01T11:00:00",
    "event_type": "MEETING"
  },
  {
    "id": 2,
    "createdBy": "jane.smith",
    "title": "Project Deadline",
    "description": "Final submission for Q4 project",
    "startTime": "2025-12-05T17:00:00",
    "endTime": "2025-12-05T17:30:00",
    "event_type": "DEADLINE"
  }
]
```

**Example cURL:**
```bash
curl -X GET http://localhost:8080/events
```

**Example JavaScript/Fetch:**
```javascript
fetch('http://localhost:8080/events')
  .then(response => response.json())
  .then(data => console.log(data));
```

---

### 2. Create Event

Create a new calendar event. The `createdBy` field is required and represents the authenticated user.

**Request:**
```http
POST /events
Content-Type: application/json

{
  "createdBy": "john.doe",
  "title": "Team Meeting",
  "description": "Weekly sync with the team",
  "startTime": "2025-12-01T10:00:00",
  "endTime": "2025-12-01T11:00:00",
  "event_type": "MEETING"
}
```

**Required Fields:**
- `createdBy` (string) - The username of the user creating the event
- `title` (string) - Event title/name
- `startTime` (datetime) - Event start time (ISO 8601 format)
- `endTime` (datetime) - Event end time (ISO 8601 format)

**Optional Fields:**
- `description` (string) - Detailed event description
- `event_type` (string) - Category or type of event (e.g., MEETING, DEADLINE, REMINDER)

**Success Response (200 OK):**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 3,
  "createdBy": "john.doe",
  "title": "Team Meeting",
  "description": "Weekly sync with the team",
  "startTime": "2025-12-01T10:00:00",
  "endTime": "2025-12-01T11:00:00",
  "event_type": "MEETING"
}
```

**Unauthorized Response (401):**
```http
HTTP/1.1 401 UNAUTHORIZED
Content-Type: application/json

"Does not login."
```

**Example cURL:**
```bash
curl -X POST http://localhost:8080/events \
  -H "Content-Type: application/json" \
  -d '{
    "createdBy": "john.doe",
    "title": "Team Meeting",
    "description": "Weekly sync with the team",
    "startTime": "2025-12-01T10:00:00",
    "endTime": "2025-12-01T11:00:00",
    "event_type": "MEETING"
  }'
```

**Example JavaScript/Fetch:**
```javascript
const newEvent = {
  createdBy: "john.doe",
  title: "Team Meeting",
  description: "Weekly sync with the team",
  startTime: "2025-12-01T10:00:00",
  endTime: "2025-12-01T11:00:00",
  event_type: "MEETING"
};

fetch('http://localhost:8080/events', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(newEvent)
})
  .then(response => response.json())
  .then(data => console.log('Event created:', data));
```

---

### 3. Get Single Event

Retrieve a specific event by its ID.

**Request:**
```http
GET /events/{id}
```

**Path Parameters:**
- `id` (number) - The unique event ID

**Success Response (200 OK):**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "createdBy": "john.doe",
  "title": "Team Meeting",
  "description": "Weekly sync with the team",
  "startTime": "2025-12-01T10:00:00",
  "endTime": "2025-12-01T11:00:00",
  "event_type": "MEETING"
}
```

**Not Found Response (404):**
```http
HTTP/1.1 404 Not Found
```

**Example cURL:**
```bash
curl -X GET http://localhost:8080/events/1
```

**Example JavaScript/Fetch:**
```javascript
fetch('http://localhost:8080/events/1')
  .then(response => response.json())
  .then(data => console.log('Event:', data));
```

---

### 4. Update Event

Modify an existing event's details. Only the fields you want to update need to be provided.

**Request:**
```http
PUT /events/{id}
Content-Type: application/json

{
  "title": "Updated Meeting Title",
  "description": "Updated description",
  "startTime": "2025-12-02T10:00:00",
  "endTime": "2025-12-02T11:00:00"
}
```

**Path Parameters:**
- `id` (number) - The unique event ID

**Request Body (all fields optional):**
- `title` (string) - Updated event title
- `description` (string) - Updated event description
- `startTime` (datetime) - Updated start time
- `endTime` (datetime) - Updated end time
- `event_type` (string) - Updated event type

**Success Response (200 OK):**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "id": 1,
  "createdBy": "john.doe",
  "title": "Updated Meeting Title",
  "description": "Updated description",
  "startTime": "2025-12-02T10:00:00",
  "endTime": "2025-12-02T11:00:00",
  "event_type": "MEETING"
}
```

**Example cURL:**
```bash
curl -X PUT http://localhost:8080/events/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Meeting Title",
    "description": "Updated description",
    "startTime": "2025-12-02T10:00:00",
    "endTime": "2025-12-02T11:00:00"
  }'
```

**Example JavaScript/Fetch:**
```javascript
const updateData = {
  title: "Updated Meeting Title",
  description: "Updated description",
  startTime: "2025-12-02T10:00:00",
  endTime: "2025-12-02T11:00:00"
};

fetch('http://localhost:8080/events/1', {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(updateData)
})
  .then(response => response.json())
  .then(data => console.log('Event updated:', data));
```

---

### 5. Delete Event

Remove an event from the calendar.

**Request:**
```http
DELETE /events?id={id}
```

**Query Parameters:**
- `id` (number) - The unique event ID to delete

**Success Response (200 OK):**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "message": "刪除成功"
}
```

**Error Response (400 Bad Request):**
```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "message": "刪除失敗，請確認id是否正確"
}
```

**Example cURL:**
```bash
curl -X DELETE "http://localhost:8080/events?id=1"
```

**Example JavaScript/Fetch:**
```javascript
fetch('http://localhost:8080/events?id=1', {
  method: 'DELETE'
})
  .then(response => response.json())
  .then(data => console.log('Delete response:', data));
```

---

## Data Types

### Event Object

```json
{
  "id": 1,
  "createdBy": "string",
  "title": "string",
  "description": "string",
  "startTime": "2025-12-01T10:00:00",
  "endTime": "2025-12-01T11:00:00",
  "event_type": "string"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Unique event identifier (auto-generated) |
| `createdBy` | String | Username of the event creator |
| `title` | String | Event title/name |
| `description` | String | Detailed event description |
| `startTime` | LocalDateTime | Event start time (ISO 8601 format) |
| `endTime` | LocalDateTime | Event end time (ISO 8601 format) |
| `event_type` | String | Event category/type |

### DateTime Format

All datetime fields use ISO 8601 format:
```
YYYY-MM-DDTHH:MM:SS
```

Example:
```
2025-12-01T10:00:00
```

---

## Common Use Cases

### 1. Create Event with Current User

```javascript
const currentUser = localStorage.getItem('username'); // Get from session

fetch('http://localhost:8080/events', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    createdBy: currentUser,
    title: "My Event",
    description: "Event description",
    startTime: new Date().toISOString().split('.')[0],
    endTime: new Date(Date.now() + 3600000).toISOString().split('.')[0],
    event_type: "MEETING"
  })
});
```

### 2. Fetch and Display All Events

```javascript
async function loadAllEvents() {
  try {
    const response = await fetch('http://localhost:8080/events');
    const events = await response.json();
    
    events.forEach(event => {
      console.log(`${event.title} on ${event.startTime}`);
    });
  } catch (error) {
    console.error('Error loading events:', error);
  }
}
```

### 3. Update Event Time

```javascript
async function rescheduleEvent(eventId, newStartTime, newEndTime) {
  const response = await fetch(`http://localhost:8080/events/${eventId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      startTime: newStartTime,
      endTime: newEndTime
    })
  });
  
  return response.json();
}
```

---

## Rate Limiting

Currently, there is no rate limiting implemented. For production use, consider implementing rate limiting to prevent abuse.

## CORS Headers

The API includes the following CORS headers:

```
Access-Control-Allow-Origin: http://localhost:4200
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Credentials: true
```

---

## Testing with Postman

You can easily test these endpoints using Postman:

1. Create a new collection named "Calendar API"
2. Add requests for each endpoint:
   - GET /events
   - POST /events
   - GET /events/{id}
   - PUT /events/{id}
   - DELETE /events?id={id}

3. Set the base URL to `http://localhost:8080` in Postman variables

---

## Changelog

### Version 1.0.0 (Current)
- ✅ Complete CRUD operations for events
- ✅ Session-based authentication
- ✅ PostgreSQL persistence
- ✅ CORS support for Angular frontend
- ✅ Automatic database schema generation

---

## Support & Troubleshooting

### Event not creating (401 error)
- Ensure `createdBy` field is provided in the request
- Check that the user session is active

### CORS errors (403)
- Verify the frontend is running on `http://localhost:4200`
- Check browser console for detailed CORS error messages

### Database connection errors
- Ensure PostgreSQL is running and accessible
- Verify database credentials in `application.properties`
- Check that the `calendar` database exists

### Timestamp format errors
- Use ISO 8601 format: `YYYY-MM-DDTHH:MM:SS`
- Example: `2025-12-01T10:00:00`

For additional help, refer to the main [README.md](./README_NEW.md)
