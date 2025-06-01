package org.vrk.accounting.service.kafka.Consumer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.vrk.accounting.domain.Item;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.enums.ItemStatus;
import org.vrk.accounting.domain.kafka.ItemKafka;
import org.vrk.accounting.repository.ItemEmployeeRepository;
import org.vrk.accounting.repository.ItemRepository;

@Service
@RequiredArgsConstructor
public class ItemConsumer {

    private final ItemRepository itemRepository;
    private final ItemEmployeeRepository itemEmployeeRepository;

    private static final Logger log = LoggerFactory.getLogger(EmployeeConsumer.class);

    @KafkaListener(
            topics = "item-topic",
            containerFactory = "itemKafkaListenerContainerFactory"
    )
    @Transactional
    public void consume(ItemKafka msg) {
        ItemEmployee responsible = itemEmployeeRepository.findBySnils(msg.getSnils());
        if ("ШТ".equals(msg.getMeasuringUnit())) {
            for (int i = 0; i < msg.getCount(); i++) {
                Item one = Item.builder()
                        .inventoryNumber(msg.getInventoryNumber())
                        .serviceNumber(msg.getServiceNumber())
                        .name(msg.getName())
                        .isPersonal(false)
                        .measuringUnit(msg.getMeasuringUnit())
                        .count(1)
                        .cost(msg.getCost())
                        .receiptDate(msg.getReceiptDate())
                        .status(ItemStatus.ON_BALANCE)
                        .responsible(responsible)
                        .build();
                itemRepository.save(one);
            }
        } else {
            Item bulk = Item.builder()
                    .inventoryNumber(msg.getInventoryNumber())
                    .serviceNumber(msg.getServiceNumber())
                    .name(msg.getName())
                    .isPersonal(false)
                    .measuringUnit(msg.getMeasuringUnit())
                    .count(msg.getCount())
                    .cost(msg.getCost())
                    .receiptDate(msg.getReceiptDate())
                    .status(ItemStatus.ON_BALANCE)
                    .responsible(responsible)
                    .build();
            itemRepository.save(bulk);
        }
    }
}
