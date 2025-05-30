package org.vrk.accounting.service.kafka.Producer;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.kafka.Act;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ActProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);
    private final KafkaTemplate<String, Act> kafkaTemplate;
    private static final String TOPIC = "acts";

    public void sendAct(Act act) {
        String key = String.valueOf(act.getId());

        CompletableFuture<SendResult<String, Act>> future =
                kafkaTemplate.send(TOPIC, key, act);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Ошибка при отправке акта: {}", act, ex);
            } else {
                log.info("Акт успешно отправлено: {}", act);
            }
        });
    }
}
