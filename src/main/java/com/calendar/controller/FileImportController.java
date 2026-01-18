package com.calendar.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.calendar.config.spring_batch.CalendarImportGateway;

@Controller
@RequestMapping("/import")
public class FileImportController {

    private final CalendarImportGateway calendarImportGateway;

    @Autowired
    FileImportController(CalendarImportGateway calendarImportGateway) {
        this.calendarImportGateway = calendarImportGateway;
    }

    @PostMapping("/events")
    public ResponseEntity<String> importEvents(@RequestParam("file") MultipartFile file) {
        try {
            Path tempPath = Files.createTempFile("calendar-", ".csv");
            File tempFile = tempPath.toFile();

            file.transferTo(tempFile);

            calendarImportGateway.importEvents(tempFile.getAbsolutePath());

            return ResponseEntity.ok("File imported successfully");

        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to import file: " + e.getMessage());
        }

    }
}
