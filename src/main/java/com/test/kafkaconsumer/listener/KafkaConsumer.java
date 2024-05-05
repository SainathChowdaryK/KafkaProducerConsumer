package com.test.kafkaconsumer.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * The AMIHeadEndConsumer Class.
 */
@Service
public class KafkaConsumer {

	@KafkaListener(topics = {"test"}, groupId = "test-group", 
			containerFactory = "kafkaListenerContainerFactory")
	public void consumeMessages(@Payload final String recievedMessage, 
			@org.springframework.messaging.handler.annotation.Header(KafkaHeaders.OFFSET) final Integer offset,final Acknowledgment ack) {
		System.out.println("Consumed Message is " + recievedMessage + " - Offset :: " + offset);
		ack.acknowledge();
	}

}
