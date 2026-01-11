package com.calendar.confug.spring_batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.calendar.domain.Event;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class ItemReader {
    @Bean
    public FlatFileItemReader<Event> calenderEventReader() {
        FlatFileItemReader<Event> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1); // Skip header line
        DefaultLineMapper<Event> lineMapper = new EventLineMapper();

        lineMapper.setLineTokenizer(new EventLineTokenizer());
        lineMapper.setFieldSetMapper(new EventFieldSetMapper());

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public ItemProcessor<Event, Event> calenderEventProcessor() {
        return event -> {
            if (event.getStartTime() == null || event.getEndTime() == null) {
                throw new IllegalArgumentException("startTime and endTime are required");
            }
            if (event.getStartTime().isAfter(event.getEndTime())) {
                throw new IllegalArgumentException("startTime must be before endTime");
            }
            return event;
        };
    }

    @Bean
    public JpaItemWriter<Event> calenderEventWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<Event> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    // TODO: Step, Job, Gateway, Flow, Controller
}
