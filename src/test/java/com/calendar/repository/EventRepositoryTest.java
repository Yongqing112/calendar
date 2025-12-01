package com.calendar.repository;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;

import com.calendar.domain.Event;

@DataJpaTest
@TestPropertySource(properties = {
	"spring.datasource.url=jdbc:h2:mem:testdb",
	"spring.datasource.driverClassName=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
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

	@Test
	void testFindAllEvents() {
		Event event1 = new Event();
		event1.setTitle("Event 1");
		event1.setDescription("Description 1");
		event1.setCreatedBy("User1");
		event1.setStartTime(LocalDateTime.of(2025, 9, 22, 10, 0));
		event1.setEndTime(LocalDateTime.of(2025, 9, 22, 11, 0));

		Event event2 = new Event();
		event2.setTitle("Event 2");
		event2.setDescription("Description 2");
		event2.setCreatedBy("User2");
		event2.setStartTime(LocalDateTime.of(2025, 9, 23, 14, 0));
		event2.setEndTime(LocalDateTime.of(2025, 9, 23, 15, 0));

		repository.save(event1);
		repository.save(event2);

		assertThat(repository.findAll()).hasSize(2);
	}

	@Test
	void testDeleteEvent() {
		Event event = new Event();
		event.setTitle("Event to Delete");
		event.setDescription("Will be deleted");
		event.setCreatedBy("User");
		event.setStartTime(LocalDateTime.of(2025, 9, 22, 10, 0));
		event.setEndTime(LocalDateTime.of(2025, 9, 22, 11, 0));

		Event saved = repository.save(event);
		Long id = saved.getId();

		repository.deleteById(id);

		assertThat(repository.findById(id)).isEmpty();
	}
}
