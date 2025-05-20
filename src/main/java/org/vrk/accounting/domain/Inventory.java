package org.vrk.accounting.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Процесс инвентаризации.
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_inventory")
public class Inventory {
    /**
     * ИД инвентаризации.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_id_seq")
    @SequenceGenerator(name = "inventory_id_seq", sequenceName = "inventory_id_seq", allocationSize = 1)
    private Long id;
    /**
     * Дата начала инвентаризации.
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;
    /**
     * Дата окончания инвентаризации.
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;
    /**
     * Список членов комиссии.
     */
    @ManyToMany
    @JoinTable(
            name = "inventory_user",
            joinColumns = @JoinColumn(name = "inventory_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<ItemEmployee> commissionMembers = new HashSet<>();
    /**
     * Инвентарная опись.
     */
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryList> inventoryLists = new ArrayList<>();
}
