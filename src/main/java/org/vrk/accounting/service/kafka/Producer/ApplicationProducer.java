package org.vrk.accounting.service.kafka.Producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.kafka.Application;

@Service
public class ApplicationProducer {
    private static final String TOPIC = "application-topic";

    @Autowired
    private KafkaTemplate<String, Application> kafkaTemplate;
    public void send(Application application) {
        kafkaTemplate.send(TOPIC, application);
    }
}
