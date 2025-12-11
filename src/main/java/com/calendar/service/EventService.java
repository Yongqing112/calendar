package com.calendar.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.calendar.domain.Event;
import com.calendar.repository.EventRepository;

@Service
public class EventService {

	private EventRepository eventRepository;

	public EventService(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@Transactional
	public Event updateEvent(Long id, Event updateEvent){
		Event event = eventRepository.findById(id).get();
		event.setTitle(updateEvent.getTitle());
		event.setDescription(updateEvent.getDescription());
		event.setStartTime(updateEvent.getStartTime());
		event.setEndTime(updateEvent.getEndTime());
		return eventRepository.save(event);
	}

	/**
	 * Search events within a date range
	 * @param startDate Start date (inclusive)
	 * @param endDate End date (inclusive)
	 * @return List of events sorted by start time
	 */
	public List<Event> searchEventsByDateRange(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null) {
			throw new IllegalArgumentException("Start date and end date cannot be null");
		}
		
		if (startDate.isAfter(endDate)) {
			throw new IllegalArgumentException("Start date must be before or equal to end date");
		}
		
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
		
		return eventRepository.findEventsByDateRange(startDateTime, endDateTime);
	}
}
