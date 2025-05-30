package org.vrk.accounting.service.kafka.Producer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.kafka.Application;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ApplicationProducer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationProducer.class);
    private final KafkaTemplate<String, Application> kafkaTemplate;
    private static final String TOPIC = "applications";

    // TODO заявление ушло на согласование
    public void sendApplication(Application application) {
        String key = String.valueOf(application.getId());

        CompletableFuture<SendResult<String, Application>> future =
                kafkaTemplate.send(TOPIC, key, application);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Ошибка при отправке заявления: {}", application, ex);
            } else {
                log.info("Заявление успешно отправлено: {}", application);
            }
        });
    }
}
