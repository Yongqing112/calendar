package com.calendar.config.spring_batch;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface CalendarImportGateway {

    @Gateway(requestChannel = "importEventChannel")
    void importEvents(String filePath);
}
