package com.calendar.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.calendar.domain.Event;
import com.calendar.repository.EventRepository;
import com.calendar.service.EventService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final EventRepository repository;

    EventController(EventRepository repository, EventService eventService) {
        this.repository = repository;
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = repository.findAll();
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Event event) {
        if (event.getCreatedBy() != null) {
            Event newEvent = repository.save(event);
            return ResponseEntity.ok(newEvent);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Does not login.");

    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        Event event = repository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        
        return ResponseEntity.ok(event);
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event updateEvent) {
        Event event = eventService.updateEvent(id, updateEvent);
        
        return ResponseEntity.ok(event);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteEvent(@RequestParam Long id) {
        try {
            repository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "刪除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "刪除失敗，請確認id是否正確");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
