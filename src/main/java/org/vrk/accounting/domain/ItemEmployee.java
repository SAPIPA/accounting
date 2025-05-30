package org.vrk.accounting.domain;

import jakarta.persistence.*;
import lombok.*;
import org.vrk.accounting.domain.enums.Role;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Пользователи сервиса.
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_employee")
public class ItemEmployee {
    /**
     * GUID пользователя.
     */
    @Id
    @Column(name = "guid")
    private UUID id;
    /**
     * Снилс пользователя.
     */
    @Column(name = "snils", nullable = false)
    private String snils;
    /**
     * Роль пользователя в системе.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
    /**
     * Табельный номер пользователя.
     */
    @Column(name = "pernr", nullable = false)
    private String pernr;
    /**
     * Рабочее место пользователя.
     */
    @ManyToOne
    @JoinColumn(name = "workplace_id")
    private Place workplace;
    /**
     * Фактическое рабочее место пользователя.
     */
    @ManyToOne
    @JoinColumn(name = "fact_workplace_id")
    private Place factWorkplace;
    /**
     * Кабинет сотрудника.
     */
    @Column(name = "office")
    private String office;
    /**
     * Обратная связь с комиссией.
     */
    @ManyToMany(mappedBy = "commissionMembers")
    private Set<Inventory> inventories = new HashSet<>();
}
