package com.calendar.config.spring_batch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.transaction.PlatformTransactionManager;

import com.calendar.domain.Event;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class ImportCalendarEventConfig {
    @Bean
    @StepScope
    public FlatFileItemReader<Event> calenderEventReader(@Value("#{stepExecution}") org.springframework.batch.core.StepExecution stepExecution) {
        String filePath = stepExecution.getJobExecution().getJobParameters().getString("filePath");
        FlatFileItemReader<Event> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(filePath));
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

    @Bean
    public Step importEventStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                FlatFileItemReader<Event> reader,
                                ItemProcessor<Event, Event> processor,
                                JpaItemWriter<Event> writer) {
        return new StepBuilder("importEventStep", jobRepository)
                .<Event, Event>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importEventJob(JobRepository jobRepository,
                              Step importEventStep) {
        return new JobBuilder("importEventJob", jobRepository)
                .start(importEventStep)
                .build();
    }

    @Bean
    public IntegrationFlow importEventFlow(JobLauncher jobLauncher, Job importEventJob){
        return IntegrationFlow.from("importEventChannel")
                .handle(message -> {
                    try {
                        String filePath = (String) message.getPayload();
                        jobLauncher.run(importEventJob, new JobParametersBuilder().addString("filePath", filePath).toJobParameters());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .get();
    }
    // TODO: Step, Job, Gateway, Flow, Controller
}
