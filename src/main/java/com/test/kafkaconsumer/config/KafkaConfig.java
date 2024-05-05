package com.test.kafkaconsumer.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class KafkaConfig.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    /** The cmep env. */
    @Autowired
    private Environment cmepEnv;

    /**
     * Kafka listener container factory.
     *
     * @return the concurrent kafka listener container factory
     */
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setAckOnError(false);
        factory.setConcurrency(Integer.valueOf(cmepEnv.getProperty("kafka.parameters.concurrency")));
        //factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
        factory.setConsumerFactory(consumerFactory());
        factory.setStatefulRetry(true);
        return factory;
    }

    /**
     * Consumer factory.
     *
     * @return the consumer factory
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        final Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                cmepEnv.getProperty("kafka.parameters.bootstrap.servers"));
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                cmepEnv.getProperty("kafka.parameters.key.deserializer"));
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                cmepEnv.getProperty("kafka.parameters.value.deserializer"));
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                cmepEnv.getProperty("kafka.parameters.enable.auto.commit"));
        consumerProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,
                cmepEnv.getProperty("kafka.parameters.session.timeout.ms"));
        consumerProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,
                cmepEnv.getProperty("kafka.parameters.poll.interval"));
        consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                cmepEnv.getProperty("kafka.parameters.poll.records"));
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                cmepEnv.getProperty("kafka.parameters.auto.offset.reset"));
        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }

    /**
     * Header producer factory.
     *
     * @return the producer factory
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        final Map<String, Object> logProps = new HashMap<>();
        logProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                cmepEnv.getProperty("kafka.parameters.bootstrap.servers"));
        logProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        logProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(logProps);
    }

    /**
     * Kafka template.
     *
     * @return the kafka template
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<String, String>(producerFactory());
    }

    /**
     * Common object mapper.
     *
     * @return the object mapper
     */
    @Bean
    @Primary
    public ObjectMapper commonObjectMapper() {
        final ObjectMapper responseMapper = new ObjectMapper();
        responseMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        return responseMapper;
    }

}
