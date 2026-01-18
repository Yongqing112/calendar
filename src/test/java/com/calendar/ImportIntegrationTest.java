package com.calendar;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.calendar.domain.Event;
import com.calendar.repository.EventRepository;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class ImportIntegrationTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importEventJob;

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void testImportEvents() throws Exception {
        // Create a temporary CSV file
        String csvContent = "createdBy,title,description,startTime,endTime,event_type\n" +
                            "Admin,Meeting,Team meeting,2025-09-22 10:00:00,2025-09-22 11:00:00,Meeting\n" +
                            "User,Conference,Annual conference,2025-09-23 14:00:00,2025-09-23 16:00:00,Conference\n";

        Path tempFile = Files.createTempFile("test-events", ".csv");
        Files.writeString(tempFile, csvContent);

        // Import the events
        jobLauncher.run(importEventJob, new JobParametersBuilder().addString("filePath", tempFile.toAbsolutePath().toString()).toJobParameters());

        // Verify events are saved
        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(2);

        Event event1 = events.stream().filter(e -> e.getTitle().equals("Meeting")).findFirst().orElse(null);
        assertThat(event1).isNotNull();
        assertThat(event1.getCreatedBy()).isEqualTo("Admin");
        assertThat(event1.getDescription()).isEqualTo("Team meeting");
        assertThat(event1.getStartTime()).isEqualTo(java.time.LocalDateTime.of(2025, 9, 22, 10, 0));
        assertThat(event1.getEndTime()).isEqualTo(java.time.LocalDateTime.of(2025, 9, 22, 11, 0));
        assertThat(event1.getEvent_type()).isEqualTo("Meeting");

        Event event2 = events.stream().filter(e -> e.getTitle().equals("Conference")).findFirst().orElse(null);
        assertThat(event2).isNotNull();
        assertThat(event2.getCreatedBy()).isEqualTo("User");
        assertThat(event2.getDescription()).isEqualTo("Annual conference");
        assertThat(event2.getStartTime()).isEqualTo(java.time.LocalDateTime.of(2025, 9, 23, 14, 0));
        assertThat(event2.getEndTime()).isEqualTo(java.time.LocalDateTime.of(2025, 9, 23, 16, 0));
        assertThat(event2.getEvent_type()).isEqualTo("Conference");

        // Clean up
        Files.deleteIfExists(tempFile);
    }
}