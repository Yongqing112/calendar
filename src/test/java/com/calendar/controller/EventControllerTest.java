package com.calendar.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.calendar.domain.Event;
import com.calendar.repository.EventRepository;
import com.calendar.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class EventControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private EventRepository eventRepository;

	@MockBean
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
		when(eventRepository.findAll())
				.thenReturn(Arrays.asList(testEvent));

		mockMvc.perform(get("/events")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].title").value("Test Event"));

		verify(eventRepository, times(1)).findAll();
	}

	@Test
	void testGetAllEvents_Empty() throws Exception {
		when(eventRepository.findAll())
				.thenReturn(Arrays.asList());

		mockMvc.perform(get("/events")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));

		verify(eventRepository, times(1)).findAll();
	}

	@Test
	void testCreateEvent_Success() throws Exception {
		when(eventRepository.save(any(Event.class)))
				.thenReturn(testEvent);

		mockMvc.perform(post("/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testEvent)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("Test Event"));

		verify(eventRepository, times(1)).save(any(Event.class));
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

		verify(eventRepository, times(0)).save(any(Event.class));
	}

	@Test
	void testGetEvent_Success() throws Exception {
		when(eventRepository.findById(1L))
				.thenReturn(Optional.of(testEvent));

		mockMvc.perform(get("/events/1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.title").value("Test Event"));

		verify(eventRepository, times(1)).findById(1L);
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
		doNothing().when(eventRepository).deleteById(1L);

		mockMvc.perform(delete("/events").param("id", "1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("刪除成功"));

		verify(eventRepository, times(1)).deleteById(1L);
	}

	@Test
	void testDeleteEvent_Failure() throws Exception {
		doThrow(new RuntimeException("Database error")).when(eventRepository).deleteById(999L);

		mockMvc.perform(delete("/events").param("id", "999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("刪除失敗，請確認id是否正確"));

		verify(eventRepository, times(1)).deleteById(999L);
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
				.thenReturn(Arrays.asList(event1, event2));

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
	void testSearchByDateRange_EmptyResult() throws Exception {
		when(eventService.searchEventsByDateRange(
				LocalDate.of(2025, 1, 1),
				LocalDate.of(2025, 1, 31)))
				.thenReturn(Arrays.asList());

		mockMvc.perform(get("/events/search")
				.param("startDate", "2025-01-01")
				.param("endDate", "2025-01-31")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));

		verify(eventService, times(1)).searchEventsByDateRange(
				LocalDate.of(2025, 1, 1),
				LocalDate.of(2025, 1, 31));
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
	void testSearchByDateRange_MissingStartDate() throws Exception {
		mockMvc.perform(get("/events/search")
				.param("endDate", "2025-09-25")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testSearchByDateRange_MissingEndDate() throws Exception {
		mockMvc.perform(get("/events/search")
				.param("startDate", "2025-09-20")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testSearchByDateRange_MissingBothDates() throws Exception {
		mockMvc.perform(get("/events/search")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testSearchByDateRange_InvalidDateFormat() throws Exception {
		mockMvc.perform(get("/events/search")
				.param("startDate", "2025/09/20")
				.param("endDate", "2025/09/25")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testSearchByDateRange_SameDateRange() throws Exception {
		Event event1 = new Event();
		event1.setId(1L);
		event1.setTitle("Event 1");
		event1.setStartTime(LocalDateTime.of(2025, 9, 22, 10, 0));

		when(eventService.searchEventsByDateRange(
				LocalDate.of(2025, 9, 22),
				LocalDate.of(2025, 9, 22)))
				.thenReturn(Arrays.asList(event1));

		mockMvc.perform(get("/events/search")
				.param("startDate", "2025-09-22")
				.param("endDate", "2025-09-22")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].title").value("Event 1"));

		verify(eventService, times(1)).searchEventsByDateRange(
				LocalDate.of(2025, 9, 22),
				LocalDate.of(2025, 9, 22));
	}

	@Test
	void testSearchByDateRange_SingleEvent() throws Exception {
		Event singleEvent = new Event();
		singleEvent.setId(1L);
		singleEvent.setTitle("Single Event");
		singleEvent.setStartTime(LocalDateTime.of(2025, 9, 22, 10, 0));

		when(eventService.searchEventsByDateRange(
				LocalDate.of(2025, 9, 20),
				LocalDate.of(2025, 9, 25)))
				.thenReturn(Arrays.asList(singleEvent));

		mockMvc.perform(get("/events/search")
				.param("startDate", "2025-09-20")
				.param("endDate", "2025-09-25")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].title").value("Single Event"));

		verify(eventService, times(1)).searchEventsByDateRange(
				LocalDate.of(2025, 9, 20),
				LocalDate.of(2025, 9, 25));
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

	@Test
	void testSearchByDateRange_NullParametersInService() throws Exception {
		when(eventService.searchEventsByDateRange(null, null))
				.thenThrow(new IllegalArgumentException("Start date and end date cannot be null"));

		mockMvc.perform(get("/events/search")
				.param("startDate", "")
				.param("endDate", "")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testSearchByDateRange_LargeDataSet() throws Exception {
		Event[] events = new Event[100];
		for (int i = 0; i < 100; i++) {
			Event event = new Event();
			event.setId((long) (i + 1));
			event.setTitle("Event " + (i + 1));
			int dayOffset = i / 24;
			int hour = (i % 24);
			event.setStartTime(LocalDateTime.of(2025, 9, 20 + dayOffset, hour, 0));
			events[i] = event;
		}

		when(eventService.searchEventsByDateRange(
				LocalDate.of(2025, 9, 20),
				LocalDate.of(2025, 10, 18)))
				.thenReturn(Arrays.asList(events));

		mockMvc.perform(get("/events/search")
				.param("startDate", "2025-09-20")
				.param("endDate", "2025-10-18")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(100))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[99].id").value(100));

		verify(eventService, times(1)).searchEventsByDateRange(
				LocalDate.of(2025, 9, 20),
				LocalDate.of(2025, 10, 18));
	}

	@Test
	void testSearchByDateRange_ControllerNullParametersDirect() {
		EventController controller = new EventController(eventRepository, eventService);
		ResponseEntity<?> resp = controller.searchByDateRange(null, null);
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		@SuppressWarnings("unchecked")
		Map<String, String> body = (Map<String, String>) resp.getBody();
		assertEquals("startDate and endDate parameters are required", body.get("error"));
	}
}
