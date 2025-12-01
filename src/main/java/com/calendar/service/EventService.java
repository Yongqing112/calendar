package com.calendar.service;

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
}
