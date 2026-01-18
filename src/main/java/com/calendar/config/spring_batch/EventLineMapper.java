package com.calendar.config.spring_batch;

import org.springframework.batch.item.file.mapping.DefaultLineMapper;

import com.calendar.domain.Event;

public class EventLineMapper extends DefaultLineMapper<Event> {

}
