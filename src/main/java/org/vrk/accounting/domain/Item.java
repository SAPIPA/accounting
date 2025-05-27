package org.vrk.accounting.domain;

import jakarta.persistence.*;
import lombok.*;
import org.vrk.accounting.domain.enums.ItemStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Материальное средство.
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_item")
public class Item {
    /**
     * ИД материального средства.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_id_seq")
    @SequenceGenerator(name = "item_id_seq", sequenceName = "item_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;
    /**
     * Является ли средство личным.
     */
    @Column(name = "is_personal", nullable = false)
    private Boolean isPersonal;
    /**
     * Наименование основного средства.
     */
    @Column(name = "name")
    private String name;
    /**
     * Инвентарный номер.
     */
    @Column(name = "inventory_number")
    private String inventoryNumber;
    /**
     * Единица измерения.
     */
    @Column(name = "measuring_unit", nullable = false)
    private String measuringUnit;
    /**
     * Количество в единицах измерения.
     */
    @Column(name = "count", nullable = false)
    private Integer count;
    /**
     * Дата поступления.
     */
    @Column(name = "receipt_date", nullable = false)
    private LocalDateTime receiptDate;
    /**
     * ШК-номер.
     */
    @Column(name = "service_number")
    private String serviceNumber;
    /**
     * Текущий статус.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ItemStatus status;
    /**
     * Имя файла-фото
     * */
    @Column(name = "photo_filename")
    private String photoFilename;
    /**
     * Ответственное лицо.
     */
    @ManyToOne
    @JoinColumn(name = "responsible_user_id")
    private ItemEmployee responsible;
    /**
     * Сотрудник, фактически пользующийся средством
     */
    @ManyToOne
    @JoinColumn(name = "current_user_id")
    private ItemEmployee currentItemEmployee;
    /**
     * Обратная связь с инвентарной описью.
     */
    @OneToMany(mappedBy = "item")
    private List<InventoryList> inventoryLists = new ArrayList<>();
}
