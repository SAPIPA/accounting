package org.vrk.accounting.service.kafka.Consumer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.Place;
import org.vrk.accounting.domain.RZDEmployee;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.domain.kafka.Employee;
import org.vrk.accounting.repository.ItemEmployeeRepository;
import org.vrk.accounting.repository.PlaceRepository;
import org.vrk.accounting.repository.RZDEmployeeRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmployeeConsumer.class);

    private final PlaceRepository placeRepo;
    private final RZDEmployeeRepository rzdRepo;
    private final ItemEmployeeRepository empRepo;

    @KafkaListener(
            topics = "employee-topic",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handleEmployee(Employee msg) {
        log.info("Получен объект Employee: {}", msg);
        RZDEmployee meta = RZDEmployee.builder()
                .snils(msg.getSnils())
                .sex(msg.getSex())
                .email(msg.getEmail())
                .workPhone(msg.getWorkPhone())
                .mobPhone(msg.getMobPhone())
                .midName(msg.getMidName())
                .firstName(msg.getFirstName())
                .lastName(msg.getLastName())
                .orgeh(msg.getOrgeh())
                .orgId(msg.getOrgId())
                .pernr(msg.getPernr())
                .internalGuid(msg.getInternalGuid())
                .plans(msg.getPlans())
                .plansId(msg.getPlansId())
                .build();
        rzdRepo.save(meta);

        // 2) Обновляем или создаём ItemEmployee
        UUID guid = msg.getInternalGuid();
        Optional<ItemEmployee> maybe = empRepo.findById(guid);

        if (maybe.isPresent()) {
            // существующий — просто обновляем поля, без save()
            ItemEmployee existing = maybe.get();
            existing.setSnils(msg.getSnils());
            existing.setPernr(msg.getPernr());
            existing.setRole(Role.ROLE_USER);
            Place pl = placeRepo.findByObjId(msg.getOrgId());
            existing.setWorkplace(pl);
            existing.setFactWorkplace(pl);
            // заметка: Hibernate сам «увидит» эти изменения и выполнит UPDATE
        } else {
            // новый — сохраняем
            ItemEmployee emp = ItemEmployee.builder()
                    .id(guid)
                    .snils(msg.getSnils())
                    .pernr(msg.getPernr())
                    .role(Role.ROLE_USER)
                    .workplace(placeRepo.findByObjId(msg.getOrgId()))
                    .factWorkplace(placeRepo.findByObjId(msg.getOrgId()))
                    .office("")  // или какое-то дефолтное значение
                    .build();
            empRepo.save(emp);
        }
    }
}