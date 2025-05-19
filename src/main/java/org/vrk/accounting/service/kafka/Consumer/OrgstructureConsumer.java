package org.vrk.accounting.service.kafka.Consumer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Place;
import org.vrk.accounting.domain.kafka.Orgstructure;
import org.vrk.accounting.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class OrgstructureConsumer {

    private final PlaceRepository placeRepository;

    @KafkaListener(topics = "orgstructure-topic", groupId = "orgstructure-group")
    @Transactional
    public void consume(Orgstructure msg) {
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
