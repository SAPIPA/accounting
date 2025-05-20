package org.vrk.accounting.service.kafka.Producer;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vrk.accounting.domain.kafka.Notification;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);
    private final KafkaTemplate<String, Notification> kafkaTemplate;
    private static final String TOPIC = "notifications";

    public void sendNotification(Notification notification) {
        String key = notification.getDestinationSnils();

        CompletableFuture<SendResult<String, Notification>> future =
                kafkaTemplate.send(TOPIC, key, notification);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Ошибка при отправке уведомления: {}", notification, ex);
            } else {
                log.info("Уведомление успешно отправлено: {}", notification);
            }
        });
    }
}