package io.github.devandref.library_events_producer.controller;

import io.github.devandref.library_events_producer.domain.LibraryEvent;
import io.github.devandref.library_events_producer.producer.LibraryEventsProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
class LibraryEventsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LibraryEventsProducer libraryEventsProducer;

    String jsonBody = "{\"libraryEventId\":1,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":1,\"bookName\":\"Nome do livro\",\"bookAuthor\":\"Autor do livro\"}}";

    @Test
    void postLibraryEventsTest() throws Exception {
        when(libraryEventsProducer.sendLibraryEvent_approach3(isA(LibraryEvent.class)))
                .thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

}