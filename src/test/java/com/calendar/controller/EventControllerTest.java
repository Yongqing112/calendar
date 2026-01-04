package com.calendar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.calendar.domain.Event;
import com.calendar.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(EventController.class)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
	private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    private Event testEvent;
	@BeforeEach
	void setUp() {
		testEvent = new Event();
		testEvent.setId(1L);
		testEvent.setTitle("Test Event");
		testEvent.setDescription("This is a test event");
		testEvent.setCreatedBy("TestUser");
		testEvent.setStartTime(LocalDateTime.of(2025, 9, 22, 10, 0));
		testEvent.setEndTime(LocalDateTime.of(2025, 9, 22, 11, 0));
		testEvent.setEvent_type("Meeting");
	}

	@Test
	void testGetAllEvents_Success() throws Exception {
		when(eventService.findAll())
				.thenReturn(List.of(testEvent));

		mockMvc.perform(get("/events")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].title").value("Test Event"));

		verify(eventService, times(1)).findAll();
	}

    @Test
	void testCreateEvent_Success() throws Exception {
		when(eventService.save(any(Event.class)))
				.thenReturn(testEvent);

		mockMvc.perform(post("/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testEvent)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("Test Event"));

		verify(eventService, times(1)).save(any(Event.class));
	}


	@Test
	void testCreateEvent_NoCreatedBy() throws Exception {
		Event eventNoCreatedBy = new Event();
		eventNoCreatedBy.setTitle("Test Event");

		mockMvc.perform(post("/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventNoCreatedBy)))
				.andExpect(status().isUnauthorized())
				.andExpect(content().string("Does not login."));

		verify(eventService, times(0)).save(any(Event.class));
	}

	@Test
	void testGetEvent_Success() throws Exception {
		when(eventService.findById(1L))
				.thenReturn(testEvent);

		mockMvc.perform(get("/events/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("Test Event"));

		verify(eventService, times(1)).findById(1L);
	}

	@Test
	void testUpdateEvent_Success() throws Exception {
		when(eventService.updateEvent(eq(1L), any(Event.class)))
				.thenReturn(testEvent);

		Event updateEvent = new Event();
		updateEvent.setTitle("Updated Event");

		mockMvc.perform(put("/events/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateEvent)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("Test Event"));

		verify(eventService, times(1)).updateEvent(eq(1L), any(Event.class));
	}

	@Test
	void testDeleteEvent_Success() throws Exception {
		doNothing().when(eventService).deleteById(1L);

		mockMvc.perform(delete("/events").param("id", "1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("刪除成功"));

		verify(eventService, times(1)).deleteById(1L);
	}

    @Test
	void testDeleteEvent_Failure() throws Exception {
		doThrow(new RuntimeException("Database error")).when(eventService).deleteById(999L);

		mockMvc.perform(delete("/events").param("id", "999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("刪除失敗，請確認id是否正確"));

		verify(eventService, times(1)).deleteById(999L);
	}

	@Test
	void testSearchByDateRange_Success() throws Exception {
		Event event1 = new Event();
		event1.setId(1L);
		event1.setTitle("Event 1");
		event1.setStartTime(LocalDateTime.of(2025, 9, 22, 10, 0));

		Event event2 = new Event();
		event2.setId(2L);
		event2.setTitle("Event 2");
		event2.setStartTime(LocalDateTime.of(2025, 9, 23, 14, 0));

		when(eventService.searchEventsByDateRange(
				LocalDate.of(2025, 9, 20),
				LocalDate.of(2025, 9, 25)))
				.thenReturn(List.of(event1, event2));

		mockMvc.perform(get("/events/search")
				.param("startDate", "2025-09-20")
				.param("endDate", "2025-09-25")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].title").value("Event 1"))
				.andExpect(jsonPath("$[1].id").value(2))
				.andExpect(jsonPath("$[1].title").value("Event 2"));

		verify(eventService, times(1)).searchEventsByDateRange(
				LocalDate.of(2025, 9, 20),
				LocalDate.of(2025, 9, 25));
	}

	@Test
	void testSearchByDateRange_InvalidDateRange() throws Exception {
		when(eventService.searchEventsByDateRange(
				LocalDate.of(2025, 12, 31),
				LocalDate.of(2025, 1, 1)))
				.thenThrow(new IllegalArgumentException("Start date must be before or equal to end date"));

		mockMvc.perform(get("/events/search")
				.param("startDate", "2025-12-31")
				.param("endDate", "2025-01-01")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Start date must be before or equal to end date"));

		verify(eventService, times(1)).searchEventsByDateRange(
				LocalDate.of(2025, 12, 31),
				LocalDate.of(2025, 1, 1));
	}

	@Test
	void testSearchByDateRange_ServiceException() throws Exception {
		when(eventService.searchEventsByDateRange(
				LocalDate.of(2025, 9, 20),
				LocalDate.of(2025, 9, 25)))
				.thenThrow(new RuntimeException("Database connection error"));

		mockMvc.perform(get("/events/search")
				.param("startDate", "2025-09-20")
				.param("endDate", "2025-09-25")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.error").value("An error occurred while searching events"));

		verify(eventService, times(1)).searchEventsByDateRange(
				LocalDate.of(2025, 9, 20),
				LocalDate.of(2025, 9, 25));
	}
}
