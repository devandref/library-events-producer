package io.github.devandref.library_events_producer.controller;

import io.github.devandref.library_events_producer.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
@EmbeddedKafka(topics = "library-events")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void postLibraryEvent() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE.toString());
        String jsonBody = "{\"libraryEventId\":1,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":1,\"bookName\":\"Nome do livro\",\"bookAuthor\":\"Autor do livro\"}}";
        var httpEntity = new HttpEntity<>(jsonBody, httpHeaders);
        var responseEntity = testRestTemplate.exchange("/v1/libraryevent", HttpMethod.POST, httpEntity, LibraryEvent.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

    }
}