package io.github.devandref.library_events_producer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.devandref.library_events_producer.domain.LibraryEvent;
import kafka.utils.TestUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
@EmbeddedKafka(topics = "library-events")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ObjectMapper objectMapper;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {
        var configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void teatDown() {
        consumer.close();
    }

    @Test
    void postLibraryEvent() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE.toString());
        String jsonBody = "{\"libraryEventId\":1,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":1,\"bookName\":\"Nome do livro\",\"bookAuthor\":\"Autor do livro\"}}";
        var httpEntity = new HttpEntity<>(jsonBody, httpHeaders);
        var responseEntity = testRestTemplate.exchange("/v1/libraryevent", HttpMethod.POST, httpEntity, LibraryEvent.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);
        assert consumerRecords.count() == 1;

    }
}