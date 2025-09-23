package com.calendar.repository;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.calendar.domain.Event;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class EventRepositoryTest {
	@Autowired
	private EventRepository repository;

	@Test
	void testSaveAndFindEvent(){
		Event event = new Event();
		event.setTitle("Title");
		event.setDescription("Description");
		event.setStartTime(LocalDateTime.of(2025, 9, 22, 10, 0));
		event.setEndTime(LocalDateTime.of(2025, 9, 22, 11, 0));
		event.setCreatedBy("Admin");

		Event saved = repository.save(event);

		assertThat(saved.getId()).isNotNull();

		Event fromDB = repository.findById(saved.getId()).get();
		assertThat(fromDB.getTitle()).isEqualTo(saved.getTitle());
		assertThat(fromDB.getDescription()).isEqualTo(saved.getDescription());
		assertThat(fromDB.getStartTime()).isEqualTo(saved.getStartTime());
		assertThat(fromDB.getEndTime()).isEqualTo(saved.getEndTime());
		assertThat(fromDB.getCreatedBy()).isEqualTo(saved.getCreatedBy());
	}
}
