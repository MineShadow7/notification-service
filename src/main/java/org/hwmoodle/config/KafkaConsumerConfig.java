package org.hwmoodle.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.hwmoodle.core.dto.UserNotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, UserNotificationEvent> userEventConsumerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.kafka.consumer.group-id}") String groupId,
            @Value("${spring.kafka.consumer.auto-offset-reset}") String autoOffsetReset) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<UserNotificationEvent> deserializer = new JsonDeserializer<>(UserNotificationEvent.class);
        deserializer.addTrustedPackages("org.hwmoodle.core.dto");
        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UserNotificationEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, UserNotificationEvent> userEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, UserNotificationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userEventConsumerFactory);
        return factory;
    }
}
