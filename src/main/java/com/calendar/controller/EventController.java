package com.calendar.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.calendar.domain.Event;
import com.calendar.repository.EventRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/events")
public class EventController {

    private EventRepository eventRepository;

    EventController(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody Event event){
        Event newEvent = eventRepository.save(event);

        return ResponseEntity.ok(newEvent);
    }

    @GetMapping
    public ResponseEntity<List<Event>> getEvents(
        @RequestParam String username,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ){
        List<Event> events = eventRepository.findByUsernameAndDateBetween(username, start, end);

        return ResponseEntity.ok(events);
    }
}
