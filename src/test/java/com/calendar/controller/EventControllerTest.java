package com.calendar.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
		testEvent.setDescription("Test Description");
		testEvent.setCreatedBy("TestUser");
		testEvent.setStartTime(LocalDateTime.of(2025, 9, 22, 10, 0));
		testEvent.setEndTime(LocalDateTime.of(2025, 9, 22, 11, 0));
		testEvent.setEvent_type("Meeting");
	}

	@Test
	void testGetAllEvents_Success() throws Exception {
		Event event2 = new Event();
		event2.setId(2L);
		event2.setTitle("Event 2");
		event2.setDescription("Description 2");
		event2.setCreatedBy("User2");
		event2.setStartTime(LocalDateTime.of(2025, 9, 23, 10, 0));
		event2.setEndTime(LocalDateTime.of(2025, 9, 23, 11, 0));

		when(eventRepository.findAll()).thenReturn(Arrays.asList(testEvent, event2));

		mockMvc.perform(get("/events").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].title").value("Test Event"))
				.andExpect(jsonPath("$[1].id").value(2L));

		verify(eventRepository, times(1)).findAll();
	}

	@Test
	void testGetAllEvents_Empty() throws Exception {
		when(eventRepository.findAll()).thenReturn(Arrays.asList());

		mockMvc.perform(get("/events").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));

		verify(eventRepository, times(1)).findAll();
	}

	@Test
	void testCreateEvent_Success() throws Exception {
		when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

		mockMvc.perform(post("/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(testEvent)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.title").value("Test Event"))
				.andExpect(jsonPath("$.createdBy").value("TestUser"));

		verify(eventRepository, times(1)).save(any(Event.class));
	}

	@Test
	void testCreateEvent_NoCreatedBy() throws Exception {
		Event eventNoUser = new Event();
		eventNoUser.setTitle("Test Event");
		eventNoUser.setDescription("Test Description");
		eventNoUser.setCreatedBy(null);
		eventNoUser.setStartTime(LocalDateTime.of(2025, 9, 22, 10, 0));
		eventNoUser.setEndTime(LocalDateTime.of(2025, 9, 22, 11, 0));

		mockMvc.perform(post("/events")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(eventNoUser)))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$").value("Does not login."));

		verify(eventRepository, times(0)).save(any(Event.class));
	}

	@Test
	void testGetEvent_Success() throws Exception {
		when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

		mockMvc.perform(get("/events/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.title").value("Test Event"))
				.andExpect(jsonPath("$.description").value("Test Description"))
				.andExpect(jsonPath("$.createdBy").value("TestUser"));

		verify(eventRepository, times(1)).findById(1L);
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
}
