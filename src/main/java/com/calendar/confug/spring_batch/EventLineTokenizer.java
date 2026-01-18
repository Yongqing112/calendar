package com.calendar.confug.spring_batch;

import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class EventLineTokenizer extends DelimitedLineTokenizer {
    public EventLineTokenizer() {
        super();
        setNames(new String[] {
                "createdBy",
                "title",
                "description",
                "startTime",
                "endTime",
                "event_type"
        });
    }

}
