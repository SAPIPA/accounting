package org.vrk.accounting.service.kafka.Consumer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    @KafkaListener(topics = "item-topic", groupId = "item-group")
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
                        .receiptDate(msg.getReceiptDate())
                        .status(ItemStatus.IN_USE)
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
                    .receiptDate(msg.getReceiptDate())
                    .status(ItemStatus.IN_USE)
                    .responsible(responsible)
                    .build();
            itemRepository.save(bulk);
        }
    }
}
