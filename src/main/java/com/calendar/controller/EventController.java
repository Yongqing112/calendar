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

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/events")
public class EventController {

    private EventRepository eventRepository;

    EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Event event) {
        if (event.getUsername() != null) {
            Event newEvent = eventRepository.save(event);
            return ResponseEntity.ok(newEvent);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Does not login.");

    }

    @GetMapping
    public ResponseEntity<List<Event>> getEvents(
            @RequestParam String username, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteEvent(@RequestParam Long id) {
        try {
            eventRepository.deleteById(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "刪除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "刪除失敗");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
