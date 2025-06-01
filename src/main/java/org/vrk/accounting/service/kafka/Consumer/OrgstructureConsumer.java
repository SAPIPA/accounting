package org.vrk.accounting.service.kafka.Consumer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Place;
import org.vrk.accounting.domain.kafka.Orgstructure;
import org.vrk.accounting.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class OrgstructureConsumer {

    private final PlaceRepository placeRepository;

    private static final Logger log = LoggerFactory.getLogger(OrgstructureConsumer.class);

    @KafkaListener(
            topics = "orgstructure-topic",
            containerFactory = "orgstructureKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleOrgstructure(Orgstructure msg) {
        log.info("Десериализованный Orgstructure: objId={}, sText='{}'", msg.getObjId(), msg.getSText());
        Place place = Place.builder()
                .objId(msg.getObjId())
                .sText(msg.getSText())
                .ztlc(msg.getZtlc())
                .regio(msg.getRegio())
                .ort01(msg.getOrt01())
                .stras(msg.getStras())
                .hausn(msg.getHausn())
                .build();
        placeRepository.save(place);
    }
}
