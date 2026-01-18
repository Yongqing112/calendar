package com.calendar.config.spring_batch;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.calendar.domain.Event;

public class EventFieldSetMapper implements FieldSetMapper<Event> {

    @Override
    public Event mapFieldSet(FieldSet fieldSet){
        Event event = new Event();
        event.setCreatedBy(fieldSet.readString("createdBy"));
        event.setTitle(fieldSet.readString("title"));
        event.setDescription(fieldSet.readString("description"));
        event.setStartTime(fieldSet.readDate("startTime","yyyy-MM-dd HH:mm:ss").toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());
        event.setEndTime(fieldSet.readDate("endTime","yyyy-MM-dd HH:mm:ss").toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());
        event.setEvent_type(fieldSet.readString("event_type"));
        return event;
    }
}
