package com.test.kafkaconsumer.producer;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaProducer {

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	@RequestMapping("/send")
	public String sendMessage() {
		Random random = new Random();
		long nextLong = random.nextLong();
		kafkaTemplate.send("test", String.valueOf(nextLong), "Message-" + nextLong);
		System.out.println("Message sent to topic:: " + "Message-" + nextLong);
		return "Success";
	}
}
