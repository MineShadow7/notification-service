package org.hwmoodle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import org.awaitility.Awaitility;
import org.hwmoodle.core.dto.UserEventOperation;
import org.hwmoodle.core.dto.UserNotificationEvent;
import org.hwmoodle.core.model.NotificationEntity;
import org.hwmoodle.core.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
                "spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer",
                "spring.mail.host=127.0.0.1",
                "spring.mail.port=3025",
                "app.notification.sender-email=no-reply@test.local",
                "app.notification.topic=user-events",
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)

@EmbeddedKafka(partitions = 1, topics = {"user-events"})
class NotificationServiceIT {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup(3025, "127.0.0.1", "smtp"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void clearMailbox() {
        try {
            greenMail.purgeEmailFromAllMailboxes();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to purge GreenMail mailboxes", ex);
        }
        notificationRepository.deleteAll();
    }

    @Test
    void shouldSendMailViaApi() throws Exception {
        UserNotificationEvent request = new UserNotificationEvent(UserEventOperation.CREATED, "api-user@example.com");

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    MimeMessage[] messages = greenMail.getReceivedMessages();
                    assertThat(messages).hasSize(1);
                    assertThat(messages[0].getAllRecipients()[0].toString()).isEqualTo("api-user@example.com");
                    assertThat(messages[0].getSubject()).isEqualTo("Аккаунт успешно создан");
                    assertThat((String) messages[0].getContent())
                            .contains("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.");
                    assertThat(notificationRepository.count()).isEqualTo(1);
                    NotificationEntity saved = notificationRepository.findAll().get(0);
                    assertThat(saved.getEmail()).isEqualTo("api-user@example.com");
                    assertThat(saved.getSubject()).isEqualTo("Аккаунт успешно создан");
                });
    }

    @Test
    void shouldSendMailViaKafkaEvent() throws Exception {
        UserNotificationEvent event = new UserNotificationEvent(UserEventOperation.DELETED, "kafka-user@example.com");
        kafkaTemplate.send("user-events", objectMapper.writeValueAsString(event));

        Awaitility.await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    MimeMessage[] messages = greenMail.getReceivedMessages();
                    assertThat(messages).hasSize(1);
                    assertThat(messages[0].getAllRecipients()[0].toString()).isEqualTo("kafka-user@example.com");
                    assertThat(messages[0].getSubject()).isEqualTo("Аккаунт удалён");
                    assertThat((String) messages[0].getContent())
                            .contains("Здравствуйте! Ваш аккаунт был удалён.");
                    assertThat(notificationRepository.count()).isEqualTo(1);
                    NotificationEntity saved = notificationRepository.findAll().get(0);
                    assertThat(saved.getEmail()).isEqualTo("kafka-user@example.com");
                    assertThat(saved.getSubject()).isEqualTo("Аккаунт удалён");
                });
    }
}
