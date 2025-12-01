package com.calendar.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
}
