package org.vrk.accounting.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_id_seq")
    @SequenceGenerator(name = "inventory_id_seq", sequenceName = "inventory_id_seq", allocationSize = 1)
    private Long id;

    /**
     * Дата начала инвентаризации (плановая).
     */
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    /**
     * Дата окончания/проведения инвентаризации.
     * Проставляется после сохранения всех записей описи.
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * Материально-ответственное лицо (МОЛ), чье имущество проверяем.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_employee_id", nullable = false)
    private ItemEmployee responsibleEmployee;

    /**
     * Материально-ответственное лицо (МОЛ), чье имущество проверяем.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_chairman_employee_id", nullable = false)
    private ItemEmployee commissionChairman;

    /**
     * Инвентарная опись (результаты). Заполняется после проверки.
     */
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryList> inventoryLists = new ArrayList<>();
}
