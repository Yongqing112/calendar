package com.calendar.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.calendar.confug.spring_batch.CalendarImportGateway;

@WebMvcTest(FileImportController.class)
public class FileImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalendarImportGateway calendarImportGateway;

    @Test
    public void testImportEventsSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());

        mockMvc.perform(multipart("/import/events").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("File imported successfully"));

        verify(calendarImportGateway).importEvents(anyString());
    }

    @Test
    public void testImportEventsFailure() throws Exception {
        FailingMockMultipartFile file = new FailingMockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());

        mockMvc.perform(multipart("/import/events").file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to import file")));
    }

    // Custom MockMultipartFile that throws IOException on transferTo
    private static class FailingMockMultipartFile extends MockMultipartFile {
        public FailingMockMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            super(name, originalFilename, contentType, content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            throw new IOException("Simulated transfer failure");
        }
    }
}