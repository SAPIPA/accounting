package org.vrk.accounting.service.kafka.Consumer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.RZDEmployee;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.domain.kafka.Employee;
import org.vrk.accounting.repository.ItemEmployeeRepository;
import org.vrk.accounting.repository.PlaceRepository;
import org.vrk.accounting.repository.RZDEmployeeRepository;

@Service
@RequiredArgsConstructor
public class EmployeeConsumer {

    private final RZDEmployeeRepository rzdEmployeeRepository;
    private final ItemEmployeeRepository itemEmployeeRepository;
    private final PlaceRepository placeRepository;

    @KafkaListener(topics = "employee-topic", groupId = "employee-group")
    @Transactional
    public void consume(Employee msg) {
        RZDEmployee entity = RZDEmployee.builder()
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
        rzdEmployeeRepository.save(entity);

        ItemEmployee employee = ItemEmployee.builder()
                .id(msg.getInternalGuid())
                .snils(msg.getSnils())
                .pernr(msg.getPernr())
                .role(Role.ROLE_USER)
                .workplace(placeRepository.findByObjId(msg.getOrgId()))
                .build();
        itemEmployeeRepository.save(employee);
    }
}
