package org.vrk.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.vrk.accounting.domain.Act;
import org.vrk.accounting.domain.Item;
import org.vrk.accounting.domain.ItemEmployee;
import org.vrk.accounting.domain.enums.ActType;
import org.vrk.accounting.domain.enums.ItemStatus;
import org.vrk.accounting.domain.enums.Role;
import org.vrk.accounting.util.file.FileUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
public class AccountingApplication {

    public static void main(String[] args) throws Exception {
//        ConfigurableApplicationContext ctx = SpringApplication.run(AccountingApplication.class, args);
//        FileUtil fileUtil = ctx.getBean(FileUtil.class);
//        Item officeChair = Item.builder()
//                .id(10L)
//                .isPersonal(false)
//                .name("Кресло офисное «Комфорт»")
//                .inventoryNumber("INV-1001")
//                .measuringUnit("шт.")
//                .count(2)
//                .cost("5.000")
//                .receiptDate(LocalDateTime.of(2024, 3, 15, 10, 0))
//                .serviceNumber("SC-2001")
//                .status(ItemStatus.IN_USE)
//                .photoFilename("comfort_chair.jpg")
//                .build();
//
//        Item directorChair = Item.builder()
//                .id(11L)
//                .isPersonal(false)
//                .name("Кресло директорское «Люкс»")
//                .inventoryNumber("INV-1002")
//                .measuringUnit("шт.")
//                .count(1)
//                .cost("5.000")
//                .receiptDate(LocalDateTime.of(2023, 11, 20, 9, 30))
//                .serviceNumber("SC-2002")
//                .status(ItemStatus.IN_USE)
//                .photoFilename("lux_chair.jpg")
//                .build();
//
//// Пример заполнения сущностей ItemEmployee
//        ItemEmployee member1 = ItemEmployee.builder()
//                .id(UUID.fromString("11111111-2222-3333-4444-555555555555"))
//                .snils("123-456-789 00")
//                .role(Role.ROLE_USER)
//                .pernr("000001")
//                .office("каб. 101")
//                .build();
//
//        ItemEmployee member2 = ItemEmployee.builder()
//                .id(UUID.fromString("66666666-7777-8888-9999-000000000000"))
//                .snils("987-654-321 00")
//                .role(Role.ROLE_USER)
//                .pernr("000002")
//                .office("каб. 102")
//                .build();
//
//        ItemEmployee member3 = ItemEmployee.builder()
//                .id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"))
//                .snils("555-666-777 88")
//                .role(Role.ROLE_USER)
//                .pernr("000003")
//                .office("каб. 103")
//                .build();
//
//        Act anotherAct = Act.builder()
//                .id(2L)
//                .type(ActType.FMU73)
//                .body(Map.of(   // В теле можно оставить дополнительную json-структуру при необходимости
//                        "organization", "Филиал «Северный» ОАО «Российские железные дороги»",
//                        "date", LocalDate.of(2025, 6, 15).toString(),
//                        "writeOffReason", "плановой амортизацией",
//                        "mainEngineer", "И.В. Иванова",
//                        "items", List.of(officeChair, directorChair),
//                        "commissionMembers", List.of(member1, member2, member3)
//                ))
//                .filePath("/uploads/FMU74_act_2.docx")
//                .build();
//        fileUtil.generateFMU73Act(anotherAct);
        SpringApplication.run(AccountingApplication.class, args);
    }

}
