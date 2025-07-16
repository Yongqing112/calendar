package com.calendar.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.calendar.domain.Event;

public interface EventRepository extends JpaRepository<Event, Long>{
    List<Event> findByUsernameAndDateBetween(String username, LocalDate start, LocalDate end);
}
