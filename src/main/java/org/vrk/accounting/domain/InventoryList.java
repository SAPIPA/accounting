package org.vrk.accounting.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Инвентарная опись.
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_inventory_list")
public class InventoryList {
    /**
     * ИД инвентарной описи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_inventory_list_id_seq")
    @SequenceGenerator(name = "item_inventory_list_id_seq", sequenceName = "item_inventory_list_id_seq", allocationSize = 1)
    private Long id;
    /**
     * Связанная инвентаризация.
     */
    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;
    /**
     * Связанное материальное средство.
     */
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    /**
     * Наличие материального средства.
     */
    @Column(name = "is_present")
    private boolean isPresent;
    /**
     * Комментарий.
     */
    @Column(name = "note")
    private String note;

}
