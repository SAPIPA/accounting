package org.vrk.accounting.service.kafka.Producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.kafka.Notification;

@Service
public class NotificationProducer {

    private static final String TOPIC = "notification-topic";

    @Autowired
    private KafkaTemplate<String, Notification> kafkaTemplate;

    public void sendNotification(Notification notification) {
        kafkaTemplate.send(TOPIC, notification);
    }
}