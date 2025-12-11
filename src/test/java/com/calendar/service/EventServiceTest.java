package com.calendar.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.calendar.domain.Event;
import com.calendar.repository.EventRepository;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private EventService eventService;

	private Event testEvent;
	private Event updateEvent;

	@BeforeEach
	void setUp() {
		testEvent = new Event();
		testEvent.setId(1L);
		testEvent.setTitle("Original Title");
		testEvent.setDescription("Original Description");
		testEvent.setCreatedBy("TestUser");
		testEvent.setStartTime(LocalDateTime.of(2025, 9, 22, 10, 0));
		testEvent.setEndTime(LocalDateTime.of(2025, 9, 22, 11, 0));
		testEvent.setEvent_type("Meeting");

		updateEvent = new Event();
		updateEvent.setTitle("Updated Title");
		updateEvent.setDescription("Updated Description");
		updateEvent.setStartTime(LocalDateTime.of(2025, 9, 23, 14, 0));
		updateEvent.setEndTime(LocalDateTime.of(2025, 9, 23, 15, 0));
	}

	@Test
	void testUpdateEvent_Success() {
		when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
		when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

		Event result = eventService.updateEvent(1L, updateEvent);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getTitle()).isEqualTo("Updated Title");
		assertThat(result.getDescription()).isEqualTo("Updated Description");
		assertThat(result.getStartTime()).isEqualTo(LocalDateTime.of(2025, 9, 23, 14, 0));
		assertThat(result.getEndTime()).isEqualTo(LocalDateTime.of(2025, 9, 23, 15, 0));
		// CreatedBy should remain unchanged
		assertThat(result.getCreatedBy()).isEqualTo("TestUser");

		verify(eventRepository, times(1)).findById(1L);
		verify(eventRepository, times(1)).save(any(Event.class));
	}

	@Test
	void testUpdateEvent_PartialUpdate() {
		when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
		when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

		Event partialUpdate = new Event();
		partialUpdate.setTitle("New Title Only");
		partialUpdate.setDescription(null);
		partialUpdate.setStartTime(null);
		partialUpdate.setEndTime(null);

		Event result = eventService.updateEvent(1L, partialUpdate);

		assertThat(result.getTitle()).isEqualTo("New Title Only");
		// Description should be updated even if null is passed
		assertThat(result.getDescription()).isNull();

		verify(eventRepository, times(1)).findById(1L);
		verify(eventRepository, times(1)).save(any(Event.class));
	}

	@Test
	void testUpdateEvent_EventNotFound() {
		when(eventRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> eventService.updateEvent(999L, updateEvent))
				.isInstanceOf(java.util.NoSuchElementException.class);

		verify(eventRepository, times(1)).findById(999L);
		verify(eventRepository, times(0)).save(any(Event.class));
	}

	@Test
	void testUpdateEvent_PreservesCreatedBy() {
		when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
		when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

		updateEvent.setCreatedBy("DifferentUser");

		Event result = eventService.updateEvent(1L, updateEvent);

		// CreatedBy should not be updated from request
		assertThat(result.getCreatedBy()).isEqualTo("TestUser");

		verify(eventRepository, times(1)).findById(1L);
		verify(eventRepository, times(1)).save(any(Event.class));
	}

	@Test
	void testUpdateEvent_UpdatesAllFields() {
		when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
		when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

		Event result = eventService.updateEvent(1L, updateEvent);

		assertThat(result.getTitle()).isEqualTo("Updated Title");
		assertThat(result.getDescription()).isEqualTo("Updated Description");
		assertThat(result.getStartTime()).isEqualTo(updateEvent.getStartTime());
		assertThat(result.getEndTime()).isEqualTo(updateEvent.getEndTime());

		verify(eventRepository, times(1)).save(any(Event.class));
	}

	@Test
	void testSearchEventsByDateRange_Success() {
		LocalDate startDate = LocalDate.of(2025, 9, 20);
		LocalDate endDate = LocalDate.of(2025, 9, 25);
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

		Event event1 = new Event();
		event1.setId(1L);
		event1.setTitle("Event 1");
		event1.setStartTime(LocalDateTime.of(2025, 9, 22, 10, 0));

		Event event2 = new Event();
		event2.setId(2L);
		event2.setTitle("Event 2");
		event2.setStartTime(LocalDateTime.of(2025, 9, 23, 14, 0));

		List<Event> events = new ArrayList<>();
		events.add(event1);
		events.add(event2);

		when(eventRepository.findEventsByDateRange(startDateTime, endDateTime))
				.thenReturn(events);

		List<Event> result = eventService.searchEventsByDateRange(startDate, endDate);

		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getId()).isEqualTo(1L);
		assertThat(result.get(1).getId()).isEqualTo(2L);

		verify(eventRepository, times(1)).findEventsByDateRange(startDateTime, endDateTime);
	}

	@Test
	void testSearchEventsByDateRange_EmptyResult() {
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 1, 31);
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

		when(eventRepository.findEventsByDateRange(startDateTime, endDateTime))
				.thenReturn(new ArrayList<>());

		List<Event> result = eventService.searchEventsByDateRange(startDate, endDate);

		assertThat(result).isNotNull();
		assertThat(result).isEmpty();

		verify(eventRepository, times(1)).findEventsByDateRange(startDateTime, endDateTime);
	}

	@Test
	void testSearchEventsByDateRange_StartDateNull() {
		assertThatThrownBy(() -> eventService.searchEventsByDateRange(null, LocalDate.of(2025, 12, 31)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Start date and end date cannot be null");
	}

	@Test
	void testSearchEventsByDateRange_EndDateNull() {
		assertThatThrownBy(() -> eventService.searchEventsByDateRange(LocalDate.of(2025, 1, 1), null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Start date and end date cannot be null");
	}

	@Test
	void testSearchEventsByDateRange_StartDateAfterEndDate() {
		LocalDate startDate = LocalDate.of(2025, 12, 31);
		LocalDate endDate = LocalDate.of(2025, 1, 1);

		assertThatThrownBy(() -> eventService.searchEventsByDateRange(startDate, endDate))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Start date must be before or equal to end date");
	}

	@Test
	void testSearchEventsByDateRange_SameDateRange() {
		LocalDate date = LocalDate.of(2025, 9, 22);
		LocalDateTime startDateTime = date.atStartOfDay();
		LocalDateTime endDateTime = date.atTime(23, 59, 59);

		Event event = new Event();
		event.setId(1L);
		event.setTitle("Event on specific day");
		event.setStartTime(LocalDateTime.of(2025, 9, 22, 10, 0));

		List<Event> events = new ArrayList<>();
		events.add(event);

		when(eventRepository.findEventsByDateRange(startDateTime, endDateTime))
				.thenReturn(events);

		List<Event> result = eventService.searchEventsByDateRange(date, date);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getTitle()).isEqualTo("Event on specific day");

		verify(eventRepository, times(1)).findEventsByDateRange(startDateTime, endDateTime);
	}
}
