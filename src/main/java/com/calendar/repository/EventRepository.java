package com.calendar.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.calendar.domain.Event;

public interface EventRepository extends JpaRepository<Event, Long>{

    @Query("SELECT e FROM Event e " +
           "WHERE e.createdBy = :createdBy " +
           "AND e.startTime BETWEEN :start AND :end")
    List<Event> findEventsInRange(@Param("createdBy") String createdBy,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);

    @Query("SELECT e FROM Event e " +
           "WHERE e.startTime >= :startDate " +
           "AND e.startTime < :endDate " +
           "ORDER BY e.startTime ASC")
    List<Event> findEventsByDateRange(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);
}
